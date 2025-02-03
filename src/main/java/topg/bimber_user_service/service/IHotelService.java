package topg.bimber_user_service.service;

import org.springframework.web.multipart.MultipartFile;
import topg.bimber_user_service.dto.HotelDto;
import topg.bimber_user_service.dto.HotelDtoFilter;
import topg.bimber_user_service.dto.HotelRequestDto;
import topg.bimber_user_service.dto.HotelResponseDto;
import topg.bimber_user_service.models.Hotel;

import java.util.List;

public interface IHotelService {
    HotelResponseDto createHotel(String name,  String state,String location, String description, List<String> amenities, List<MultipartFile> pictures);
    List<HotelDtoFilter> getHotelsByState(String stateName);
    String editHotelById(Long id, HotelRequestDto hotelRequestDto);
    HotelDtoFilter getHotelById(Long id);
    String deleteHotelById(Long id);
    Integer getTotalHotelsInState(String state);

}
