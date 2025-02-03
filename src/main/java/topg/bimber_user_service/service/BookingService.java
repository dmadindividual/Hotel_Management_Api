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
import topg.bimber_user_service.repository.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService implements IBookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final HotelRepository hotelRepository;
    private final MailService mailService;
    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    // Books a room for a user and processes payment.
    @Transactional
    @Override
    public BookingResponseDto bookRoom(BookingRequestDto bookingRequestDto) {
        User user = getEntityOrThrow(() -> userRepository.findById(bookingRequestDto.userId()), "User not found");
        Room room = getEntityOrThrow(() -> roomRepository.findById(bookingRequestDto.roomId()), "Room not found");
        Hotel hotel = getEntityOrThrow(() -> hotelRepository.findById(bookingRequestDto.hotelId()), "Hotel not found");

        validateBookingConditions(user, room, bookingRequestDto);

        BigDecimal totalPrice = calculateTotalPrice(room.getPrice(), bookingRequestDto.startDate(), bookingRequestDto.endDate());

        user.setBalance(user.getBalance().subtract(totalPrice));

        Booking booking = createBooking(user, room, hotel, bookingRequestDto, totalPrice);

        createPayment(user, totalPrice, booking.getId());

        room.setAvailable(false);
        roomRepository.save(room);

        scheduleRoomAvailabilityReset(room, bookingRequestDto.endDate());

        sendConfirmationEmail(user, room, booking);

        return buildBookingResponseDto(booking);
    }

    private void validateBookingConditions(User user, Room room, BookingRequestDto bookingRequestDto) {
        LocalDate startDate = bookingRequestDto.startDate();
        LocalDate endDate = bookingRequestDto.endDate();

        if (!room.isAvailable()) {
            throw new IllegalStateException("Room is not available");
        }

        if (startDate.isBefore(LocalDate.now())) {
            throw new IllegalStateException("Cannot book a room for past dates");
        }

        if (endDate.isBefore(startDate)) {
            throw new IllegalStateException("End date cannot be before start date");
        }

        if (bookingRepository.existsByUserIdAndHotelIdAndStatus(user.getId(), bookingRequestDto.hotelId(), BookingStatus.CONFIRMED)) {
            throw new IllegalStateException("You already have an active booking for this hotel");
        }

        if (bookingRepository.existsByRoomIdAndDatesOverlap(room.getId(), startDate, endDate)) {
            throw new IllegalStateException("Room is already booked for the selected dates");
        }

        if (user.getBalance().compareTo(room.getPrice()) < 0) {
            throw new IllegalStateException("Insufficient balance to book the room");
        }
    }

    private BigDecimal calculateTotalPrice(BigDecimal pricePerNight, LocalDate startDate, LocalDate endDate) {
        long numberOfDays = ChronoUnit.DAYS.between(startDate, endDate);
        return pricePerNight.multiply(BigDecimal.valueOf(numberOfDays));
    }

    private Booking createBooking(User user, Room room, Hotel hotel, BookingRequestDto bookingRequestDto, BigDecimal totalPrice) {
        Booking booking = Booking.builder()
                .user(user)
                .room(room)
                .hotel(hotel)
                .startDate(bookingRequestDto.startDate())
                .endDate(bookingRequestDto.endDate())
                .status(BookingStatus.PENDING)
                .isPaid(true)
                .totalPrice(totalPrice)
                .build();
        return bookingRepository.save(booking);
    }

    private void createPayment(User user, BigDecimal amount, Long bookingId) {
        Payment payment = Payment.builder()
                .bookingId(bookingId)
                .amount(amount)
                .success(true)
                .user(user)
                .build();
        paymentRepository.save(payment);
    }

    @Async
    private void scheduleRoomAvailabilityReset(Room room, LocalDate endDate) {
        long delay = ChronoUnit.MILLIS.between(LocalDateTime.now(), endDate.atStartOfDay());

        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            room.setAvailable(true);
            roomRepository.save(room);
        }, delay, TimeUnit.MILLISECONDS);
    }

    private void sendConfirmationEmail(User user, Room room, Booking booking) {
        Hotel hotel = room.getHotel();
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

        try {
            mailService.sendMail(new NotificationEmail(
                    "Room Successfully Booked",
                    user.getEmail(),
                    emailMessage
            ));
        } catch (Exception e) {
            log.error("Failed to send booking confirmation email to user: {}", user.getEmail(), e);
        }
    }

    private BookingResponseDto buildBookingResponseDto(Booking booking) {
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

    private <T> T getEntityOrThrow(Supplier<Optional<T>> supplier, String errorMessage) {
        return supplier.get().orElseThrow(() -> new IllegalArgumentException(errorMessage));
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
