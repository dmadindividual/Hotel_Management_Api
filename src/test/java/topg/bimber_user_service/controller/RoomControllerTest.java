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
import topg.bimber_user_service.dto.RoomRequestDto;
import topg.bimber_user_service.dto.RoomResponseDto;
import topg.bimber_user_service.models.Hotel;
import topg.bimber_user_service.models.Room;
import topg.bimber_user_service.models.RoomType;
import topg.bimber_user_service.service.HotelService;
import topg.bimber_user_service.service.RoomService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RoomControllerTest {

    @MockitoBean
    private RoomService roomService;  // Use @MockBean to mock the service

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Create Room Controller")
    @WithMockUser(username = "user", roles = "ADMIN")
    void createRoom() throws Exception {
        Hotel hotel = new Hotel();
        hotel.setName("Standard Hotel");
        hotel.setLocation("Lagos");


        // Create a room object and set properties
        Room room = new Room();
        room.setRoomType(RoomType.DELUXE);
        room.setAvailable(true);
        room.setPrice(BigDecimal.valueOf(10000));
        room.setHotel(hotel);

        // Create a request DTO for the room
        RoomRequestDto roomRequestDto = new RoomRequestDto(
                room.getRoomType(),
                room.getPrice(),
                room.isAvailable(),
                hotel.getId()
        );

        RoomResponseDto roomResponseDto = new RoomResponseDto(
                room.getId(),
                room.getRoomType(),
                room.getPrice(),
                room.isAvailable(),
                room.getHotel().getId()
        );

        when(roomService.createRoom(roomRequestDto)).thenReturn(roomResponseDto);  // Ensure this is a method on the mock

        // Act: Perform the POST request
        this.mockMvc.perform(post("/api/v1/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomRequestDto)))
                .andExpect(status().isCreated());

    }



    @Test
    @DisplayName("Delete Room Controller")
    @WithMockUser(username = "user", roles = "ADMIN")
    void deleteRoom() throws Exception {
        // Arrange: Create hotel and room objects with IDs set
        Hotel hotel = new Hotel();
        hotel.setId(1L);
        hotel.setName("Standard Hotel");
        hotel.setLocation("Lagos");

        Room room = new Room();
        room.setId(1L);
        room.setRoomType(RoomType.DELUXE);
        room.setAvailable(true);
        room.setPrice(BigDecimal.valueOf(10000));
        room.setHotel(hotel);
        String message = "deleted";
        // Mocking the delete method
        when(roomService.deleteRoomById(room.getId())).thenReturn(message);

        // Act: Perform the DELETE request
        this.mockMvc.perform(delete("/api/v1/rooms/{id}", room.getId()))
                .andExpect(status().isOk());  // Ensure the expected response matches the controller
    }


    @Test
    @DisplayName("Find All Available Rooms")
    @WithMockUser(username = "user", roles = "ADMIN")
    void findAllAvailableRooms() throws Exception {
        Hotel hotel = new Hotel();
        hotel.setName("Standard Hotel");
        hotel.setLocation("Lagos");

        Room room = new Room();
        room.setRoomType(RoomType.DELUXE);
        room.setAvailable(true);
        room.setPrice(BigDecimal.valueOf(10000));
        room.setHotel(hotel);

        Hotel hotel2 = new Hotel();
        hotel2.setName("Lower Hotel");
        hotel2.setLocation("Ibadan");

        Room room2 = new Room();
        room2.setRoomType(RoomType.SUITE);
        room2.setAvailable(true);
        room2.setPrice(BigDecimal.valueOf(15000));
        room2.setHotel(hotel2);

        Hotel hotel3 = new Hotel();
        hotel3.setName("Oriental Hotel");
        hotel3.setLocation("Lagos");

        Room room3 = new Room();
        room3.setRoomType(RoomType.SINGLE);
        room3.setAvailable(true);
        room3.setPrice(BigDecimal.valueOf(8000));
        room3.setHotel(hotel3);

        RoomResponseDto roomResponseDto = new RoomResponseDto(room.getId(), room.getRoomType(), room.getPrice(), room.isAvailable(), room.getHotel().getId());
        RoomResponseDto roomResponseDto2 = new RoomResponseDto(room2.getId(), room2.getRoomType(), room2.getPrice(), room2.isAvailable(), room2.getHotel().getId());
        RoomResponseDto roomResponseDto3 = new RoomResponseDto(room3.getId(), room3.getRoomType(), room3.getPrice(), room3.isAvailable(), room3.getHotel().getId());

        List<RoomResponseDto> roomResponseDtoList = List.of(roomResponseDto3, roomResponseDto2, roomResponseDto);

        when(roomService.findAllAvailableRooms()).thenReturn(roomResponseDtoList);

        // Act: Perform the GET request
        this.mockMvc.perform(get("/api/v1/rooms/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(roomResponseDtoList.size()))
                .andExpect(jsonPath("$[0].roomType").value("SINGLE"))
                .andExpect(jsonPath("$[0].price").value(8000))
                .andExpect(jsonPath("$[1].roomType").value("SUITE"))
                .andExpect(jsonPath("$[1].price").value(15000))
                .andExpect(jsonPath("$[2].roomType").value("DELUXE"))
                .andExpect(jsonPath("$[2].price").value(10000))
                .andExpect(jsonPath("$[0].isAvailable").value(true))
                .andExpect(jsonPath("$[1].hotelId").value(hotel2.getId()))
                .andExpect(jsonPath("$[2].hotelId").value(hotel.getId()));
    }


    @Test
    @DisplayName("Find All Available Rooms By hotelID")
    @WithMockUser(username = "user", roles = "ADMIN")
    void findAllRoomsByHotel() throws Exception {
        Hotel hotel = new Hotel();
        hotel.setName("Standard Hotel");
        hotel.setId(1L);
        hotel.setLocation("Lagos");

        Room room = new Room();
        room.setRoomType(RoomType.DELUXE);
        room.setAvailable(true);
        room.setPrice(BigDecimal.valueOf(10000));
        room.setHotel(hotel);

        room.setRoomType(RoomType.SINGLE);
        room.setAvailable(false);
        room.setPrice(BigDecimal.valueOf(20000));
        room.setHotel(hotel);

        Hotel hotel2 = new Hotel();
        hotel2.setName("Lower Hotel");
        hotel2.setLocation("Ibadan");

        Room room2 = new Room();
        room2.setRoomType(RoomType.SUITE);
        room2.setAvailable(true);
        room2.setPrice(BigDecimal.valueOf(15000));
        room2.setHotel(hotel2);



        RoomResponseDto roomResponseDto = new RoomResponseDto(room.getId(), room.getRoomType(), room.getPrice(), room.isAvailable(), room.getHotel().getId());
        RoomResponseDto roomResponseDto2 = new RoomResponseDto(room2.getId(), room2.getRoomType(), room2.getPrice(), room2.isAvailable(), room2.getHotel().getId());

        List<RoomResponseDto> roomResponseDtoList = List.of( roomResponseDto2, roomResponseDto);

        when(roomService.findAllRoomsByHotelId(room.getHotel().getId())).thenReturn(roomResponseDtoList);

        this.mockMvc.perform(get("/api/v1/rooms//hotel/{hotelId}", hotel.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(roomResponseDtoList.size()))
                .andExpect(jsonPath("$[0].roomType").value(roomResponseDto2.roomType().name()))
                .andExpect(jsonPath("$[0].price").value(roomResponseDto2.price().intValue()))
                .andExpect(jsonPath("$[0].isAvailable").value(roomResponseDto2.isAvailable()))
                .andExpect(jsonPath("$[0].hotelId").value(roomResponseDto2.hotelId()))
                .andExpect(jsonPath("$[1].roomType").value(roomResponseDto.roomType().name()))
                .andExpect(jsonPath("$[1].price").value(roomResponseDto.price().intValue()))
                .andExpect(jsonPath("$[1].isAvailable").value(roomResponseDto.isAvailable()))
                .andExpect(jsonPath("$[1].hotelId").value(roomResponseDto.hotelId()))
                .andExpect(jsonPath("$[*].roomType").isNotEmpty())
                .andExpect(jsonPath("$[*].isAvailable").isNotEmpty())
                .andExpect(jsonPath("$[*].price").isNotEmpty());
    }
}

