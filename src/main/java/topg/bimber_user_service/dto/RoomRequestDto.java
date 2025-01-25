package topg.bimber_user_service.dto;

import topg.bimber_user_service.models.RoomType;

import java.math.BigDecimal;

public record RoomRequestDto(
        RoomType roomType,
        BigDecimal price,
        Boolean isAvailable,
        Long hotelId
) {
}
