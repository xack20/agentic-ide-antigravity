package com.usermanagement.adapter.persistence.repository;

import com.usermanagement.adapter.persistence.document.SessionDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for SessionDocument.
 */
@Repository
public interface SpringDataSessionRepository extends MongoRepository<SessionDocument, String> {

    Optional<SessionDocument> findByRefreshTokenHash(String tokenHash);

    List<SessionDocument> findByUserId(String userId);

    List<SessionDocument> findByUserIdAndRevokedAtIsNullAndExpiresAtAfter(String userId, Instant now);

    List<SessionDocument> findByExpiresAtBefore(Instant now);
}
