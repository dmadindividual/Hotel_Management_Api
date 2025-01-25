package topg.bimber_user_service.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Optional;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private Instant expiryDate;
    @OneToOne(fetch = FetchType.EAGER)
    private User user;



}
