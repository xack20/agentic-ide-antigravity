package com.usermanagement.adapter.web.request;

import com.usermanagement.domain.enums.DeactivationReason;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for deactivating a user.
 */
public record DeactivateRequest(
        @NotNull(message = "Reason is required") DeactivationReason reason,

        String evidence) {
}
