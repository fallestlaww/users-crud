package org.example.backend.repository;

import org.example.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {
        "spring.test.database.replace=NONE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=update",
        "spring.sql.init.mode=never"
})
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    private User user;
    private User anotherUser;

    private User createUser(String firstName, String lastName, String email) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        return userRepository.save(user);
    }

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        user = createUser("John", "Smith", "john.smith@example.com");
        anotherUser = createUser("Jane", "Smith", "jane.smith@example.com");
    }

    @Test
    public void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<User> actualPageList = userRepository.findAll(pageable);
        List<User> actualList = actualPageList.getContent();

        assertAll(
                () -> assertEquals(2, actualList.size()),
                () -> assertEquals(user.getId(), actualList.getFirst().getId()),
                () -> assertEquals(anotherUser.getId(), actualList.get(1).getId()),
                () -> assertEquals(user.getEmail(), actualList.getFirst().getEmail()),
                () ->assertEquals(anotherUser.getEmail(), actualList.get(1).getEmail())
        );
    }

    @Test
    public void testFindByEmailSuccess() {
        User actualUser = userRepository.findByEmail(user.getEmail());
        assertAll(
                () -> assertEquals(user.getId(), actualUser.getId()),
                () -> assertEquals(user.getEmail(), actualUser.getEmail()),
                () -> assertEquals(user.getFirstName(), actualUser.getFirstName()),
                () -> assertEquals(user.getLastName(), actualUser.getLastName())
        );
    }

    @Test
    public void testFindByEmailFailure_wrongEmail() {
        User actualUser = userRepository.findByEmail("wrongEmail");
        assertNull(actualUser);
    }

    @Test
    public void testExistsByEmailSuccess() {
        assertTrue(userRepository.existsByEmail(user.getEmail()));
    }

    @Test
    public void testExistsByEmailFailure_wrongEmail() {
        assertFalse(userRepository.existsByEmail("wrongEmail"));
    }

    @Test
    public void testFindByFirstNameSuccess() {
        Pageable pageable = PageRequest.of(0, 10);

        Optional<Page<User>> actualPageList = userRepository.findByFirstName(user.getFirstName(), pageable);
        List<User> actualList = actualPageList.get().getContent();

        assertAll(
                () -> assertEquals(1, actualList.size()),
                () -> assertEquals(user.getId(), actualList.getFirst().getId()),
                () -> assertEquals(user.getEmail(), actualList.getFirst().getEmail()),
                () -> assertEquals(user.getFirstName(), actualList.getFirst().getFirstName())
        );
    }

    @Test
    public void testFindByFirstNameFailure_wrongFirstName() {
        Pageable pageable = PageRequest.of(0, 10);

        Optional<Page<User>> actualPageList = userRepository.findByFirstName("wrong", pageable);
        List<User> actualList = actualPageList.get().getContent();

        assertEquals(0, actualList.size());
    }
}
