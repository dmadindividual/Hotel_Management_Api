package topg.bimber_user_service.service;

import topg.bimber_user_service.dto.BookingRequestDto;
import topg.bimber_user_service.dto.BookingResponseDto;

import java.util.List;

public interface IBookingService {

    BookingResponseDto bookRoom(BookingRequestDto bookingRequestDto);

    String cancelBooking(Long bookingId);

    BookingResponseDto updateBooking(Long bookingId, BookingRequestDto bookingRequestDto);

    List<BookingResponseDto> listAllBookings();

}
