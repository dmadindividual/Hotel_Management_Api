package topg.bimber_user_service.dto;

import java.time.LocalDate;

public record BookingRequestDto(
        String userId,
        Long roomId,
        Long hotelId,    // Added hotelId to specify the hotel
        LocalDate startDate,
        LocalDate endDate,
        Boolean isPaid
) {}
