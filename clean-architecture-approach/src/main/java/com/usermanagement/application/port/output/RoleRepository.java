package com.usermanagement.application.port.output;

import com.usermanagement.domain.entity.Role;
import com.usermanagement.domain.enums.RoleName;

import java.util.List;
import java.util.Optional;

/**
 * Output port for role persistence operations.
 */
public interface RoleRepository {

    /**
     * Finds a role by ID.
     */
    Optional<Role> findById(String id);

    /**
     * Finds a role by name.
     */
    Optional<Role> findByName(RoleName name);

    /**
     * Gets all roles.
     */
    List<Role> findAll();

    /**
     * Finds roles by IDs.
     */
    List<Role> findByIds(Iterable<String> ids);

    /**
     * Saves a role.
     */
    Role save(Role role);

    /**
     * Gets the default role for new users.
     */
    Role getDefaultRole();
}
