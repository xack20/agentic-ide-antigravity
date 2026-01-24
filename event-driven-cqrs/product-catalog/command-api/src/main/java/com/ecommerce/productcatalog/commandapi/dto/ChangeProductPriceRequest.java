package com.ecommerce.productcatalog.commandapi.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ChangeProductPriceRequest {

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.00", inclusive = true, message = "Price must be greater than or equal to 0")
    private BigDecimal price;

    @NotBlank(message = "Currency is required")
    private String currency;
}
