package org.example.backend.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.example.backend.model.User;

/**
 * Data Transfer Object for user information responses.
 * This class is used to send user data to clients.
 * Uses snake_case naming strategy for JSON serialization.
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserInformationResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    /**
     * Constructs a new UserInformationResponse from a User entity.
     *
     * @param user the user entity to convert
     */
    public UserInformationResponse(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
    }
}
