package at.bif.swen.rest.service;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class AccessTrackingService {

    private final Map<UUID, Integer> accessCounts = new ConcurrentHashMap<>();

    @Value("${access.output-folder:/data/access-logs/input}")
    private String outputFolder;

    public void recordAccess(UUID documentId) {
        accessCounts.merge(documentId, 1, Integer::sum);
        exportAccessLogs();
        log.info("Recorded access for document {}, total today: {}", documentId, accessCounts.get(documentId));
    }

    public void exportAccessLogs() {
        LocalDate now = LocalDate.now();
        log.info("Exporting access logs for {} with {} documents", now, accessCounts.size());

        try {
            writeAccessLogXml(now, new HashMap<>(accessCounts));
            log.info("Successfully exported access log for {}", now);
        } catch (Exception e) {
            log.error("Failed to export access log for {}", now, e);
        }
    }

    private void writeAccessLogXml(LocalDate date, Map<UUID, Integer> counts) throws IOException, JAXBException {
        Path outputPath = Paths.get(outputFolder);
        Files.createDirectories(outputPath);

        String filename = String.format("access-%s.xml", date.format(DateTimeFormatter.ISO_LOCAL_DATE));
        Path filePath = outputPath.resolve(filename);

        AccessLogXml accessLog = new AccessLogXml();
        accessLog.setDate(date.format(DateTimeFormatter.ISO_LOCAL_DATE));

        List<AccessEntryXml> entries = new ArrayList<>();
        for (Map.Entry<UUID, Integer> entry : counts.entrySet()) {
            AccessEntryXml xmlEntry = new AccessEntryXml();
            xmlEntry.setDocumentId(entry.getKey().toString());
            xmlEntry.setCount(entry.getValue());
            entries.add(xmlEntry);
        }
        accessLog.setEntries(entries);

        JAXBContext context = JAXBContext.newInstance(AccessLogXml.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(accessLog, filePath.toFile());

        log.info("Wrote access log to {}", filePath);
    }

    @XmlRootElement(name = "accessLog")
    @XmlAccessorType(XmlAccessType.FIELD)
    @Getter
    @Setter
    public static class AccessLogXml {
        @XmlAttribute(name = "date", required = true)
        private String date;

        @XmlElement(name = "access")
        private List<AccessEntryXml> entries;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @Getter
    @Setter
    public static class AccessEntryXml {
        @XmlElement(name = "documentId", required = true)
        private String documentId;

        @XmlElement(name = "count", required = true)
        private int count;
    }
}
