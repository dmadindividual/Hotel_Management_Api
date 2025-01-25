package topg.bimber_user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import topg.bimber_user_service.models.Admin;
import topg.bimber_user_service.models.User;

import java.util.Optional;
@Repository
public interface AdminRepository extends JpaRepository<Admin, String> {

    Optional<Admin> findByEmail(String email);

    Optional<Admin> findByUsername(String username);
}
