package org.example.backend.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.example.backend.dto.request.UserInformationRequest;
import org.example.backend.exceptions.custom.EntityNullException;
import org.example.backend.model.User;
import org.example.backend.repository.UserRepository;
import org.example.backend.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;

    private User expectedUser;
    private User actualUser;
    private UserInformationRequest request;
    private Validator validator;

    @BeforeEach
    public void setUp() {
        expectedUser = createUser(1L,"John", "Doe", "john.doe@example.com");

        request = createUserInformationRequest("John", "Doe", "john.doe.example.com");

        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    private User createUser(Long id, String firstName, String lastName, String email) {
        User user = new User();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        return user;
    }

    private UserInformationRequest createUserInformationRequest(String firstName, String lastName, String email) {
        UserInformationRequest request = new UserInformationRequest();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setEmail(email);
        return request;
    }

    @Test
    public void userCreateSuccess() {
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);

        actualUser = userService.createUser(request);

        assertNotNull(actualUser);
        assertAll(
                () -> assertEquals(expectedUser.getId(), actualUser.getId()),
                () -> assertEquals(expectedUser.getEmail(), actualUser.getEmail()),
                () -> assertEquals(expectedUser.getFirstName(), actualUser.getFirstName()),
                () -> assertEquals(expectedUser.getLastName(), actualUser.getLastName())
        );
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void userCreateFailure_alreadyCreatedEmail() {
        when(userRepository.existsByEmail(any())).thenReturn(true);

        assertThrows(EntityExistsException.class, () -> userService.createUser(request));
        verify(userRepository, times(1)).existsByEmail(any());
    }

    @Test
    public void userCreateFailure_incorrectEmailFormat() {
        UserInformationRequest invalidRequest = createUserInformationRequest("John", "Doe", "%");

        var violations = validator.validate(invalidRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void getAllUsersSuccess() {
        List<User> expectedList = List.of(expectedUser);
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> expected = new PageImpl<>(expectedList, pageable, expectedList.size());

        when(userRepository.findAll(pageable)).thenReturn(expected);

        Page<User> actual = userService.getUsersList(pageable);

        assertNotNull(actual);
        assertEquals(expected.getTotalElements(), actual.getTotalElements());
        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    public void getUserByNameSuccess() {
        List<User> expectedList = List.of(expectedUser);
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> expected = new PageImpl<>(expectedList, pageable, expectedList.size());

        when(userRepository.findByFirstName(request.getFirstName(), pageable)).thenReturn(Optional.of(expected));

        Page<User> actual = userService.getUsersByName(request.getFirstName(), pageable);
        assertNotNull(actual);
        assertEquals(expected.getTotalElements(), actual.getTotalElements());
        verify(userRepository, times(1)).findByFirstName(request.getFirstName(), pageable);
    }

    @Test
    public void getUserByNameFailure_wrongFirstName() {
        String wrongName = "Jane";
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findByFirstName(wrongName, pageable)).thenReturn(Optional.empty());

        assertThrows(EntityNullException.class, () -> userService.getUsersByName(wrongName, pageable));
        verify(userRepository, times(1)).findByFirstName(wrongName, pageable);
    }

    @Test
    public void getUserByNameFailure_firstNameIsNull() {
        Pageable pageable = PageRequest.of(0, 10);

        assertThrows(EntityNullException.class, () -> userService.getUsersByName(null, pageable));
    }

    @Test
    public void updateUserSuccess() {
        UserInformationRequest updateRequest = createUserInformationRequest("John", "Doee", "john.doe1@example.com");
        when(userRepository.findById(expectedUser.getId())).thenReturn(Optional.of(expectedUser));

        expectedUser.setLastName("Doee");
        expectedUser.setEmail("john.doe1@example.com");
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);

        actualUser = userService.updateUser(1L, updateRequest);

        assertEquals(expectedUser, actualUser);
        assertAll(
                () -> assertEquals(expectedUser.getEmail(), actualUser.getEmail()),
                () -> assertEquals(expectedUser.getFirstName(), actualUser.getFirstName()),
                () -> assertEquals(expectedUser.getLastName(), actualUser.getLastName()),
                () -> assertEquals(expectedUser.getId(), actualUser.getId())
        );
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void updateUserFailure_wrongId() {
        UserInformationRequest updateRequest = createUserInformationRequest("John", "Doee", "john.doe1@example.com");

        when(userRepository.findById(expectedUser.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.updateUser(1L, updateRequest));
        verify(userRepository, times(1)).findById(expectedUser.getId());
    }

    @Test
    public void updateUserFailure_wrongEmail() {
        UserInformationRequest updateRequest = createUserInformationRequest("John", "Doee", "john.doe1");

        var violations = validator.validate(updateRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void updateUserFailure_existedEmail() {
        Long userId = 1L;
        String existingEmail = "john.doe1@example.com";

        User currentUser = createUser(userId, "Old", "User", "old.email@example.com");

        UserInformationRequest updateRequest = createUserInformationRequest("New", "Name", existingEmail);

        User otherUser = createUser(2L, "Other", "User", existingEmail);

        when(userRepository.findById(userId)).thenReturn(Optional.of(currentUser));
        when(userRepository.existsByEmail(existingEmail)).thenReturn(true);

        assertThrows(EntityExistsException.class, () -> {
            userService.updateUser(userId, updateRequest);
        });

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).existsByEmail(existingEmail);
        verify(userRepository, never()).save(any());
    }

    @Test
    public void deleteUserSuccess() {
        when(userRepository.findById(expectedUser.getId())).thenReturn(Optional.of(expectedUser));
        userService.deleteUser(expectedUser.getId());
        verify(userRepository).delete(any());
    }

    @Test
    public void deleteUserFailure_wrongId() {
        when(userRepository.findById(expectedUser.getId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(expectedUser.getId()));
        verify(userRepository, times(1)).findById(expectedUser.getId());
    }
}
