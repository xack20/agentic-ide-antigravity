package com.usermanagement.adapter.persistence.repository;

import com.usermanagement.adapter.persistence.document.AuditLogDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * Spring Data MongoDB repository for AuditLogDocument.
 */
@Repository
public interface SpringDataAuditLogRepository extends MongoRepository<AuditLogDocument, String> {

    List<AuditLogDocument> findByTargetIdOrderByTimestampDesc(String targetId);

    List<AuditLogDocument> findByActorIdOrderByTimestampDesc(String actorId);

    List<AuditLogDocument> findByActionAndTimestampAfterOrderByTimestampDesc(
            String action, Instant after);

    List<AuditLogDocument> findByTargetIdAndTimestampBetweenOrderByTimestampDesc(
            String targetId, Instant start, Instant end);
}
