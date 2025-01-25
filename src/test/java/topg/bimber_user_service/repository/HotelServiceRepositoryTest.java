package topg.bimber_user_service.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import topg.bimber_user_service.models.Hotel;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class HotelServiceRepositoryTest {

    @Autowired
    private HotelRepository hotelRepository;

    private Hotel hotel;

    @BeforeEach
    void init() {
        hotel = new Hotel();
        hotel.setLocation("Lagos");
        hotel.setName("Standard Hotel");
    }

    @Test
    @DisplayName("Hotel is created and saved in the database")
    void createHotel() {
        hotel = hotelRepository.save(hotel);

        Optional<Hotel> hotelOptional = hotelRepository.findById(hotel.getId());

        assertTrue(hotelOptional.isPresent(), "Hotel should be found in the repository");

        Hotel retrievedHotel = hotelOptional.get();

        assertNotNull(retrievedHotel, "The retrieved hotel should not be null");
        assertEquals(hotel.getId(), retrievedHotel.getId(), "Both Ids must match");
        assertEquals(hotel.getLocation(), retrievedHotel.getLocation(), "Locations should match");
        assertEquals("Standard Hotel", retrievedHotel.getName(), "Hotel names should match");
    }


    @Test
    @DisplayName("Get all hotels")
    void getAllHotels(){
        List<Hotel> list = new ArrayList<>();

        list.add(hotel);
        list.add(hotel);

        hotelRepository.saveAll(list);
        List<Hotel> hotels = hotelRepository.findAll();

        // Assert: Verify that the hotels are saved and retrieved correctly
        assertNotNull(hotels, "Hotel list should not be null");
        assertEquals(2, hotels.size(), "There should be two hotels in the list");
    }


    @Test
    @DisplayName("Edit Hotel By id")
    void editHotelById(){
        hotelRepository.save(hotel);

        hotel.setName("Lead way");
        hotel.setLocation("Kogi");
        hotelRepository.save(hotel);

        assertEquals("Kogi", hotel.getLocation());
        assertTrue(hotel.getName().equals("Lead way"));

    }

    @Test
    @DisplayName("Delete Hotel By Id")
    void deleteHotelById() {
        // Arrange: Save the hotel first
        hotel = hotelRepository.save(hotel);

        // Act: Delete the hotel
        hotelRepository.delete(hotel);

        // Assert: Ensure the hotel is no longer in the repository
        Optional<Hotel> hotelOption = hotelRepository.findById(hotel.getId());
        assertTrue(hotelOption.isEmpty(), "Hotel should not exist after deletion");
    }

}
