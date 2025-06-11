package org.example.backend.repository;

import org.example.backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

/**
 * Repository interface for User entity.
 * Provides methods for database operations related to users.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Finds all users with pagination.
     *
     * @param pageable the pagination information
     * @return a page of matching users
     */
    Page<User> findAll(Pageable pageable);

    /**
     * Finds users by email.
     *
     * @param email the email to search for
     * @return user with searched email
     */
    User findByEmail(String email);
    /**
     * Checks if a user with the specified email exists.
     *
     * @param email the email to check
     * @return true if a user with the email exists, false otherwise
     */
    boolean existsByEmail(String email);
    /**
     * Finds users by first name with pagination.
     *
     * @param firstName the first name to search for
     * @param pageable the pagination information
     * @return an optional containing a page of matching users, or empty if none found
     */
    Optional<Page<User>> findByFirstName(String firstName, Pageable pageable);
}
