package topg.bimber_user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import topg.bimber_user_service.models.UserVerificationToken;

import java.util.Optional;

@Repository
public interface UserVerificationRepository extends JpaRepository<UserVerificationToken, Long> {
    Optional<UserVerificationToken> findByToken(String token);
}
