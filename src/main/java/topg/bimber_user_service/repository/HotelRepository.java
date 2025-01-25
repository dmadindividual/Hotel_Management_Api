package topg.bimber_user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import topg.bimber_user_service.models.Hotel;

import java.util.Optional;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
    Optional<Hotel> findByName(String name);
}
