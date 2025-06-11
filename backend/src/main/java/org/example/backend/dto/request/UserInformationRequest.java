package org.example.backend.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Data Transfer Object for user information requests.
 * This class is used to receive user data from client requests.
 * Uses snake_case naming strategy for JSON serialization/deserialization.
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserInformationRequest {
    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 255, message = "First name must be between 1 and 255 characters")
    private String firstName;
    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 255, message = "Last name must be between 1 and 255 characters")
    private String lastName;
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email must be at most 255 characters")
    private String email;
}
