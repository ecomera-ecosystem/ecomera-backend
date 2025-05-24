package com.moushtario.ecomera.mvc.domain.enums;

import lombok.RequiredArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
@Getter
public enum CategoryType {

    ELECTRONICS("Electronics"),
    JEWELERY("Jewelery"),
    MENS_CLOTHING("Men's Clothing"),
    WOMENS_CLOTHING("Women's Clothing");



    private final String name;

    public static Optional<CategoryType> fromString(String value) {
        return Arrays.stream(values())
                .filter(ct -> ct.name().equalsIgnoreCase(value) || ct.getName().equalsIgnoreCase(value))
                .findFirst();
    }

}
