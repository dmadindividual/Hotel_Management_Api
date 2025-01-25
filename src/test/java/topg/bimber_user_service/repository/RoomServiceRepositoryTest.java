package topg.bimber_user_service.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import topg.bimber_user_service.exceptions.UserNotFoundInDb;
import topg.bimber_user_service.models.Hotel;
import topg.bimber_user_service.models.Room;
import topg.bimber_user_service.models.RoomType;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)

public class RoomServiceRepositoryTest {
    @Autowired
    private RoomRepository roomRepository;
    private HotelRepository hotelRepository;

    private Room room;

    @BeforeEach
    void init(){
        // Arrange: Create a new Room object and set its properties
         room = new Room();
        room.setRoomType(RoomType.DELUXE);
        room.setPrice(BigDecimal.valueOf(4000));
        room.setAvailable(true);

    }


    @Test
    @DisplayName("Create Room")
    void createRoom() {


        room = roomRepository.save(room);

        // Assert: Verify that the room was saved correctly
        Optional<Room> savedRoom = roomRepository.findById(room.getId());
        assertTrue(savedRoom.isPresent(), "The room should be present in the repository");

        Room retrievedRoom = savedRoom.get();
        assertEquals(room.getId(), retrievedRoom.getId(), "The room ID should match");
        assertEquals(RoomType.DELUXE, retrievedRoom.getRoomType(), "The room type should match");
        assertEquals(BigDecimal.valueOf(4000), retrievedRoom.getPrice(), "The room price should match");
        assertTrue(retrievedRoom.isAvailable(), "The room should be available");
    }


    @Test
    @DisplayName("Edit Room By Id")
    void editRoomById() {


        // Act: Save the room to the repository
        room = roomRepository.save(room);

        room.setAvailable(false);
        room.setRoomType(RoomType.SINGLE);
        room.setPrice(BigDecimal.valueOf(50000));
        // Act: Save the room to the repository
        room = roomRepository.save(room);

        // Assert: Verify that the room was saved correctly
        Optional<Room> savedRoom = roomRepository.findById(room.getId());
        assertTrue(savedRoom.isPresent(), "The room should be present in the repository");

        Room retrievedRoom = savedRoom.get();
        assertEquals(room.getId(), retrievedRoom.getId(), "The room ID should match");
        assertEquals(RoomType.SINGLE, retrievedRoom.getRoomType(), "The room type should match");
        assertEquals(BigDecimal.valueOf(50000), retrievedRoom.getPrice(), "The room price should match");
        assertFalse(retrievedRoom.isAvailable(), "The room is not available");
    }


    @Test
    @DisplayName("Delete Room By Id")
    void deleteRoomById() {

        room = roomRepository.save(room); // Save room to database

        // Act: Delete the saved room by id
        roomRepository.deleteById(room.getId());

        // Assert: Verify that the room was deleted correctly
        Optional<Room> deletedRoom = roomRepository.findById(room.getId());
        assertTrue(deletedRoom.isEmpty(), "The room should not be present in the repository");
    }


}
