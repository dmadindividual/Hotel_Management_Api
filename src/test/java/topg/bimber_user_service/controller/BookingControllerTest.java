package topg.bimber_user_service.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import topg.bimber_user_service.dto.BookingRequestDto;
import topg.bimber_user_service.dto.BookingResponseDto;
import topg.bimber_user_service.models.*;
import topg.bimber_user_service.service.BookingService;
import topg.bimber_user_service.service.HotelService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BookingControllerTest {
    @MockitoBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Book a room")
    @WithMockUser(username = "user", roles = "USER")
    void bookRoom() throws Exception {
        User user = new User();
        user.setUsername("Baki");
        user.setRole(Role.USER);
        user.setBalance(BigDecimal.valueOf(50000000));

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

        Booking booking = new Booking();
        booking.setPaid(true);
        booking.setRoom(room);
        booking.setUser(user);
        booking.setStatus(BookingStatus.CONFIRMED);

        Payment payment = new Payment();
        payment.setBookingId(booking.getId());
        payment.setAmount(room.getPrice());
        payment.setSuccess(true);



        BookingRequestDto bookingRequestDto = new BookingRequestDto(
                user.getId(),
                room.getId(),
                hotel.getId(),
                LocalDate.now(),
                LocalDate.now(),
                true

        );

        BookingResponseDto bookingResponseDto = new BookingResponseDto(booking.getId(), booking.getUser().getId(), booking.getRoom().getId(), booking.getStartDate(), booking.getEndDate(), booking.getStatus().toString(), booking.isPaid());
        when(bookingService.bookRoom(bookingRequestDto)).thenReturn(bookingResponseDto);


        this.mockMvc.perform(post("/api/v1/bookings/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isOk());


    }



    @Test
    @DisplayName("Cancel a booking")
    @WithMockUser(username = "user", roles = "ADMIN")
    void cancelBooking() throws Exception {
        User user = new User();
        user.setUsername("Baki");
        user.setRole(Role.USER);
        user.setBalance(BigDecimal.valueOf(50000000));

        Hotel hotel = new Hotel();
        hotel.setName("Happy Hotels");
        hotel.setLocation("Abuja");
        hotel.setId(1L);

        Room room = new Room();
        room.setPrice(BigDecimal.valueOf(10000));
        room.setAvailable(true);
        room.setHotel(hotel);
        room.setRoomType(RoomType.SINGLE);

        Booking booking = new Booking();
        booking.setId(1L);  // Set ID to avoid null pointer issues in tests
        booking.setPaid(true);
        booking.setRoom(room);
        booking.setUser(user);
        booking.setStatus(BookingStatus.CONFIRMED);

        Payment payment = new Payment();
        payment.setBookingId(booking.getId());
        payment.setAmount(room.getPrice());
        payment.setSuccess(true);

        String message = "successfully cancelled Booking";

        when(bookingService.cancelBooking(booking.getId())).thenReturn(message);

        this.mockMvc.perform(put("/api/v1/bookings/cancel/{bookingId}", booking.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(message));
    }



}
