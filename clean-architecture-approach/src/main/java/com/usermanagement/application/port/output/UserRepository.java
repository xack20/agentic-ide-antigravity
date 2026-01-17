package com.usermanagement.application.port.output;

import com.usermanagement.domain.entity.User;
import com.usermanagement.domain.valueobject.Email;
import com.usermanagement.domain.valueobject.Phone;
import com.usermanagement.domain.valueobject.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Output port for user persistence operations.
 * Implementation provided by infrastructure layer.
 */
public interface UserRepository {

    /**
     * Saves a user (insert or update).
     */
    User save(User user);

    /**
     * Finds a user by ID.
     */
    Optional<User> findById(UserId id);

    /**
     * Finds a user by ID string.
     */
    Optional<User> findById(String id);

    /**
     * Finds a user by email.
     */
    Optional<User> findByEmail(Email email);

    /**
     * Finds a user by normalized email string.
     */
    Optional<User> findByEmail(String normalizedEmail);

    /**
     * Finds a user by phone.
     */
    Optional<User> findByPhone(Phone phone);

    /**
     * Finds a user by normalized phone string.
     */
    Optional<User> findByPhone(String normalizedPhone);

    /**
     * Finds a user by username.
     */
    Optional<User> findByUsername(String username);

    /**
     * Checks if an email already exists.
     */
    boolean existsByEmail(String normalizedEmail);

    /**
     * Checks if a phone already exists.
     */
    boolean existsByPhone(String normalizedPhone);

    /**
     * Checks if a username already exists.
     */
    boolean existsByUsername(String username);

    /**
     * Deletes a user by ID.
     */
    void deleteById(UserId id);

    /**
     * Searches users with pagination and filtering.
     */
    UserSearchResult search(UserSearchCriteria criteria);

    /**
     * Counts users matching criteria.
     */
    long count(UserSearchCriteria criteria);
}
