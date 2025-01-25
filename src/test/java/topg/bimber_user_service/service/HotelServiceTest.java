package topg.bimber_user_service.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import topg.bimber_user_service.dto.HotelDto;
import topg.bimber_user_service.dto.HotelRequestDto;
import topg.bimber_user_service.dto.HotelResponseDto;
import topg.bimber_user_service.models.Hotel;
import topg.bimber_user_service.repository.HotelRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)

public class HotelServiceTest {

    @InjectMocks
    private HotelService hotelService;

    @Mock
    private HotelRepository hotelRepository;

    @Test
    @DisplayName("Save Hotel to db")
    void saveHotel() {
        // Arrange: Create input DTO
        HotelRequestDto hotelDto = new HotelRequestDto("Standard Hotel", "Lagos", null);

        // Mock the hotel entity (the actual model used by the repository)
        Hotel hotelEntity = new Hotel();
        hotelEntity.setName("Standard Hotel");
        hotelEntity.setLocation("Lagos");


        // Mock the repository to return the hotel entity when saved
        when(hotelRepository.save(any(Hotel.class))).thenReturn(hotelEntity);

        // Act: Call the service method
        HotelResponseDto savedHotelResponse = hotelService.createHotel(hotelDto);

        // Assert: Verify response properties
        assertThat(savedHotelResponse).isNotNull();
        assertThat(savedHotelResponse.success()).isTrue();

        // Extract and check the hotel details from the response
        HotelDto savedHotelDto = savedHotelResponse.hotel();
        assertThat(savedHotelDto.name()).isEqualTo("Standard Hotel");
        assertThat(savedHotelDto.location()).isEqualTo("Lagos");

        // Verify repository interactions
        Mockito.verify(hotelRepository).save(any(Hotel.class));
    }

    @Test
    @DisplayName("Get list of hotels from db")
    void getListOfHotels() {
        // Arrange: Create hotel entities
        Hotel hotel1 = new Hotel();
        hotel1.setName("Standard Hotel");
        hotel1.setLocation("Lagos");

        Hotel hotel2 = new Hotel();
        hotel2.setName("Luxury Hotel");
        hotel2.setLocation("Abuja");

        List<Hotel> hotels = new ArrayList<>();
        hotels.add(hotel1);
        hotels.add(hotel2);

        // Mock repository to return list of hotels
        when(hotelRepository.findAll()).thenReturn(hotels);

        // Act: Call service method to retrieve hotels
        List<HotelDto> hotelDtos = hotelService.getAllHotels();

        // Assert: Verify that the returned list is not null and has expected data
        assertThat(hotelDtos).isNotNull();
        assertThat(hotelDtos.size()).isEqualTo(2);
        assertThat(hotelDtos.get(0).name()).isEqualTo("Standard Hotel");
        assertThat(hotelDtos.get(1).location()).isEqualTo("Abuja");

        // Verify repository interaction
        Mockito.verify(hotelRepository).findAll();
    }

    @Test
    @DisplayName("Edit Hotel by id")
    void editHotelById() {
        // Arrange: Create hotel entity
        Hotel hotel1 = new Hotel();
        hotel1.setName("Standard Hotel");
        hotel1.setLocation("Lagos");

        // Mock repository to return hotel when searched by ID
        when(hotelRepository.findById(hotel1.getId())).thenReturn(Optional.of(hotel1));

        // Simulate the update
        Hotel updatedHotel = new Hotel();
        updatedHotel.setName("Bimber Hotel");
        updatedHotel.setLocation("Kaduna");

        // Mock saving the updated hotel
        when(hotelRepository.save(any(Hotel.class))).thenReturn(updatedHotel);

        // Act: Call service method to update hotel
        HotelResponseDto updatedHotelDto = hotelService.editHotelById(hotel1.getId(), new HotelRequestDto( "Bimber Hotel", "Kaduna", null));

        // Assert: Verify updated values
        assertThat(updatedHotelDto).isNotNull();
        assertThat(updatedHotelDto.hotel().name()).isEqualTo("Bimber Hotel");
        assertThat(updatedHotelDto.hotel().location()).isEqualTo("Kaduna");

        // Verify repository interactions
        Mockito.verify(hotelRepository).findById(hotel1.getId());
        Mockito.verify(hotelRepository).save(any(Hotel.class));
    }


    @Test
    @DisplayName("Delete Hotel By Id")
    void deleteHotelById() {
        // Arrange: Create hotel entity
        Hotel hotel1 = new Hotel();
        hotel1.setName("Standard Hotel");
        hotel1.setLocation("Lagos");

        // Mock repository to return hotel when searched by ID
        when(hotelRepository.findById(hotel1.getId())).thenReturn(Optional.of(hotel1));

        // Mock delete operation (since it returns void)
        Mockito.doNothing().when(hotelRepository).delete(any(Hotel.class));

        // Act: Call the service method to delete the hotel
        hotelService.deleteHotelById(hotel1.getId());

        // Assert: Verify repository interactions
        Mockito.verify(hotelRepository).findById(hotel1.getId());
        Mockito.verify(hotelRepository).delete(hotel1);
    }


}
