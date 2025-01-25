package topg.bimber_user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import topg.bimber_user_service.dto.BookingRequestDto;
import topg.bimber_user_service.dto.BookingResponseDto;
import topg.bimber_user_service.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // Endpoint to book a room
    @PostMapping("/book")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<BookingResponseDto> bookRoom(@RequestBody BookingRequestDto bookingRequestDto) {
        BookingResponseDto response = bookingService.bookRoom(bookingRequestDto);
        return ResponseEntity.ok(response);
    }

    // Endpoint to cancel a booking
    @PutMapping("/cancel/{bookingId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> cancelBooking(@PathVariable Long bookingId) {
        String response = bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok(response);
    }

    // Endpoint to update a booking
    @PutMapping("/update/{bookingId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BookingResponseDto> updateBooking(
            @PathVariable Long bookingId,
            @RequestBody BookingRequestDto bookingRequestDto) {
        BookingResponseDto response = bookingService.updateBooking(bookingId, bookingRequestDto);
        return ResponseEntity.ok(response);
    }

    // Endpoint to list all bookings
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<BookingResponseDto>> listAllBookings() {
        List<BookingResponseDto> bookings = bookingService.listAllBookings();
        return ResponseEntity.ok(bookings);
    }
}
