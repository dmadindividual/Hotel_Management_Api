package topg.bimber_user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UserRequestDto(
        @NotBlank(message = "Username cannot be blank")
        @Size(min = 6, message = "Username must be more than 5 characters")
        String username,

        @NotBlank(message = "Password cannot be blank")
        @Size(min = 6, message = "Password must be more than 5 characters")
        String password,

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email should be valid")
        String email,
        BigDecimal amount
) {
}
