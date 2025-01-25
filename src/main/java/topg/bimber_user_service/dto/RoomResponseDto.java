package topg.bimber_user_service.dto;


import topg.bimber_user_service.models.RoomType;

import java.math.BigDecimal;
import java.util.List;

public record RoomResponseDto(
        Long id,
        RoomType roomType,
        BigDecimal price,
        Boolean isAvailable,
        Long hotelId
) {
}

