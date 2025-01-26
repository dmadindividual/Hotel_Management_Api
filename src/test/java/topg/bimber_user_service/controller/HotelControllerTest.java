package topg.bimber_user_service.controller;



import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import topg.bimber_user_service.dto.HotelRequestDto;
import topg.bimber_user_service.dto.HotelResponseDto;
import topg.bimber_user_service.dto.HotelDto;
import topg.bimber_user_service.service.HotelService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is; // Correct import
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class HotelControllerTest {

    @MockitoBean
    private HotelService hotelService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Create Hotel Controller")
    @WithMockUser(username = "user", roles = "ADMIN")
    void createHotel() throws Exception {
        // Arrange: Create input DTO
        HotelRequestDto hotelRequestDto = new HotelRequestDto("Standard Hotel", "Lagos", null);
        HotelDto hotelDto = new HotelDto(1L, "Standard Hotel", "Lagos");
        HotelResponseDto hotelResponseDto = new HotelResponseDto(true, hotelDto, null);

        // Mock the service method to return the expected DTO
        when(hotelService.createHotel(hotelRequestDto)).thenReturn(hotelResponseDto);  // Ensure this is a method on the mock

        // Act: Perform the POST request
        this.mockMvc.perform(post("/api/v1/hotel/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hotelRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get All Hotels Controller")
    @WithMockUser(username = "user", roles = "ADMIN")
    void getAllHotels() throws Exception {

        HotelDto hotelDto = new HotelDto(1L, "Standard Hotel", "Lagos");
        HotelDto hotelDto1 = new HotelDto(2L, "Good Hotel", "Abuja");
        List<HotelDto> hotelDtos = new ArrayList<>();
        hotelDtos.add(hotelDto1);
        hotelDtos.add(hotelDto);

        when(hotelService.getAllHotels()).thenReturn(hotelDtos);

        this.mockMvc.perform(get("/api/v1/hotel/hotels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(hotelDtos.size())));
    }


    @Test
    @DisplayName("Get Hotel By Id")
    @WithMockUser(username = "user", roles = "ADMIN")
    void getHotelById() throws Exception {
        HotelRequestDto hotelRequestDto = new HotelRequestDto("Standard Hotel", "Lagos", null);
        HotelDto hotelDto = new HotelDto(1L, "Standard Hotel", "Lagos");
        HotelResponseDto hotelResponseDto = new HotelResponseDto(true, hotelDto, null);

        when(hotelService.getHotelById(hotelDto.id())).thenReturn(hotelResponseDto);


        this.mockMvc.perform(get("/api/v1/hotel/hotels/{id}", hotelDto.id()))
                .andExpect(status().isOk());


    }


    @Test
    @DisplayName("Delete Hotel By Id")
    @WithMockUser(username = "user", roles = "ADMIN")
    void deleteHotelById() throws Exception {
        HotelDto hotelDto = new HotelDto(1L, "Standard Hotel", "Lagos");
String message = "deleted";
        // Mocking the delete method
        when(hotelService.deleteHotelById(hotelDto.id())).thenReturn(message);

        // Act: Perform the DELETE request
        this.mockMvc.perform(delete("/api/v1/hotel/delete/{id}", hotelDto.id()))
                .andExpect(status().isOk());
    }


}
