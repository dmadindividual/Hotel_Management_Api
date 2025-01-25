package topg.bimber_user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import topg.bimber_user_service.models.Booking;
import topg.bimber_user_service.models.BookingStatus;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByUserIdAndStatus(String id, BookingStatus bookingStatus);
}
