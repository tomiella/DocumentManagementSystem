package at.bif.swen.batch.repository;

import at.bif.swen.batch.entity.DocumentAccessStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentAccessStatsRepository extends JpaRepository<DocumentAccessStats, Long> {

    Optional<DocumentAccessStats> findByDocumentIdAndAccessDate(UUID documentId, LocalDate accessDate);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM documents WHERE id = :documentId)", nativeQuery = true)
    boolean documentExists(UUID documentId);
}
