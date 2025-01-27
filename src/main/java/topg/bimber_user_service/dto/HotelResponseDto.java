package topg.bimber_user_service.dto;

import java.io.Serializable;
import java.util.List;

public record HotelResponseDto(
        boolean success,
      HotelDto hotel,
        List<RoomResponseDto> rooms
)implements Serializable {
    private static final long serialVersionUID = 1L;
}
