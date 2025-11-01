package com.tech2nxt.aiofbackend.repository;

import com.tech2nxt.aiofbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email address
     * @param email User's email
     * @return Optional containing user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if email already exists
     * @param email Email to check
     * @return true if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Find active user by email
     * @param email User's email
     * @param active Active status
     * @return Optional containing active user if found
     */
    Optional<User> findByEmailAndActive(String email, Boolean active);
}
