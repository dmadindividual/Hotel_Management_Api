package topg.bimber_user_service.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import topg.bimber_user_service.dto.BookingRequestDto;
import topg.bimber_user_service.dto.BookingResponseDto;
import topg.bimber_user_service.exceptions.ErrorResponse;
import topg.bimber_user_service.exceptions.SuccessResponse;
import topg.bimber_user_service.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/book")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> bookRoom(@RequestBody BookingRequestDto bookingRequestDto) {
        try {
            BookingResponseDto response = bookingService.bookRoom(bookingRequestDto);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Not Found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Server Error", e.getMessage()));
        }
    }


    @DeleteMapping("/cancel/{bookingId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId, @RequestParam String userId) {
        try {
            String response = bookingService.cancelBooking(bookingId, userId);
            return ResponseEntity.ok(new SuccessResponse("success", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("error", "An unexpected error occurred"));
        }
    }


    @PutMapping("/update/{bookingId}")
    public ResponseEntity<BookingResponseDto> updateBooking(@PathVariable Long bookingId, @RequestBody BookingRequestDto bookingRequestDto) {
        BookingResponseDto response = bookingService.updateBooking(bookingId, bookingRequestDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<BookingResponseDto>> listAllBookings() {
        List<BookingResponseDto> response = bookingService.listAllBookings();
        return ResponseEntity.ok(response);
    }
}
