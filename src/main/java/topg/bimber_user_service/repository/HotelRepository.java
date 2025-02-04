package topg.bimber_user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import topg.bimber_user_service.models.Hotel;
import topg.bimber_user_service.models.State;

import java.util.List;
import java.util.Optional;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
    Optional<Hotel> findByName(String name);

    List<Hotel> findByState(State state);

    Integer countByState(State state);

    @Query("SELECT h FROM Hotel h JOIN Booking b ON h.id = b.hotel.id " +
            "WHERE h.state = :state " +
            "GROUP BY h.id " +
            "ORDER BY COUNT(b.id) DESC")
    List<Hotel> findMostBookedHotelsByState(State state);
}
