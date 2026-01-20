package at.bif.swen.rest.repository;

import at.bif.swen.rest.entity.DocumentAccessStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentAccessStatsRepository extends JpaRepository<DocumentAccessStats, Long> {

    Optional<DocumentAccessStats> findByDocumentIdAndAccessDate(UUID documentId, LocalDate accessDate);
}
