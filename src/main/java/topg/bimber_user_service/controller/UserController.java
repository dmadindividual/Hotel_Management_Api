package topg.bimber_user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import topg.bimber_user_service.dto.UserAndAdminUpdateDto;
import topg.bimber_user_service.dto.UserCreatedDto;
import topg.bimber_user_service.dto.UserRequestDto;
import topg.bimber_user_service.dto.UserResponseDto;
import topg.bimber_user_service.models.User;
import topg.bimber_user_service.service.UserService;

import java.security.Principal;

import static org.springframework.http.HttpStatus.OK;

@RequestMapping("/api/v1/user")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;



    @GetMapping("/me/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable("id") String userId, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        UserResponseDto message = userService.getUserById(userId);
        return ResponseEntity.ok(message);
    }

    @PutMapping("/me/edit/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<UserResponseDto> editUserById(@Valid @RequestBody UserAndAdminUpdateDto userAndAdminUpdateDto, @PathVariable("id") String userId, Principal principal) {
        User user = userService.findByUsername(principal.getName());

        UserResponseDto message = userService.editUserById(userAndAdminUpdateDto, userId);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/me/delete/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<String> deleteUserById(@PathVariable("id") String userId , Principal principal){
        User user = userService.findByUsername(principal.getName());
        if(!user.isEnabled()){
            throw new IllegalStateException("Your account is not activated. Please activate your account.");

        }
        String message = userService.deleteUserById( userId);
        return ResponseEntity.ok(message);
    }

    @GetMapping("accountVerification/{token}")
    public ResponseEntity<String> verifyAccount(@PathVariable String token) {
        try {
            userService.verifyToken(token);  // Assuming verifyToken checks the token validity and does the necessary action
            return new ResponseEntity<>("Account created successfully", OK);

        } catch (Exception e) {
            // Handle errors like invalid token
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token.");
        }
    }
}