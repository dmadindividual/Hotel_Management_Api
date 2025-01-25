package topg.bimber_user_service.service;

import topg.bimber_user_service.dto.RoomRequestDto;
import topg.bimber_user_service.dto.RoomResponseDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IRoomService {
    RoomResponseDto createRoom(RoomRequestDto roomRequestDto);
    RoomResponseDto editRoomById(Long id, RoomRequestDto roomRequestDto);
    String deleteRoomById(Long id);
    List<RoomResponseDto> findAllRoomsByHotelId(Long hotelId);

    boolean isRoomAvailable(Long id);
    List<RoomResponseDto> findAllAvailableRooms();
    RoomResponseDto deactivateRoom(Long id);
    RoomResponseDto reactivateRoom(Long id);
    List<RoomResponseDto> filterRooms(String type);
    List<RoomResponseDto> filterByPrice(BigDecimal minPrice, BigDecimal maxPrice);

}
