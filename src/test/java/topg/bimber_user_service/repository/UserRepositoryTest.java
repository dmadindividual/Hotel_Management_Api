package topg.bimber_user_service.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import topg.bimber_user_service.models.Role;
import topg.bimber_user_service.models.User;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;



@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void init() {
        user = new User();
        user.setUsername("Izana");
        user.setEmail("izana@outlook.com");
        user.setPassword("OLfa12/?");
        user.setCreatedAt(Date.from(Instant.now()));
        user.setUpdatedAt(Date.from(Instant.now()));
        user.setRole(Role.USER);
        user.setBalance(BigDecimal.valueOf(700000.00));
    }

    @Test
    @DisplayName("Create a new user and save to database")
    void createUser() {
        user = userRepository.save(user);

        assertThat(user).isNotNull();
        assertThat(user.getId()).isNotNull();
        assertThat(user.getUsername()).isEqualTo("Izana");
        assertThat(user.getEmail()).isEqualTo("izana@outlook.com");
        assertThat(user.getRole()).isEqualTo(Role.USER);
        assertThat(user.getBalance()).isEqualTo(BigDecimal.valueOf(700000.00));
    }

    @Test
    @DisplayName("Get a user By Id")
    void getUserById() {
        user = userRepository.save(user);

        Optional<User> retrievedUser = userRepository.findById(user.getId());

        assertTrue(retrievedUser.isPresent(), "User should be found");
        User foundUser = retrievedUser.get();
        assertEquals(user.getId(), foundUser.getId(), "User IDs should match");
        assertEquals(user.getUsername(), foundUser.getUsername(), "Usernames should match");
        assertEquals(user.getEmail(), foundUser.getEmail(), "Emails should match");
        assertEquals(user.getRole(), foundUser.getRole(), "Roles should match");
        assertEquals(user.getBalance(), foundUser.getBalance(), "Balances should match");
    }

    @Test
    @DisplayName("Edit a user By Id")
    void editUserById() {
        user = userRepository.save(user);

        user.setUsername("Baki");
        user.setEmail("Baki@outlook.com");
        user.setBalance(BigDecimal.valueOf(500));
        user.setUpdatedAt(Date.from(Instant.now()));

        user = userRepository.save(user);

        Optional<User> optionalUser = userRepository.findById(user.getId());

        assertTrue(optionalUser.isPresent(), "User should exist after update");

        User updatedUser = optionalUser.get();
        assertEquals(user.getId(), updatedUser.getId(), "The IDs must match");
        assertEquals("Baki", updatedUser.getUsername(), "Usernames should match");
        assertEquals("Baki@outlook.com", updatedUser.getEmail(), "Emails should match");
        assertEquals(BigDecimal.valueOf(500), updatedUser.getBalance(), "Balances should match");
    }

    @Test
    @DisplayName("Delete a user By Id")
    void deleteUserById() {
        user = userRepository.save(user);
        userRepository.delete(user);

        Optional<User> optionalUser = userRepository.findById(user.getId());

        assertTrue(optionalUser.isEmpty(), "User should not exist");
    }
}
