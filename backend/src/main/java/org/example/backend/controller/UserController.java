package org.example.backend.controller;

import jakarta.validation.Valid;
import org.example.backend.dto.request.UserInformationRequest;
import org.example.backend.dto.response.UserInformationResponse;
import org.example.backend.model.User;
import org.example.backend.service.impl.UserServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for managing users.
 * Provides endpoints for CRUD operations on users.
 */
@RestController()
@RequestMapping("/users")
public class UserController {
    private final UserServiceImpl userService;

    /**
     *  Injecting dependencies with constructor injection.
     *
     * @param userService the service to be used for user operations
     */
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    /**
     * Retrieves a paginated list of all users.
     *
     * @param page the page number (zero-based)
     * @param size the number of items per page
     * @return a page of users
     */
    @GetMapping()
    public Page<User> getAllUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue ="5") int size) {
        return userService.getUsersList(PageRequest.of(page, size));
    }

    /**
     * Searches for users by first name.
     *
     * @param page the page number (zero-based)
     * @param size the number of items per page
     * @param firstName the first name to search for
     * @return a page of matching users
     */
    @GetMapping("/search")
    public Page<User> searchUsersByFirstName(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size,
                                             @RequestParam String firstName) {
        return userService.getUsersByName(firstName, PageRequest.of(page, size));
    }

    /**
     * Creates a new user with information that was passed to the request object.
     *
     * @param request the user information request containing the user details
     * @return a response entity containing the created user information
     */
    @PostMapping()
    public ResponseEntity<?> createUser(@Valid @RequestBody UserInformationRequest request) {
        User createdUser = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserInformationResponse(createdUser));
    }

    /**
     * Updates the user with the information that was passed to the request object.
     *
     * @param id the ID of the user to update
     * @param request the user information request containing the updated details
     * @return a response entity containing the updated user information
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserInformationRequest request) {
        User updatedUser = userService.updateUser(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(new UserInformationResponse(updatedUser));
    }

    /**
     * Deletes a user by passed user id.
     *
     * @param id the ID of the user to delete
     * @return a response entity with a success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body("Successful deleted user " + id);
    }
}
