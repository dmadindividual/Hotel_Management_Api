package topg.bimber_user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import topg.bimber_user_service.models.Room;
import topg.bimber_user_service.models.RoomType;
import topg.bimber_user_service.models.State;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByHotelId(Long hotelId);
    List<Room> findByAvailable(boolean available);

    List<Room> findByRoomType(RoomType roomType);

    List<Room> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    boolean existsByIdAndHotelId(Long id, Long hotelId);

    List<Room> findByHotelIdAndAvailable(Long hotelId, boolean available);

    Optional<Room> findByHotelIdAndId(Long hotelId, Long roomId);

    List<Room> findByHotelIdAndRoomType(Long hotelId, RoomType roomType);

    List<Room> findByPriceBetweenAndHotel_State(BigDecimal minPrice, BigDecimal maxPrice, State state);
}
