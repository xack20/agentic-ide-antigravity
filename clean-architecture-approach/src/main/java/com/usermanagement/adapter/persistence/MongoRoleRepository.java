package com.usermanagement.adapter.persistence;

import com.usermanagement.adapter.persistence.document.RoleDocument;
import com.usermanagement.adapter.persistence.repository.SpringDataRoleRepository;
import com.usermanagement.application.port.output.RoleRepository;
import com.usermanagement.domain.entity.Role;
import com.usermanagement.domain.entity.RolePrerequisites;
import com.usermanagement.domain.enums.Permission;
import com.usermanagement.domain.enums.RoleName;
import com.usermanagement.domain.enums.UserStatus;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * MongoDB implementation of RoleRepository.
 */
@Repository
public class MongoRoleRepository implements RoleRepository {

        private final SpringDataRoleRepository springDataRepo;

        public MongoRoleRepository(SpringDataRoleRepository springDataRepo) {
                this.springDataRepo = springDataRepo;
        }

        @Override
        public Optional<Role> findById(String id) {
                return springDataRepo.findById(id).map(this::toEntity);
        }

        @Override
        public Optional<Role> findByName(RoleName name) {
                return springDataRepo.findByName(name.name()).map(this::toEntity);
        }

        @Override
        public List<Role> findAll() {
                return springDataRepo.findAll().stream()
                                .map(this::toEntity)
                                .collect(Collectors.toList());
        }

        @Override
        public List<Role> findByIds(Iterable<String> ids) {
                return StreamSupport.stream(springDataRepo.findAllById(ids).spliterator(), false)
                                .map(this::toEntity)
                                .collect(Collectors.toList());
        }

        @Override
        public Role save(Role role) {
                RoleDocument doc = toDocument(role);
                RoleDocument saved = springDataRepo.save(doc);
                return toEntity(saved);
        }

        @Override
        public Role getDefaultRole() {
                // Find role marked as default, or fall back to USER role
                return springDataRepo.findByIsDefaultRoleTrue()
                                .map(this::toEntity)
                                .or(() -> findByName(RoleName.USER))
                                .orElseGet(this::createDefaultUserRole);
        }

        private Role createDefaultUserRole() {
                // Create and save default USER role if it doesn't exist
                Role userRole = Role.create(
                                UUID.randomUUID().toString(),
                                RoleName.USER,
                                "Default user role",
                                Set.of() // No special permissions for basic users
                );
                return save(userRole);
        }

        private Role toEntity(RoleDocument doc) {
                Set<Permission> permissions = doc.getPermissions() != null
                                ? doc.getPermissions().stream()
                                                .map(Permission::valueOf)
                                                .collect(Collectors.toSet())
                                : new HashSet<>();

                Set<RoleName> excludesWith = doc.getExcludesWith() != null
                                ? doc.getExcludesWith().stream()
                                                .map(RoleName::valueOf)
                                                .collect(Collectors.toSet())
                                : new HashSet<>();

                RolePrerequisites prerequisites = RolePrerequisites.none();
                if (doc.getPrerequisites() != null) {
                        RoleDocument.PrerequisitesDocument prereqDoc = doc.getPrerequisites();
                        prerequisites = RolePrerequisites.of(
                                        prereqDoc.requiredStatus() != null
                                                        ? UserStatus.valueOf(prereqDoc.requiredStatus())
                                                        : null,
                                        prereqDoc.requiresEmailVerified(),
                                        prereqDoc.requiresPhoneVerified());
                }

                return Role.reconstitute(
                                doc.getId(),
                                RoleName.valueOf(doc.getName()),
                                doc.getDisplayName(),
                                doc.getDescription(),
                                doc.getPrivilegeLevel(),
                                permissions,
                                prerequisites,
                                excludesWith,
                                doc.isRequiresDualApproval(),
                                doc.getCreatedAt(),
                                doc.getUpdatedAt());
        }

        private RoleDocument toDocument(Role role) {
                RoleDocument doc = new RoleDocument();
                doc.setId(role.getId());
                doc.setName(role.getName().name());
                doc.setDisplayName(role.getDisplayName());
                doc.setDescription(role.getDescription());
                doc.setPrivilegeLevel(role.getPrivilegeLevel());

                doc.setPermissions(role.getPermissions().stream()
                                .map(Enum::name)
                                .collect(Collectors.toSet()));

                doc.setExcludesWith(role.getExcludesWith().stream()
                                .map(Enum::name)
                                .collect(Collectors.toSet()));

                RolePrerequisites prereq = role.getPrerequisites();
                if (prereq != null) {
                        doc.setPrerequisites(new RoleDocument.PrerequisitesDocument(
                                        prereq.getRequiredStatus() != null ? prereq.getRequiredStatus().name() : null,
                                        prereq.isRequiresEmailVerified(),
                                        prereq.isRequiresPhoneVerified()));
                }

                doc.setRequiresDualApproval(role.requiresDualApproval());
                doc.setCreatedAt(role.getCreatedAt());
                doc.setUpdatedAt(role.getUpdatedAt());

                return doc;
        }
}
