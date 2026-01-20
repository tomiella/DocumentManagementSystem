package at.bif.swen.batch.service;

import at.bif.swen.batch.entity.DocumentAccessStats;
import at.bif.swen.batch.repository.DocumentAccessStatsRepository;
import at.bif.swen.batch.xml.AccessEntryXml;
import at.bif.swen.batch.xml.AccessLogXml;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AccessLogBatchService {

    private static final Logger log = LoggerFactory.getLogger(AccessLogBatchService.class);

    private final DocumentAccessStatsRepository accessStatsRepository;

    @Value("${batch.input-folder:/data/access-logs/input}")
    private String inputFolder;

    @Value("${batch.file-pattern:access-*.xml}")
    private String filePattern;

    @Value("${batch.archive-folder:/data/access-logs/archive}")
    private String archiveFolder;

    @Value("${batch.enabled:true}")
    private boolean enabled;

    @Scheduled(cron = "${batch.cron:0 0 1 * * ?}")
    public void processAccessLogs() {
        if (!enabled) {
            log.info("Batch processing is disabled");
            return;
        }

        log.info("Starting access log batch processing from folder: {}", inputFolder);

        Path inputPath = Paths.get(inputFolder);
        if (!Files.exists(inputPath)) {
            log.warn("Input folder does not exist: {}", inputFolder);
            return;
        }

        Path archivePath = Paths.get(archiveFolder);
        try {
            Files.createDirectories(archivePath);
        } catch (IOException e) {
            log.error("Failed to create archive folder: {}", archiveFolder, e);
            return;
        }

        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + filePattern);

        try (Stream<Path> files = Files.list(inputPath)) {
            files.filter(Files::isRegularFile)
                    .filter(path -> matcher.matches(path.getFileName()))
                    .forEach(this::processFile);
        } catch (IOException e) {
            log.error("Error listing files in input folder", e);
        }

        log.info("Access log batch processing completed");
    }

    private void processFile(Path file) {
        log.info("Processing file: {}", file.getFileName());

        try {
            AccessLogXml accessLog = parseXmlFile(file);
            if (accessLog == null || accessLog.getEntries() == null) {
                log.warn("Empty or invalid XML file: {}", file.getFileName());
                archiveFile(file);
                return;
            }

            LocalDate date = parseDate(accessLog.getDate());
            if (date == null) {
                log.error("Invalid date format in file {}: {}", file.getFileName(), accessLog.getDate());
                archiveFile(file);
                return;
            }

            int successCount = 0;
            int errorCount = 0;

            for (AccessEntryXml entry : accessLog.getEntries()) {
                try {
                    processEntry(entry, date);
                    successCount++;
                } catch (Exception e) {
                    log.error("Error processing entry for document {}: {}",
                            entry.getDocumentId(), e.getMessage());
                    errorCount++;
                }
            }

            log.info("Processed {} entries successfully, {} errors from file: {}",
                    successCount, errorCount, file.getFileName());

            archiveFile(file);

        } catch (Exception e) {
            log.error("Error processing file: {}", file.getFileName(), e);
        }
    }

    private AccessLogXml parseXmlFile(Path file) {
        try {
            JAXBContext context = JAXBContext.newInstance(AccessLogXml.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (AccessLogXml) unmarshaller.unmarshal(file.toFile());
        } catch (JAXBException e) {
            log.error("Failed to parse XML file: {}", file.getFileName(), e);
            return null;
        }
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    @Transactional
    public void processEntry(AccessEntryXml entry, LocalDate date) {
        UUID documentId;
        try {
            documentId = UUID.fromString(entry.getDocumentId());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid document ID format: " + entry.getDocumentId());
        }

        if (!accessStatsRepository.documentExists(documentId)) {
            log.warn("Document not found, skipping: {}", documentId);
            return;
        }

        DocumentAccessStats stats = accessStatsRepository
                .findByDocumentIdAndAccessDate(documentId, date)
                .orElseGet(() -> DocumentAccessStats.builder()
                        .documentId(documentId)
                        .accessDate(date)
                        .accessCount(0)
                        .build());

        stats.setAccessCount(stats.getAccessCount() + entry.getCount());
        accessStatsRepository.save(stats);

        log.debug("Updated access stats for document {} on {}: count={}",
                documentId, date, stats.getAccessCount());
    }

    private void archiveFile(Path file) {
        try {
            Path archivePath = Paths.get(archiveFolder);
            Path targetPath = archivePath.resolve(file.getFileName());

            if (Files.exists(targetPath)) {
                String name = file.getFileName().toString();
                int dotIndex = name.lastIndexOf('.');
                String baseName = dotIndex > 0 ? name.substring(0, dotIndex) : name;
                String extension = dotIndex > 0 ? name.substring(dotIndex) : "";
                targetPath = archivePath.resolve(baseName + "_" + System.currentTimeMillis() + extension);
            }

            Files.move(file, targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Archived file to: {}", targetPath);
        } catch (IOException e) {
            log.error("Failed to archive file: {}", file.getFileName(), e);
        }
    }
}
