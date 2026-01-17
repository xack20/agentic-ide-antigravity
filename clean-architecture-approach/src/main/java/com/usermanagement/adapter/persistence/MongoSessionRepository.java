package com.usermanagement.adapter.persistence;

import com.usermanagement.adapter.persistence.document.SessionDocument;
import com.usermanagement.adapter.persistence.repository.SpringDataSessionRepository;
import com.usermanagement.application.port.output.SessionRepository;
import com.usermanagement.domain.entity.Session;
import com.usermanagement.domain.valueobject.UserId;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * MongoDB implementation of SessionRepository.
 */
@Repository
public class MongoSessionRepository implements SessionRepository {

    private final SpringDataSessionRepository springDataRepo;

    public MongoSessionRepository(SpringDataSessionRepository springDataRepo) {
        this.springDataRepo = springDataRepo;
    }

    @Override
    public Session save(Session session) {
        SessionDocument doc = toDocument(session);
        SessionDocument saved = springDataRepo.save(doc);
        return toEntity(saved);
    }

    @Override
    public Optional<Session> findById(String id) {
        return springDataRepo.findById(id).map(this::toEntity);
    }

    @Override
    public Optional<Session> findByRefreshTokenHash(String tokenHash) {
        return springDataRepo.findByRefreshTokenHash(tokenHash).map(this::toEntity);
    }

    @Override
    public List<Session> findByUserId(UserId userId) {
        return springDataRepo.findByUserId(userId.getValue()).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Session> findActiveByUserId(UserId userId) {
        return springDataRepo.findByUserIdAndRevokedAtIsNullAndExpiresAtAfter(
                userId.getValue(), Instant.now())
                .stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public int revokeAllByUserId(UserId userId) {
        List<SessionDocument> sessions = springDataRepo.findByUserIdAndRevokedAtIsNullAndExpiresAtAfter(
                userId.getValue(), Instant.now());

        Instant now = Instant.now();
        for (SessionDocument session : sessions) {
            session.setRevokedAt(now);
            springDataRepo.save(session);
        }

        return sessions.size();
    }

    @Override
    public int deleteExpired() {
        List<SessionDocument> expired = springDataRepo.findByExpiresAtBefore(Instant.now());
        springDataRepo.deleteAll(expired);
        return expired.size();
    }

    @Override
    public void deleteById(String id) {
        springDataRepo.deleteById(id);
    }

    private SessionDocument toDocument(Session session) {
        SessionDocument doc = new SessionDocument();
        doc.setId(session.getId());
        doc.setUserId(session.getUserId().getValue());
        doc.setRefreshTokenHash(session.getRefreshTokenHash());
        doc.setAccessTokenJti(session.getAccessTokenJti());
        doc.setIpAddress(session.getIpAddress());
        doc.setUserAgent(session.getUserAgent());
        doc.setDeviceFingerprint(session.getDeviceFingerprint());
        doc.setCreatedAt(session.getCreatedAt());
        doc.setExpiresAt(session.getExpiresAt());
        doc.setRevokedAt(session.getRevokedAt());
        return doc;
    }

    private Session toEntity(SessionDocument doc) {
        return Session.reconstitute(
                doc.getId(),
                UserId.of(doc.getUserId()),
                doc.getRefreshTokenHash(),
                doc.getAccessTokenJti(),
                doc.getIpAddress(),
                doc.getUserAgent(),
                doc.getDeviceFingerprint(),
                doc.getCreatedAt(),
                doc.getExpiresAt(),
                doc.getRevokedAt());
    }
}
