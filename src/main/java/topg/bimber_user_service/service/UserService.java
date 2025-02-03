package topg.bimber_user_service.service;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import topg.bimber_user_service.config.JwtUtils;
import topg.bimber_user_service.config.UserDetailsServiceImpl;
import topg.bimber_user_service.dto.*;
import topg.bimber_user_service.exceptions.InvalidUserInputException;
import topg.bimber_user_service.exceptions.MailNotSentException;
import topg.bimber_user_service.exceptions.UserNotFoundInDb;
import topg.bimber_user_service.mail.MailService;
import topg.bimber_user_service.models.*;
import topg.bimber_user_service.repository.AdminRepository;
import topg.bimber_user_service.repository.UserRepository;
import topg.bimber_user_service.repository.UserVerificationRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserVerificationRepository userVerificationRepository;
    private final JwtUtils jwtUtils;
    private final MailService mailService;
    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    // Creates a new user and sends a verification email
    @Transactional
    @Override
    public UserCreatedDto createUser(UserRequestDto userRequestDto) {
        if (StringUtils.isBlank(userRequestDto.email()) ||
                StringUtils.isBlank(userRequestDto.password()) ||
                StringUtils.isBlank(userRequestDto.username())) {
            throw new InvalidUserInputException("Email, password, or username cannot be blank.");
        } else if (userRepository.findByEmail(userRequestDto.email()).isPresent()) {
            throw new InvalidUserInputException("Email is already Taken");
        }

        User user = User.builder()
                .id(generateUserId())
                .username(userRequestDto.username())
                .email(userRequestDto.email())
                .password(passwordEncoder.encode(userRequestDto.password()))
                .createdAt(Date.from(Instant.now()))
                .updatedAt(Date.from(Instant.now()))
                .role(Role.USER)
                .balance(BigDecimal.ZERO)
                .enabled(false)
                .build();
        user = userRepository.save(user);
        String token = generateVerificationToken(user);

        mailService.sendMail(new NotificationEmail(
                "Please activate your account",
                user.getEmail(),
                "Thank you for signing up to our hotel, " +
                        "please click on the below url to activate you account " +
                        "http://localhost:9090/api/v1/user/accountVerification/" + token
        ));
        UserResponseDto userResponseDto = new UserResponseDto(user.getEmail(), user.getUsername(), user.getId());

        return new UserCreatedDto(
                true,
                "user with " + user.getUsername() + " created",
                userResponseDto
        );
    }

    // Generates a verification token for the user
    private String generateVerificationToken(User user) {
        String verificationToken = UUID.randomUUID().toString();
        UserVerificationToken userVerificationToken = new UserVerificationToken();
        userVerificationToken.setToken(verificationToken);
        userVerificationToken.setUser(user);
        userVerificationRepository.save(userVerificationToken);
        return verificationToken;
    }

    // Generates a unique user ID
    private String generateUserId() {
        String alphanumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomPart = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 11; i++) {
            int index = random.nextInt(alphanumeric.length());
            randomPart.append(alphanumeric.charAt(index));
        }
        return "User_" + randomPart;
    }

    // Retrieves a user by ID
    @Override
    public UserResponseDto getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundInDb("User with id " + userId + " not found"));
        if (!user.isEnabled()) {
            throw new IllegalStateException("Your account is not activated. Please activate your account.");
        }
        return new UserResponseDto(
                user.getEmail(),
                user.getUsername(),
                user.getId()
        );
    }

    // Edits user details by ID
    @Transactional
    @Override
    public UserResponseDto editUserById(UserAndAdminUpdateDto userAndAdminUpdateDto, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundInDb("User with id " + userId + " not found"));
        if (!user.isEnabled()) {
            throw new IllegalStateException("Your account is not activated. Please activate your account.");
        }
        if (userRepository.findByEmail(userAndAdminUpdateDto.email()).isPresent()) {
            throw new IllegalArgumentException("Email is already taken.");
        }
        user.setEmail(userAndAdminUpdateDto.email());
        user.setUsername(userAndAdminUpdateDto.username());
        userRepository.save(user);
        return new UserResponseDto(user.getEmail(), user.getUsername(), user.getId());
    }

    // Deletes a user by ID
    @Override
    public String deleteUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundInDb("User with id " + userId + " not found"));
        userRepository.delete(user);
        return "User with id " + userId + " has been successfully deleted.";
    }

    @Override
    public String fundAccount(String userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundInDb("User not found"));

        user.setBalance(user.getBalance().add(amount));
        userRepository.save(user);
        return "You have successfully funded your account with " + amount;
    }

    public JwtResponseDto loginUser(LoginRequestDto loginRequestDto) {
        // Authenticate the user
        Authentication authentication = authenticateUser(loginRequestDto);

        // Set authentication context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsServiceImpl userDetails = (UserDetailsServiceImpl) authentication.getPrincipal();
        String username = userDetails.getUsername();

        // Generate JWT token for the authenticated user
        String jwt = jwtUtils.generateToken(userDetails);

        return new JwtResponseDto(true, jwt);
    }

    private Authentication authenticateUser(LoginRequestDto loginRequestDto) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.username(),
                        loginRequestDto.password()
                )
        );
    }


    public void verifyToken(String token) {
        UserVerificationToken verificationToken = userVerificationRepository.findByToken(token)
                .orElseThrow(() -> new MailNotSentException("Invalid Token"));
        fetchAdminAndEnable(verificationToken);
    }

    private void fetchAdminAndEnable(UserVerificationToken verificationToken) {
        String email = verificationToken.getUser().getEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundInDb("Email not found"));
        user.setEnabled(true);
        userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("User with username " + username + " not found"));
    }



}