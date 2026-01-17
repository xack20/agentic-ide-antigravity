package com.usermanagement.adapter.persistence.repository;

import com.usermanagement.adapter.persistence.document.RoleDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data MongoDB repository for RoleDocument.
 */
@Repository
public interface SpringDataRoleRepository extends MongoRepository<RoleDocument, String> {

    Optional<RoleDocument> findByName(String name);

    Optional<RoleDocument> findByIsDefaultRoleTrue();
}
