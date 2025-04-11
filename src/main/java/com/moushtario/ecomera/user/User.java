package com.moushtario.ecomera.user;

import jakarta.persistence.*;
import lombok.*;

/**
 * @author Youssef
 * @version 1.0
 * @created 11/04/2025
 * @lastModified 11/04/2025
 */

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "_user")
public class User {

    @Id
    @GeneratedValue
    private int id;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
