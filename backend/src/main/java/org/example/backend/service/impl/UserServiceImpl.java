package org.example.backend.service.impl;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.dto.request.UserInformationRequest;
import org.example.backend.exceptions.custom.EntityNullException;
import org.example.backend.model.User;
import org.example.backend.repository.UserRepository;
import org.example.backend.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.util.Optional;

/**
 * Implementation of the UserService interface.
 * Provides business logic for user management operations.
 */
@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    /**
     * Injecting dependencies with constructor injection.
     * @param userRepository the repository to be used for user operations
     */
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves a paginated list of all users.
     *
     * @param pageable the pagination information
     * @return a page of users
     */
    @Override
    public Page<User> getUsersList(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    /**
     * Creates a new user with information that was passed to the request object.
     *
     * @param request the user information request containing the user details
     * @return the created user
     * @throws EntityExistsException if a user with the same email already exists
     * @throws EntityNullException if the email is null or empty
     * @throws ConstraintViolationException if the email formatted incorrectly
     */
    @Override
    public User createUser(UserInformationRequest request) {
        log.info("Creating user according to received request: {} ", request);
        if(userRepository.existsByEmail(request.getEmail())) {
            log.warn("Requested email already registered: {}", request.getEmail());
            throw new EntityExistsException("User already exists");
        }

        if(request.getEmail() == null || request.getEmail().isEmpty()) {
            log.warn("Requested email is null or empty: {}", request.getEmail());
            throw new EntityNullException("Email cannot be null or empty");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        log.info("Created user: {}", user);
        return userRepository.save(user);
    }

    /**
     * Updates the user with the information that was passed to the request object.
     *
     * @param id the ID of the user to update
     * @param request the user information request containing the updated details
     * @return the updated user
     * @throws EntityNotFoundException if the user is not found
     * @throws EntityExistsException if the new email is already in use
     * @throws EntityNullException if the ID is null
     * @throws ConstraintViolationException if the email formatted incorrectly
     */
    @Override
    public User updateUser(Long id, UserInformationRequest request) {
        log.info("Updating user with id {} according to received request: {} ", id, request);
        if(id == null) {
            log.warn("Requested id is null");
            throw new EntityNullException("Id can not be null.");
        }
        User updatedUser = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found. Maybe you entered wrong or negative id? "));

        // allows to save information about the user's past email, so as not to consider the current email as someone else's email, but to skip it when updating
        if(request.getEmail() != null && !request.getEmail().equals(updatedUser.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                log.warn("Requested email already registered: {}", request.getEmail());
                throw new EntityExistsException("User with this email already exists");
            }
            // update email only in case if requested email is not null, do not exist in another object and is not current email of this object
            updatedUser.setEmail(request.getEmail());
        }

        Optional.of(request.getFirstName()).ifPresent(updatedUser::setFirstName);
        Optional.of(request.getLastName()).ifPresent(updatedUser::setLastName);

        return userRepository.save(updatedUser);
    }

    /**
     * Deletes a user by passed user id
     *
     * @param id the ID of the user to delete
     * @throws EntityNotFoundException if the user is not found
     * @throws EntityNullException if the ID is null
     */
    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with id {}", id);
        if(id == null) {
            log.warn("Requested id is null");
            throw new EntityNullException("Id can not be null.");
        }
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found. Maybe you entered wrong or negative id?"));
        userRepository.delete(user);
    }

    /**
     * Searches for users by first name.
     *
     * @param name the first name to search for
     * @param pageable the pagination information
     * @return a page of matching users
     * @throws EntityNullException if the name is null or empty
     */
    @Override
    public Page<User> getUsersByName(String name, Pageable pageable) {
        if(name == null || name.isEmpty()) {
            log.warn("Requested name is null or empty");
            throw new EntityNullException("Name can not be null or empty");
        }
        return userRepository.findByFirstName(name, pageable).orElseThrow(() -> new EntityNullException("User not found"));
    }
}
