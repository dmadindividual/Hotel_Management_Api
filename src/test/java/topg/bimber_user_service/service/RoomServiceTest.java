package topg.bimber_user_service.service;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import topg.bimber_user_service.dto.RoomRequestDto;
import topg.bimber_user_service.dto.RoomResponseDto;
import topg.bimber_user_service.exceptions.UserNotFoundInDb;
import topg.bimber_user_service.models.Hotel;
import topg.bimber_user_service.models.Room;
import topg.bimber_user_service.models.RoomType;
import topg.bimber_user_service.repository.BookingRepository;
import topg.bimber_user_service.repository.HotelRepository;
import topg.bimber_user_service.repository.RoomRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
import static org.mockito.ArgumentMatchers.any;
import static reactor.core.publisher.Mono.when;

@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoomServiceTest {

    @InjectMocks
    private RoomService roomService;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private HotelRepository hotelRepository;

    @Test
    @DisplayName("Create room and save into DB")
    void createRoom() {
        // Arrange: Mock the hotel entity
        Hotel hotel = new Hotel();
        hotel.setName("Standard Hotel");
        hotel.setLocation("Lagos");

        // Stub the repository behavior for hotel lookup
        Mockito.when(hotelRepository.findById(hotel.getId())).thenReturn(Optional.of(hotel));

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

        // Stub the repository behavior for saving the room
        Mockito.when(roomRepository.save(any(Room.class))).thenReturn(room);

        // Act: Call the service method
        RoomResponseDto roomResponseDto = roomService.createRoom(roomRequestDto);

        // Assert: Verify the response
        assertThat(roomResponseDto).isNotNull();
        assertThat(roomResponseDto.roomType()).isEqualTo(RoomType.DELUXE);
        assertThat(roomResponseDto.isAvailable()).isTrue();

        // Verify the interactions with the repositories
        Mockito.verify(hotelRepository).findById(hotel.getId());
        Mockito.verify(roomRepository).save(any(Room.class));
    }


    @Test
    @DisplayName("Edit room by ID")
    void editRoomById() {
        // Arrange: Create and set up hotel entity with a valid ID
        Hotel hotel = new Hotel();
        hotel.setName("Standard Hotel");
        hotel.setLocation("Lagos");

        // Arrange: Create an existing room with a valid ID
        Room room = new Room();
        room.setRoomType(RoomType.DELUXE);
        room.setAvailable(true);
        room.setPrice(BigDecimal.valueOf(10000));
        room.setHotel(hotel);

        Mockito.when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));

        // Act: Update the room properties
        RoomRequestDto roomRequestDto = new RoomRequestDto(
                RoomType.SINGLE,
                BigDecimal.valueOf(500000),
                true,
                hotel.getId()
        );

        // Update mock room object to reflect changes
        room.setRoomType(RoomType.SINGLE);
        room.setPrice(BigDecimal.valueOf(500000));

        Mockito.when(roomRepository.save(any(Room.class))).thenReturn(room);

        RoomResponseDto roomResponseDto = roomService.editRoomById(room.getId(), roomRequestDto);

        // Assert: Verify the room has been updated correctly
        assertThat(roomResponseDto).isNotNull();
        assertThat(roomResponseDto.roomType()).isEqualTo(RoomType.SINGLE);
        assertThat(roomResponseDto.price()).isEqualByComparingTo(BigDecimal.valueOf(500000));
        assertThat(roomResponseDto.isAvailable()).isEqualTo(true);

        // Verify repository interactions
        Mockito.verify(roomRepository).findById(room.getId());
        Mockito.verify(roomRepository).save(any(Room.class));
    }



    @Test
    @DisplayName("Delete room By Id")
    void deleteRoomById(){

        Hotel hotel = new Hotel();
        hotel.setName("Standard Hotel");
        hotel.setLocation("Lagos");

        // Arrange: Create an existing room with a valid ID
        Room room = new Room();
        room.setRoomType(RoomType.DELUXE);
        room.setAvailable(true);
        room.setPrice(BigDecimal.valueOf(10000));
        room.setHotel(hotel);

        Mockito.when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));
        Mockito.doNothing().when(roomRepository).delete(any(Room.class));

        roomService.deleteRoomById(room.getId());



        Mockito.verify(roomRepository).findById(room.getId());
        Mockito.verify(roomRepository).delete(room);

    }

    @Test
    @DisplayName("Filter rooms by type")
    void filter() {
        // Arrange: Create hotel entities
        Hotel hotel = new Hotel();
        hotel.setName("Standard Hotel");
        hotel.setLocation("Lagos");

        Hotel hotel2 = new Hotel();
        hotel2.setName("Luxury Hotel");
        hotel2.setLocation("Abuja");

        // Arrange: Create rooms with valid IDs
        Room room = new Room();
        room.setRoomType(RoomType.DELUXE);
        room.setAvailable(true);
        room.setPrice(BigDecimal.valueOf(10000));
        room.setHotel(hotel);

        Room room2 = new Room();
        room2.setRoomType(RoomType.DELUXE);
        room2.setAvailable(true);
        room2.setPrice(BigDecimal.valueOf(10000));
        room2.setHotel(hotel2);  // Assigning to hotel2

        List<Room> rooms = new ArrayList<>();
        rooms.add(room);
        rooms.add(room2);

        // Mock the repository call
        Mockito.when(roomRepository.findByRoomType(RoomType.DELUXE)).thenReturn(rooms);

        // Act: Call the service method to filter rooms
        List<RoomResponseDto> list = roomService.filterRooms(RoomType.DELUXE.toString());

        // Assert: Verify the result
        assertThat(list).isNotNull();
        assertThat(list.size()).isEqualTo(2);

        // Verify repository interaction
        Mockito.verify(roomRepository).findByRoomType(RoomType.DELUXE);
    }



}


