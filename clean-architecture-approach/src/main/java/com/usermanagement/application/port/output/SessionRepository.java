package com.usermanagement.application.port.output;

import com.usermanagement.domain.entity.Session;
import com.usermanagement.domain.valueobject.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Output port for session persistence operations.
 */
public interface SessionRepository {

    /**
     * Saves a session.
     */
    Session save(Session session);

    /**
     * Finds a session by ID.
     */
    Optional<Session> findById(String id);

    /**
     * Finds a session by refresh token hash.
     */
    Optional<Session> findByRefreshTokenHash(String tokenHash);

    /**
     * Finds all sessions for a user.
     */
    List<Session> findByUserId(UserId userId);

    /**
     * Finds all active (non-revoked, non-expired) sessions for a user.
     */
    List<Session> findActiveByUserId(UserId userId);

    /**
     * Revokes all sessions for a user.
     * 
     * @return number of sessions revoked
     */
    int revokeAllByUserId(UserId userId);

    /**
     * Deletes expired sessions.
     * 
     * @return number of sessions deleted
     */
    int deleteExpired();

    /**
     * Deletes a session.
     */
    void deleteById(String id);
}
