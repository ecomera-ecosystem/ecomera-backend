package com.youssef.ecomera.domain.product.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @JsonValue
    private final String name;

    public static Optional<CategoryType> fromString(String value) {
        return Arrays.stream(values())
                .filter(ct -> ct.name().equalsIgnoreCase(value) || ct.getName().equalsIgnoreCase(value))
                .findFirst();
    }

    @JsonCreator
    public static CategoryType forValue(String value) {
        return fromString(value)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid category: " + value + ". Valid values are: " +
                                Arrays.stream(values())
                                        .map(CategoryType::getName)
                                        .collect(Collectors.joining(", "))
                ));
    }
}
