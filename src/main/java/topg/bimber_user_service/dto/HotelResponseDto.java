package topg.bimber_user_service.dto;

import java.util.List;

public record HotelResponseDto(
        boolean success,
      HotelDto hotel,
        List<RoomResponseDto> rooms
) {
}
