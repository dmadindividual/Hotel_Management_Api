package topg.bimber_user_service.dto;

import java.io.Serializable;
import java.time.LocalDate;

public record BookingResponseDto(
        Long bookingId,
        String userId,
        Long roomId,
        LocalDate startDate,
        LocalDate endDate,
        String status,
        boolean isPaid
)implements Serializable {
    private static final long serialVersionUID = 1L;

}