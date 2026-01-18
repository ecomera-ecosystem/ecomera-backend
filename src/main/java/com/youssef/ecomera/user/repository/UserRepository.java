package com.youssef.ecomera.user.repository;

import com.youssef.ecomera.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Youssef
 * @version 1.0
 * @created 13/04/2025
 * @lastModified 13/04/2025
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}
