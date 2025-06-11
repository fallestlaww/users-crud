package org.example.backend.service;

import org.example.backend.dto.request.UserInformationRequest;
import org.example.backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for user management operations. Provides abstract methods for CRUD operations for User entity.
 */
public interface UserService {
    /**
     * Retrieves a paginated list of all users.
     * @param pageable the pagination information
     * @return a page of users
     */
    Page<User> getUsersList(Pageable pageable);

    /**
     * Creates a new user with information that was passed to the request object.
     *
     * @param request the user information request containing the user details
     * @return the created user
     */
    User createUser(UserInformationRequest request);

    /**
     * Updates the user with the information that was passed to the request object.
     *
     * @param id the ID of the user to update
     * @param request the user information request containing the updated details
     * @return the updated user
     */
    User updateUser(Long id, UserInformationRequest request);

    /**
     * Deletes a user by passed user id
     *
     * @param id the ID of the user to delete
     */
    void deleteUser(Long id);

    /**
     * Searches for users by first name.
     *
     * @param name the first name to search for
     * @param pageable the pagination information
     * @return a page of matching users
     */
    Page<User> getUsersByName(String name, Pageable pageable);
}
