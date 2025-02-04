package topg.bimber_user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import topg.bimber_user_service.models.Booking;
import topg.bimber_user_service.models.BookingStatus;

import java.time.LocalDate;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByUserIdAndHotelIdAndStatus(String userId, Long hotelId, BookingStatus bookingStatus);

    @Query("""
    SELECT COUNT(b) > 0 FROM Booking b\s
    WHERE b.room.id = :roomId\s
    AND (:startDate < b.endDate AND :endDate > b.startDate)
""")
    boolean existsByRoomIdAndDatesOverlap(@Param("roomId") Long roomId,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);

    Optional<Booking> findByUserIdAndHotelId(String userId, Long hotelId);
}
