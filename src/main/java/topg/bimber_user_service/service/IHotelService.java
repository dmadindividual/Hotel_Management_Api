package topg.bimber_user_service.service;

import topg.bimber_user_service.dto.HotelDto;
import topg.bimber_user_service.dto.HotelRequestDto;
import topg.bimber_user_service.dto.HotelResponseDto;
import topg.bimber_user_service.models.Hotel;

import java.util.List;

public interface IHotelService {
    HotelResponseDto createHotel(HotelRequestDto hotelRequestDto);
    List<HotelDto>  getAllHotels();
    HotelResponseDto editHotelById(Long id, HotelRequestDto hotelRequestDto);
    String deleteHotelById(Long id);
HotelResponseDto getHotelById(Long id);
}
