package com.moushtario.ecomera.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Youssef
 * @version 1.0
 * @created 13/04/2025
 * @lastModified 13/04/2025
 */
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
}
