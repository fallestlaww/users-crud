package org.example.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.example.backend.dto.request.UserInformationRequest;
import org.example.backend.exceptions.custom.EntityNullException;
import org.example.backend.model.User;
import org.example.backend.repository.UserRepository;
import org.example.backend.service.impl.UserServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@Import(UserControllerTest.MockConfig.class)
public class UserControllerTest {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    private User expectedUser;
    private User expectedUser2;
    private UserInformationRequest request;
    private Pageable pageable;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public UserServiceImpl userService() {
            return Mockito.mock(UserServiceImpl.class);
        }

        @Bean
        public UserRepository userRepository() {
            return Mockito.mock(UserRepository.class);
        }
    }

    @BeforeEach
    public void setUp() {
        expectedUser = createUser("John", "Doe", "john.doe@example.com");
        expectedUser2 = createUser("Marie", "Cross", "marie.doe@example.com");
        request = createUserInformationRequest("John", "Doe", "john.doe@example.com");
        pageable = PageRequest.of(0, 10);
    }

    @AfterEach
    public void resetMocks() {
        Mockito.reset(userService, userRepository);
    }

    private User createUser(String firstName, String lastName, String email) {
        User user = new User();
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
    public void testGetAllUserSuccess() throws Exception {
        List<User> userList = List.of(expectedUser, expectedUser2);
        Page<User> userPage = new PageImpl<>(userList, pageable, userList.size());
        when(userService.getUsersList(pageable)).thenReturn(userPage);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateUserSuccess() throws Exception {
        when(userService.createUser(Mockito.any())).thenReturn(expectedUser);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath("$.first_name").value(expectedUser.getFirstName()))
                .andExpect(jsonPath("$.last_name").value(expectedUser.getLastName()))
                .andExpect(jsonPath("$.email").value(expectedUser.getEmail()))
                .andExpect(status().isCreated());
    }

    @Test
    public void testCreateUserFailure_wrongEmailFormat() throws Exception {
        when(userService.createUser(Mockito.any())).thenThrow(ConstraintViolationException.class);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    public void testCreateUserFailure_emailAlreadyExists() throws Exception {
        when(userService.createUser(Mockito.any())).thenThrow(EntityExistsException.class);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    public void testCreateUserFailure_emailNull() throws Exception {
        when(userService.createUser(Mockito.any())).thenThrow(EntityNullException.class);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    public void testUpdateUserSuccess() throws Exception {
        User updatedUser = createUser("new_first_name", "new_last_name", "new_email@mail.com");
        UserInformationRequest updatedRequest = createUserInformationRequest("new_first_name", "new_last_name", "new_email@mail.com");
        when(userService.updateUser(eq(1L), any(UserInformationRequest.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedRequest)))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateUserFailure_emailAlreadyExists() throws Exception {
        when(userService.updateUser(eq(1L), any(UserInformationRequest.class))).thenThrow(EntityExistsException.class);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    public void testUpdateUserFailure_emailNull() throws Exception {
        when(userService.updateUser(eq(1L), any(UserInformationRequest.class))).thenThrow(EntityNullException.class);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    public void testUpdateUserFailure_emailIncorrectFormat() throws Exception {
        when(userService.updateUser(eq(1L), any(UserInformationRequest.class))).thenThrow(ConstraintViolationException.class);
        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    public void testDeleteUserSuccess() throws Exception {
        doNothing().when(userService).deleteUser(eq(1L));
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteUserFailure_idIsNull() throws Exception {
        doThrow(EntityNullException.class).when(userService).deleteUser(eq(1L));
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    public void testDeleteUserFailure_wrongId() throws Exception {
        doThrow(EntityNotFoundException.class).when(userService).deleteUser(eq(1L));
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNotFound());
    }
}
