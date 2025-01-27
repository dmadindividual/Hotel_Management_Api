package topg.bimber_user_service.service;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import topg.bimber_user_service.config.JwtUtils;
import topg.bimber_user_service.dto.*;
import topg.bimber_user_service.exceptions.MailNotSentException;
import topg.bimber_user_service.exceptions.UserNotFoundInDb;
import topg.bimber_user_service.mail.MailService;
import topg.bimber_user_service.models.Admin;
import topg.bimber_user_service.models.AdminVerificationToken;
import topg.bimber_user_service.models.NotificationEmail;
import topg.bimber_user_service.models.Role;
import topg.bimber_user_service.repository.AdminRepository;
import topg.bimber_user_service.repository.AdminVerificationRepository;

import java.time.Instant;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService implements IAdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final AdminVerificationRepository adminVerificationRepository;
    private final MailService mailService;

    // Creates a new admin user and sends an email verification token
    @Override
    @Transactional
    public UserCreatedDto createAdmin(UserRequestDto userRequestDto) {
        if (!isValidRequest(userRequestDto)) {
            return createFailureResponse("Email, password, or username cannot be blank.");
        }

        if (isEmailTaken(userRequestDto.email())) {
            return createFailureResponse("Email is already taken.");
        }

        Admin admin = createAdminEntity(userRequestDto);
        admin = adminRepository.save(admin);

        String token = generateVerificationToken(admin);
        sendActivationEmail(admin, token);

        return createSuccessResponse(admin);
    }

    private boolean isValidRequest(UserRequestDto userRequestDto) {
        return StringUtils.isNotBlank(userRequestDto.email()) &&
                StringUtils.isNotBlank(userRequestDto.password()) &&
                StringUtils.isNotBlank(userRequestDto.username());
    }

    private boolean isEmailTaken(String email) {
        return adminRepository.findByEmail(email).isPresent();
    }

    private Admin createAdminEntity(UserRequestDto userRequestDto) {
        return Admin.builder()
                .id(generateUserId())
                .username(userRequestDto.username())
                .email(userRequestDto.email())
                .password(passwordEncoder.encode(userRequestDto.password()))
                .createdAt(Date.from(Instant.now()))
                .updatedAt(Date.from(Instant.now()))
                .role(Role.ADMIN)
                .enabled(false)
                .build();
    }

    private void sendActivationEmail(Admin admin, String token) {
        String activationUrl = "http://localhost:9090/api/v1/admin/accountVerification/" + token;
        String emailBody = String.format(
                "Thank you for signing up to our hotel, please click on the below URL to activate your account: %s",
                activationUrl
        );

        mailService.sendMail(new NotificationEmail(
                "Please activate your account",
                admin.getEmail(),
                emailBody
        ));
    }

    private UserCreatedDto createFailureResponse(String message) {
        return new UserCreatedDto(false, message, null);
    }

    private UserCreatedDto createSuccessResponse(Admin admin) {
        UserResponseDto userResponseDto = new UserResponseDto(admin.getEmail(), admin.getUsername(), admin.getId());
        return new UserCreatedDto(true, "User with " + admin.getUsername() + " created", userResponseDto);
    }


    // Generates a unique user ID for the admin
    private String generateUserId() {
        String alphanumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomPart = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 11; i++) {
            int index = random.nextInt(alphanumeric.length());
            randomPart.append(alphanumeric.charAt(index));
        }
        return "Admin" + "_" + randomPart;
    }

    // Retrieves an admin by ID
    @Override
    public UserResponseDto getAdminById(String adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new UserNotFoundInDb("User with id " + adminId + " not found"));
        if (!admin.isEnabled()) {
            throw new IllegalStateException("Your account is not activated. Please activate your account.");
        }
        return new UserResponseDto(admin.getEmail(), admin.getUsername(), admin.getId());
    }

    // Updates admin details by ID
    @Override
    @Transactional
    public UserResponseDto editAdminById(UserAndAdminUpdateDto adminUpdateRequestDto, String adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new UserNotFoundInDb("User with id " + adminId + " not found"));
        if (!admin.isEnabled()) {
            throw new IllegalStateException("Your account is not activated. Please activate your account.");
        }
        if (adminRepository.findByEmail(adminUpdateRequestDto.email()).isPresent()) {
            throw new IllegalArgumentException("Email is already taken.");
        }
        if (adminUpdateRequestDto.email() != null) {
            admin.setEmail(adminUpdateRequestDto.email());
        }
        if (adminUpdateRequestDto.username() != null) {
            admin.setUsername(adminUpdateRequestDto.username());
        }
        adminRepository.save(admin);
        return new UserResponseDto(admin.getEmail(), admin.getUsername(), admin.getId());
    }

    // Deletes an admin by ID
    @Override
    public String deleteAdminById(String adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new UserNotFoundInDb("User with id " + adminId + " not found"));
        if (!admin.isEnabled()) {
            throw new IllegalStateException("Your account is not activated. Please activate your account.");
        }
        adminRepository.delete(admin);
        return "User with id " + adminId + " has been successfully deleted.";
    }

    // Generates a verification token for an admin
    private String generateVerificationToken(Admin admin) {
        String verificationToken = UUID.randomUUID().toString();
        AdminVerificationToken adminVerificationToken = new AdminVerificationToken();
        adminVerificationToken.setToken(verificationToken);
        adminVerificationToken.setAdmin(admin);
        adminVerificationRepository.save(adminVerificationToken);
        return verificationToken;
    }

    // Verifies the provided token and enables the admin account
    public void verifyToken(String token) {
        AdminVerificationToken verificationToken = adminVerificationRepository.findByToken(token)
                .orElseThrow(() -> new MailNotSentException("Invalid Token"));
        fetchAdminAndEnable(verificationToken);
    }

    // Enables the admin account linked to the verification token
    private void fetchAdminAndEnable(AdminVerificationToken verificationToken) {
        String email = verificationToken.getAdmin().getEmail();
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundInDb("Email not found"));
        admin.setEnabled(true);
        adminRepository.save(admin);
    }
}
