package com.youssef.ecomera.domain.product.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum CategoryType {

    @Schema(description = "Electronic products like laptops, phones")
    ELECTRONICS("Electronics"),
    @Schema(description = "Jewelry")
    JEWELRY("Jewelry"),
    @Schema(description = "Men's Clothing")
    MENS_CLOTHING("Men's Clothing"),
    @Schema(description = "Women's Clothing")
    WOMENS_CLOTHING("Women's Clothing");


    private final String name;

    public static Optional<CategoryType> fromString(String value) {
        return Arrays.stream(values())
                .filter(ct -> ct.name().equalsIgnoreCase(value) || ct.getName().equalsIgnoreCase(value))
                .findFirst();
    }

}
