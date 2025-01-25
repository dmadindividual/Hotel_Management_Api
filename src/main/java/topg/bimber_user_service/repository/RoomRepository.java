package topg.bimber_user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import topg.bimber_user_service.models.Room;
import topg.bimber_user_service.models.RoomType;

import java.math.BigDecimal;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByHotelId(Long hotelId);
    List<Room> findByIsAvailable(boolean isAvailable);

    List<Room> findByRoomType(RoomType roomType);

    List<Room> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
}
