package topg.bimber_user_service.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import topg.bimber_user_service.dto.BookingRequestDto;
import topg.bimber_user_service.dto.BookingResponseDto;
import topg.bimber_user_service.exceptions.UserNotFoundInDb;
import topg.bimber_user_service.mail.MailService;
import topg.bimber_user_service.models.*;
import topg.bimber_user_service.repository.BookingRepository;
import topg.bimber_user_service.repository.PaymentRepository;
import topg.bimber_user_service.repository.RoomRepository;
import topg.bimber_user_service.repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService implements IBookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final MailService mailService;
    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    // Books a room for a user and processes payment.
    @Transactional
    @Override
    @CachePut(value = "bookings", key = "#result.id")
    public BookingResponseDto bookRoom(BookingRequestDto bookingRequestDto) {
        User user = userRepository.findById(bookingRequestDto.userId())
                .orElseThrow(() -> new UserNotFoundInDb("User not found"));

        boolean hasActiveBooking = bookingRepository.existsByUserIdAndStatus(user.getId(), BookingStatus.CONFIRMED);
        if (hasActiveBooking) {
            throw new IllegalStateException("You already have an active booking. Please complete or cancel it before making a new one.");
        }

        Room room = roomRepository.findById(bookingRequestDto.roomId())
                .orElseThrow(() -> new UserNotFoundInDb("Room not found"));

        Hotel hotel = room.getHotel();

        if (!Objects.equals(hotel.getId(), bookingRequestDto.hotelId())) {
            throw new IllegalArgumentException("Room does not belong to the specified hotel");
        }

        if (!room.isAvailable()) {
            throw new IllegalStateException("Room is not available");
        }

        if (user.getBalance().compareTo(room.getPrice()) < 0) {
            throw new IllegalStateException("Insufficient balance to book the room");
        }

        BigDecimal newBalance = user.getBalance().subtract(room.getPrice());
        user.setBalance(newBalance);
        userRepository.save(user);

        Booking booking = Booking.builder()
                .user(user)
                .room(room)
                .startDate(bookingRequestDto.startDate())
                .endDate(bookingRequestDto.endDate())
                .status(BookingStatus.PENDING)
                .isPaid(true)
                .build();

        booking = bookingRepository.save(booking);

        Payment payment = Payment.builder()
                .bookingId(booking.getId())
                .amount(room.getPrice())
                .success(true)
                .user(user)
                .build();
        paymentRepository.save(payment);

        room.setAvailable(false);
        roomRepository.save(room);

        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        try {
            String emailMessage = String.format(
                    "Dear %s,\n\nThank you for booking a room at %s.\n\n" +
                            "Your booking details:\n- Hotel: %s\n- Room: %s\n- Start Date: %s\n- End Date: %s\n\n" +
                            "Your booking has been confirmed successfully. We look forward to hosting you!\n\n" +
                            "Best regards,\n%s Team",
                    user.getUsername(),
                    hotel.getName(),
                    hotel.getName(),
                    room.getRoomType(),
                    booking.getStartDate(),
                    booking.getEndDate(),
                    hotel.getName()
            );

            mailService.sendMail(new NotificationEmail(
                    "Room Successfully Booked",
                    user.getEmail(),
                    emailMessage
            ));
        } catch (Exception e) {
            log.error("Failed to send booking confirmation email to user: {}", user.getEmail(), e);
        }

        return new BookingResponseDto(
                booking.getId(),
                booking.getUser().getId(),
                booking.getRoom().getId(),
                booking.getStartDate(),
                booking.getEndDate(),
                booking.getStatus().name(),
                booking.isPaid()
        );
    }

    // Cancels an existing booking.
    @Override
    @CacheEvict(value = "bookings", key = "#bookingId")
    public String cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new UserNotFoundInDb("Booking not found"));

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        Room room = booking.getRoom();
        room.setAvailable(true);
        roomRepository.save(room);

        return "Booking with ID " + bookingId + " has been cancelled successfully";
    }

    // Updates an existing booking.
    @Override
    @CachePut(value = "bookings", key = "#bookingId")
    public BookingResponseDto updateBooking(Long bookingId, BookingRequestDto bookingRequestDto) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new UserNotFoundInDb("Booking not found"));

        Room room = roomRepository.findById(bookingRequestDto.roomId())
                .orElseThrow(() -> new UserNotFoundInDb("Room not found"));

        booking.setRoom(room);
        booking.setStartDate(bookingRequestDto.startDate());
        booking.setEndDate(bookingRequestDto.endDate());
        booking.setPaid(Boolean.TRUE.equals(bookingRequestDto.isPaid()));

        booking = bookingRepository.save(booking);

        return new BookingResponseDto(
                booking.getId(),
                booking.getUser().getId(),
                booking.getRoom().getId(),
                booking.getStartDate(),
                booking.getEndDate(),
                booking.getStatus().name(),
                booking.isPaid()
        );
    }

    // Retrieves all bookings.
    @Override
    @Cacheable(value = "bookings")
    public List<BookingResponseDto> listAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        return bookings.stream()
                .map(booking -> new BookingResponseDto(
                        booking.getId(),
                        booking.getUser().getId(),
                        booking.getRoom().getId(),
                        booking.getStartDate(),
                        booking.getEndDate(),
                        booking.getStatus().name(),
                        booking.isPaid()
                ))
                .collect(Collectors.toList());
    }
}
