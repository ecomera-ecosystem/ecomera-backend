package com.moushtario.ecomera.auth.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CurrentUserDto {
    private String id, email, firstname, lastname, role, lastLogin, ipAddress;
}

