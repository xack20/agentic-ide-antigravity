package com.usermanagement.adapter.persistence.repository;

import com.usermanagement.adapter.persistence.document.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data MongoDB repository for UserDocument.
 */
@Repository
public interface SpringDataUserRepository extends MongoRepository<UserDocument, String> {

    Optional<UserDocument> findByEmailNormalized(String email);

    Optional<UserDocument> findByPhoneNormalized(String phone);

    Optional<UserDocument> findByUsername(String username);

    boolean existsByEmailNormalized(String email);

    boolean existsByPhoneNormalized(String phone);

    boolean existsByUsername(String username);
}
