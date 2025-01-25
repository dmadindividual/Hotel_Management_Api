package topg.bimber_user_service.service;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import topg.bimber_user_service.dto.HotelDto;
import topg.bimber_user_service.dto.HotelRequestDto;
import topg.bimber_user_service.dto.HotelResponseDto;
import topg.bimber_user_service.dto.RoomResponseDto;
import topg.bimber_user_service.exceptions.InvalidUserInputException;
import topg.bimber_user_service.models.Hotel;
import topg.bimber_user_service.models.Room;
import topg.bimber_user_service.repository.HotelRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class HotelService implements IHotelService {

    private final HotelRepository hotelRepository;

    // Creates a new hotel and saves it in the repository
    @Transactional
    @Override
    @CacheEvict(value = {"hotel", "hotels"}, allEntries = true)
    public HotelResponseDto createHotel(HotelRequestDto hotelRequestDto) {
        if (StringUtils.isBlank(hotelRequestDto.name()) ||
                StringUtils.isBlank(hotelRequestDto.location())) {
            throw new InvalidUserInputException("Hotel name or location cannot be empty");
        } else if (hotelRepository.findByName(hotelRequestDto.name()).isPresent()) {
            throw new InvalidUserInputException("Name is already Taken");
        }

        Hotel hotel = Hotel.builder()
                .name(hotelRequestDto.name())
                .location(hotelRequestDto.location())
                .build();

        if (hotelRequestDto.rooms() != null && !hotelRequestDto.rooms().isEmpty()) {
            Hotel finalHotel = hotel;
            List<Room> rooms = hotelRequestDto.rooms().stream()
                    .map(roomRequestDto -> Room.builder()
                            .roomType(roomRequestDto.getRoomType())
                            .price(roomRequestDto.getPrice())
                            .isAvailable(true)
                            .hotel(finalHotel)
                            .build())
                    .collect(Collectors.toList());
            hotel.setRooms(rooms);
        }

        hotel = hotelRepository.save(hotel);

        HotelDto hotelDto = new HotelDto(hotel.getId(), hotel.getName(), hotel.getLocation());
        Hotel finalHotel1 = hotel;
        List<RoomResponseDto> roomResponseDtos = hotel.getRooms() != null
                ? hotel.getRooms().stream()
                .map(room -> new RoomResponseDto(
                        room.getId(),
                        room.getRoomType(),
                        room.getPrice(),
                        room.isAvailable(),
                        finalHotel1.getId()
                ))
                .collect(Collectors.toList())
                : new ArrayList<>();

        return new HotelResponseDto(
                true,
                hotelDto,
                roomResponseDtos
        );
    }

    // Retrieves all hotels from the repository
    @Override
    @Cacheable(value = "hotels")
    public List<HotelDto> getAllHotels() {
        List<Hotel> hotels = hotelRepository.findAll();
        return hotels.stream()
                .map(hotel -> new HotelDto(
                        hotel.getId(),
                        hotel.getName(),
                        hotel.getLocation()
                ))
                .collect(Collectors.toList());
    }

    // Edits an existing hotel by its ID
    @Transactional
    @Override
    @CacheEvict(value = {"hotel", "hotels"}, allEntries = true)
    public HotelResponseDto editHotelById(Long id, HotelRequestDto hotelRequestDto) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new InvalidUserInputException("Id not found"));

        if (hotelRequestDto.name() != null &&
                !hotel.getName().equals(hotelRequestDto.name()) &&
                hotelRepository.findByName(hotelRequestDto.name()).isPresent()) {
            throw new InvalidUserInputException("Name is already taken");
        }

        if (hotelRequestDto.name() != null) {
            hotel.setName(hotelRequestDto.name());
        }

        if (hotelRequestDto.location() != null) {
            hotel.setLocation(hotelRequestDto.location());
        }

        hotel = hotelRepository.save(hotel);

        HotelDto hotelDto = new HotelDto(hotel.getId(), hotel.getName(), hotel.getLocation());

        return new HotelResponseDto(true, hotelDto, null);
    }

    // Deletes a hotel by its ID
    @Override
    @CacheEvict(value = {"hotel", "hotels"}, allEntries = true)
    public String deleteHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new InvalidUserInputException("Id not found"));

        hotelRepository.delete(hotel);
        return hotel.getName() + " located at " + hotel.getLocation() + " has been deleted";
    }

    // Retrieves a hotel by its ID
    @Override
    @Cacheable(value = "hotel", key = "#id")
    public HotelResponseDto getHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new InvalidUserInputException("Id not found"));

        HotelDto hotelDto = new HotelDto(hotel.getId(), hotel.getName(), hotel.getLocation());

        List<RoomResponseDto> roomDtos = hotel.getRooms().stream()
                .map(room -> new RoomResponseDto(
                        room.getId(),
                        room.getRoomType(),
                        room.getPrice(),
                        room.isAvailable(),
                        hotel.getId()
                ))
                .toList();

        return new HotelResponseDto(true, hotelDto, roomDtos);
    }
}
