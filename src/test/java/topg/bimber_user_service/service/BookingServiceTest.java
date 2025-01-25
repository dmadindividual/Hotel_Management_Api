package topg.bimber_user_service.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import topg.bimber_user_service.dto.BookingRequestDto;
import topg.bimber_user_service.dto.BookingResponseDto;
import topg.bimber_user_service.mail.MailService;
import topg.bimber_user_service.models.*;
import topg.bimber_user_service.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookingServiceTest {

    @InjectMocks
    private BookingService bookingService;

    @Mock
    private MailService mailService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private PaymentRepository paymentRepository;


    @Test
    @DisplayName("book a room")
    void bookRoom() {
        User user = new User();
        user.setUsername("Baki");
        user.setRole(Role.USER);
        user.setBalance(BigDecimal.valueOf(50000000));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        Hotel hotel = new Hotel();
        hotel.setName("Happy Hotels");
        hotel.setLocation("Abuja");
        hotel.setId(1L);


        Room room = new Room();
        room.setPrice(BigDecimal.valueOf(100000));
        room.setAvailable(true);
        room.setHotel(hotel);
        room.setPrice(BigDecimal.valueOf(10000));
        room.setRoomType(RoomType.SINGLE);

        when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));

        Booking booking = new Booking();
        booking.setPaid(true);
        booking.setRoom(room);
        booking.setUser(user);
        booking.setStatus(BookingStatus.CONFIRMED);


        Payment payment = new Payment();
        payment.setBookingId(booking.getId());
        payment.setAmount(room.getPrice());
        payment.setSuccess(true);

        Mockito.when(paymentRepository.save(any(Payment.class))).thenReturn(payment);


        BookingRequestDto bookingRequestDto = new BookingRequestDto(
                user.getId(),
                room.getId(),
                hotel.getId(),
                LocalDate.now(),
                LocalDate.now(),
                true

        );

        Mockito.when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        Mockito.doNothing().when(mailService).sendMail(any(NotificationEmail.class));


        BookingResponseDto bookingResponseDto = bookingService.bookRoom(bookingRequestDto);

        assertThat(bookingResponseDto).isNotNull();
        assertThat(booking.isPaid()).isTrue();
        assertThat(booking.getRoom().getRoomType()).isEqualTo(room.getRoomType());

        Mockito.verify(userRepository).findById(user.getId());
        Mockito.verify(roomRepository).findById(room.getId());
        Mockito.verify(bookingRepository, Mockito.times(2)).save(any(Booking.class));
        Mockito.verify(paymentRepository).save(any(Payment.class));
        Mockito.verify(mailService).sendMail(any(NotificationEmail.class));


    }


    @Test
    @DisplayName("Cancel Booking")
    void cancelBooking() {
        Hotel hotel = new Hotel();
        hotel.setName("Happy Hotels");
        hotel.setLocation("Abuja");

        Room room = new Room();
        room.setPrice(BigDecimal.valueOf(10000));
        room.setAvailable(false);
        room.setHotel(hotel);
        room.setRoomType(RoomType.SINGLE);


        Booking booking = new Booking();
        booking.setPaid(true);
        booking.setRoom(room);
        booking.setStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        booking.setStatus(BookingStatus.CANCELLED);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        room.setAvailable(true);
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        bookingService.cancelBooking(booking.getId());

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        assertThat(room.isAvailable()).isTrue();

        // Verify interactions
        Mockito.verify(bookingRepository).findById(booking.getId());
        Mockito.verify(bookingRepository).save(any(Booking.class));
        Mockito.verify(roomRepository).save(any(Room.class));
    }



}
