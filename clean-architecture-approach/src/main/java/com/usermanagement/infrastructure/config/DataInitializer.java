package com.usermanagement.infrastructure.config;

import com.usermanagement.adapter.persistence.document.RoleDocument;
import com.usermanagement.adapter.persistence.repository.SpringDataRoleRepository;
import com.usermanagement.domain.enums.Permission;
import com.usermanagement.domain.enums.RoleName;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Initializes default data for the application.
 */
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initRoles(SpringDataRoleRepository roleRepository) {
        return args -> {
            // Check if roles already exist
            if (roleRepository.count() > 0) {
                return;
            }

            // Create default USER role
            RoleDocument userRole = new RoleDocument();
            userRole.setId(UUID.randomUUID().toString());
            userRole.setName(RoleName.USER.name());
            userRole.setDisplayName("User");
            userRole.setDescription("Standard user role with basic permissions");
            userRole.setPermissions(Set.of(
                    Permission.USER_READ.name()));
            userRole.setPrivilegeLevel(RoleName.USER.getPrivilegeLevel());
            userRole.setActive(true);
            userRole.setSystemRole(true);
            userRole.setDefaultRole(true);
            userRole.setCreatedAt(Instant.now());
            roleRepository.save(userRole);

            // Create ADMIN role
            RoleDocument adminRole = new RoleDocument();
            adminRole.setId(UUID.randomUUID().toString());
            adminRole.setName(RoleName.ADMIN.name());
            adminRole.setDisplayName("Administrator");
            adminRole.setDescription("Administrator role with user management permissions");
            adminRole.setPermissions(Set.of(
                    Permission.USER_READ.name(),
                    Permission.USER_WRITE.name(),
                    Permission.USER_STATUS_MANAGE.name(),
                    Permission.ROLE_READ.name(),
                    Permission.ROLE_MANAGE.name(),
                    Permission.AUDIT_READ.name()));
            adminRole.setPrivilegeLevel(RoleName.ADMIN.getPrivilegeLevel());
            adminRole.setActive(true);
            adminRole.setSystemRole(true);
            adminRole.setDefaultRole(false);
            adminRole.setCreatedAt(Instant.now());
            roleRepository.save(adminRole);

            // Create SUPER_ADMIN role
            RoleDocument superAdminRole = new RoleDocument();
            superAdminRole.setId(UUID.randomUUID().toString());
            superAdminRole.setName(RoleName.SUPER_ADMIN.name());
            superAdminRole.setDisplayName("Super Administrator");
            superAdminRole.setDescription("Super administrator with all permissions");
            superAdminRole.setPermissions(Arrays.stream(Permission.values())
                    .map(Permission::name)
                    .collect(Collectors.toSet()));
            superAdminRole.setPrivilegeLevel(RoleName.SUPER_ADMIN.getPrivilegeLevel());
            superAdminRole.setActive(true);
            superAdminRole.setSystemRole(true);
            superAdminRole.setDefaultRole(false);
            superAdminRole.setCreatedAt(Instant.now());
            roleRepository.save(superAdminRole);

            System.out.println("✓ Default roles initialized: USER, ADMIN, SUPER_ADMIN");
        };
    }
}
