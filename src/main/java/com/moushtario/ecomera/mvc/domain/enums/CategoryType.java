package com.moushtario.ecomera.mvc.domain.enums;

import lombok.RequiredArgsConstructor;
import lombok.Getter;

@RequiredArgsConstructor
@Getter
public enum CategoryType {

    ELECTRONICS("Electronics"),
    JEWELERY("Jewelery"),
    MENS_CLOTHING("Men's Clothing"),
    WOMENS_CLOTHING("Women's Clothing");



    private final String name;

}
