package topg.bimber_user_service.dto;

import java.time.LocalDate;

public record BookingRequestDto(
        String userId,
        Long roomId,
        Long hotelId,
        LocalDate startDate,
        LocalDate endDate,
        Boolean isPaid
) {
}
