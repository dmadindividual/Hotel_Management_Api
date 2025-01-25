package topg.bimber_user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import topg.bimber_user_service.dto.UserAndAdminUpdateDto;
import topg.bimber_user_service.dto.UserResponseDto;
import topg.bimber_user_service.service.AdminService;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;


    @GetMapping("/me/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserResponseDto> getAdminById(@PathVariable("id") String userId) {
        UserResponseDto message = adminService.getAdminById(userId);
        return ResponseEntity.ok(message);
    }

    @PutMapping("/me/edit/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserResponseDto> editUserById(@Valid @RequestBody UserAndAdminUpdateDto adminUpdateRequestDto, @PathVariable("id") String userId) {
        UserResponseDto message = adminService.editAdminById(adminUpdateRequestDto, userId);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/me/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteAdminById(@PathVariable("id") String userId) {
        String message = adminService.deleteAdminById(userId);
        return ResponseEntity.ok(message);
    }

    @GetMapping("accountVerification/{token}")
    public ResponseEntity<String> verifyAccount(@PathVariable String token) {
        try {
            adminService.verifyToken(token);  // Assuming verifyToken checks the token validity and does the necessary action
            return new ResponseEntity<>("Account created successfully", OK);
        } catch (Exception e) {
            // Handle errors like invalid token
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token.");
        }
    }

}