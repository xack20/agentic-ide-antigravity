package com.usermanagement.adapter.persistence;

import com.usermanagement.adapter.persistence.document.AuditLogDocument;
import com.usermanagement.adapter.persistence.repository.SpringDataAuditLogRepository;
import com.usermanagement.application.port.output.AuditLogRepository;
import com.usermanagement.domain.entity.AuditLog;
import com.usermanagement.domain.entity.AuditMetadata;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MongoDB implementation of AuditLogRepository.
 */
@Repository
public class MongoAuditLogRepository implements AuditLogRepository {

    private final SpringDataAuditLogRepository springDataRepo;

    public MongoAuditLogRepository(SpringDataAuditLogRepository springDataRepo) {
        this.springDataRepo = springDataRepo;
    }

    @Override
    public AuditLog save(AuditLog auditLog) {
        AuditLogDocument doc = toDocument(auditLog);
        AuditLogDocument saved = springDataRepo.save(doc);
        return toEntity(saved);
    }

    @Override
    public List<AuditLog> findByTargetId(String targetId, int limit) {
        return springDataRepo.findByTargetIdOrderByTimestampDesc(targetId)
                .stream()
                .limit(limit)
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditLog> findByActorId(String actorId, int limit) {
        return springDataRepo.findByActorIdOrderByTimestampDesc(actorId)
                .stream()
                .limit(limit)
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditLog> findByAction(String action, Instant after, int limit) {
        return springDataRepo.findByActionAndTimestampAfterOrderByTimestampDesc(action, after)
                .stream()
                .limit(limit)
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditLog> findByTargetIdAndTimeRange(String targetId, Instant start, Instant end) {
        return springDataRepo.findByTargetIdAndTimestampBetweenOrderByTimestampDesc(targetId, start, end)
                .stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    private AuditLogDocument toDocument(AuditLog log) {
        AuditLogDocument doc = new AuditLogDocument();
        doc.setId(log.getId());
        doc.setAction(log.getAction());
        doc.setActorId(log.getActorId());
        doc.setTargetId(log.getTargetId());
        doc.setTargetType(log.getTargetType());
        doc.setOldValues(log.getOldValues());
        doc.setNewValues(log.getNewValues());
        doc.setReason(log.getReason());

        if (log.getMetadata() != null) {
            doc.setIpAddress(log.getMetadata().getIpAddress());
            doc.setUserAgent(log.getMetadata().getUserAgent());
            doc.setCorrelationId(log.getMetadata().getCorrelationId());
        }

        doc.setTimestamp(log.getTimestamp());
        return doc;
    }

    private AuditLog toEntity(AuditLogDocument doc) {
        return AuditLog.reconstitute(
                doc.getId(),
                doc.getAction(),
                doc.getActorId(),
                doc.getTargetId(),
                doc.getTargetType(),
                doc.getOldValues(),
                doc.getNewValues(),
                doc.getReason(),
                AuditMetadata.of(doc.getIpAddress(), doc.getUserAgent(), doc.getCorrelationId()),
                doc.getTimestamp());
    }
}
