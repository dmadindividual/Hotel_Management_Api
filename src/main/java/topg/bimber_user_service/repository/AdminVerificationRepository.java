package topg.bimber_user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import topg.bimber_user_service.models.AdminVerificationToken;

import java.util.Optional;

@Repository
public interface AdminVerificationRepository extends JpaRepository<AdminVerificationToken, Long> {
    Optional<AdminVerificationToken> findByToken( String token);
}
