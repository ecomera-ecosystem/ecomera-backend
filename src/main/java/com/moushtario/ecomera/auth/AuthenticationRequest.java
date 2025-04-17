package com.moushtario.ecomera.auth;

import lombok.*;

/**
 * @author Youssef
 * @version 1.0
 * @created 16/04/2025
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationRequest {

    private String email;
    private String password;
}
