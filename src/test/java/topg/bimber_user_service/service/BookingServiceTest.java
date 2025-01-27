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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        // Create a User
        User user = new User();
        user.setUsername("Baki");
        user.setRole(Role.USER);
        user.setBalance(BigDecimal.valueOf(50000000));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // Create a Hotel
        Hotel hotel = new Hotel();
        hotel.setId(1L); // Ensure the ID matches the booking request
        hotel.setName("Happy Hotels");
        hotel.setLocation("Abuja");

        // Mock the hotelRepository to return the hotel when it's requested by ID
        when(hotelRepository.findById(hotel.getId())).thenReturn(Optional.of(hotel));

        // Create a Room and assign the Hotel
        Room room = new Room();
        room.setId(1L); // Ensure the ID matches the booking request
        room.setPrice(BigDecimal.valueOf(100000));
        room.setAvailable(true);
        room.setHotel(hotel); // Assign the correct hotel to the room
        room.setRoomType(RoomType.SINGLE);
        when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));
        when(roomRepository.existsByIdAndHotelId(room.getId(), hotel.getId())).thenReturn(true);

        // Create a Booking
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setPaid(true);
        booking.setRoom(room);
        booking.setUser(user);
        booking.setStatus(BookingStatus.CONFIRMED);

        // Mock Payment
        Payment payment = new Payment();
        payment.setBookingId(booking.getId());
        payment.setAmount(room.getPrice());
        payment.setSuccess(true);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);


        BookingRequestDto bookingRequestDto = new BookingRequestDto(
                user.getId(),
                room.getId(),
                hotel.getId(),
                LocalDate.now(),
                LocalDate.now(),
                true
        );

        // Mock Booking save
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        // Mock Mail Service
        doNothing().when(mailService).sendMail(any(NotificationEmail.class));

        // Call the method under test
        BookingResponseDto bookingResponseDto = bookingService.bookRoom(bookingRequestDto);

        // Assert and Verify
        assertThat(bookingResponseDto).isNotNull();
        assertThat(booking.isPaid()).isTrue();
        assertThat(booking.getRoom().getRoomType()).isEqualTo(room.getRoomType());

        verify(userRepository).findById(user.getId());
        verify(roomRepository).findById(room.getId());
        verify(roomRepository).existsByIdAndHotelId(room.getId(), hotel.getId());
        verify(hotelRepository).findById(hotel.getId());
        verify(bookingRepository, times(2)).save(any(Booking.class));
        verify(paymentRepository).save(any(Payment.class));
        verify(mailService).sendMail(any(NotificationEmail.class));
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
        verify(bookingRepository).findById(booking.getId());
        verify(bookingRepository).save(any(Booking.class));
        verify(roomRepository).save(any(Room.class));
    }


    @Test
    @DisplayName("List all Booking")
    void ListAllBookings(){
        User user = new User();
        user.setEmail("Baki");
        user.setUsername("fola");

        User user2 = new User();
        user2.setEmail("Baki");
        user2.setUsername("fola");

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
        booking.setUser(user);
        booking.setStatus(BookingStatus.CONFIRMED);

        Hotel hotel2 = new Hotel();
        hotel2.setName("Happy Hotels");
        hotel2.setLocation("Abuja");

        Room room2 = new Room();
        room2.setPrice(BigDecimal.valueOf(10000));
        room2.setAvailable(false);
        room2.setHotel(hotel);
        room2.setRoomType(RoomType.SINGLE);


        Booking booking2 = new Booking();
        booking2.setPaid(true);
        booking2.setRoom(room);
        booking2.setUser(user2);
        booking2.setStatus(BookingStatus.CONFIRMED);



        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        bookingList.add(booking2);

        when(bookingRepository.findAll()).thenReturn(bookingList);

        List<BookingResponseDto> bookingResponseDtos = bookingService.listAllBookings();

        assertThat(bookingResponseDtos).isNotNull();
        assertThat(bookingResponseDtos.size()).isEqualTo(2);

        verify(bookingRepository).findAll();

    }


}
