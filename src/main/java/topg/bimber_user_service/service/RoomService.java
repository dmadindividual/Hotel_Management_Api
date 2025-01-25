package topg.bimber_user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import topg.bimber_user_service.dto.*;
import topg.bimber_user_service.exceptions.UserNotFoundInDb;
import topg.bimber_user_service.models.Hotel;
import topg.bimber_user_service.models.Room;
import topg.bimber_user_service.models.RoomType;
import topg.bimber_user_service.repository.HotelRepository;
import topg.bimber_user_service.repository.RoomRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService implements IRoomService {
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;

    // Creates a new room and associates it with a hotel
    @Transactional
    @Override
    @CacheEvict(value = "availableRooms", allEntries = true)
    public RoomResponseDto createRoom(RoomRequestDto roomRequestDto) {
        Hotel hotel = hotelRepository.findById(roomRequestDto.hotelId())
                .orElseThrow(() -> new UserNotFoundInDb("Hotel not found"));

        Room room = Room.builder()
                .roomType(roomRequestDto.roomType())
                .price(roomRequestDto.price())
                .hotel(hotel)
                .isAvailable(roomRequestDto.isAvailable())
                .build();
        room = roomRepository.save(room);

        return new RoomResponseDto(
                room.getId(),
                room.getRoomType(),
                room.getPrice(),
                room.isAvailable(),
                room.getHotel().getId()
        );
    }

    // Updates room details by ID
    @Override
    @Transactional
    public RoomResponseDto editRoomById(Long id, RoomRequestDto roomRequestDto) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundInDb("Room not found"));

        if (roomRequestDto.roomType() != null) {
            room.setRoomType(roomRequestDto.roomType());
        }
        if (roomRequestDto.price() != null) {
            room.setPrice(roomRequestDto.price());
        }
        if (roomRequestDto.isAvailable() != null) {
            room.setAvailable(roomRequestDto.isAvailable());
        }

        room = roomRepository.save(room);

        return new RoomResponseDto(
                room.getId(),
                room.getRoomType(),
                room.getPrice(),
                room.isAvailable(),
                room.getHotel().getId()
        );
    }

    // Deletes a room by ID
    @Override
    @Transactional
    public String deleteRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundInDb("Room not found"));
        roomRepository.delete(room);
        return "You have successfully deleted room with id:  " + room.getId();
    }

    // Finds all rooms associated with a specific hotel
    @Override
    public List<RoomResponseDto> findAllRoomsByHotelId(Long hotelId) {
        List<Room> rooms = roomRepository.findByHotelId(hotelId);
        return rooms.stream()
                .map(room -> new RoomResponseDto(
                        room.getId(),
                        room.getRoomType(),
                        room.getPrice(),
                        room.isAvailable(),
                        room.getHotel().getId()
                ))
                .toList();
    }

    // Checks if a room is available by ID
    @Override
    public boolean isRoomAvailable(Long id) {
        return roomRepository.findById(id)
                .map(Room::isAvailable)
                .orElseThrow(() -> new UserNotFoundInDb("Room with ID " + id + " not found"));
    }

    // Retrieves all available rooms
    @Override
    @Cacheable(value = "availableRooms", unless = "#result == null || #result.isEmpty()")
    public List<RoomResponseDto> findAllAvailableRooms() {
        List<Room> availableRooms = roomRepository.findByIsAvailable(true);
        return availableRooms.stream()
                .map(room -> new RoomResponseDto(
                        room.getId(),
                        room.getRoomType(),
                        room.getPrice(),
                        room.isAvailable(),
                        room.getHotel().getId()
                ))
                .toList();
    }

    // Deactivates a room by ID
    @Override
    public RoomResponseDto deactivateRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundInDb("Room with ID " + id + " not found"));
        room.setAvailable(false);
        room = roomRepository.save(room);

        return new RoomResponseDto(
                room.getId(),
                room.getRoomType(),
                room.getPrice(),
                room.isAvailable(),
                room.getHotel().getId()
        );
    }

    // Reactivates a room by ID
    @Override
    public RoomResponseDto reactivateRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundInDb("Room with ID " + id + " not found"));
        room.setAvailable(true);
        room = roomRepository.save(room);

        return new RoomResponseDto(
                room.getId(),
                room.getRoomType(),
                room.getPrice(),
                room.isAvailable(),
                room.getHotel().getId()
        );
    }

    // Filters rooms by type
    @Override
    public List<RoomResponseDto> filterRooms(String type) {
        List<Room> rooms = roomRepository.findByRoomType(RoomType.valueOf(type));
        return rooms.stream()
                .map(room -> new RoomResponseDto(
                        room.getId(),
                        room.getRoomType(),
                        room.getPrice(),
                        room.isAvailable(),
                        room.getHotel().getId()
                ))
                .toList();
    }

    // Filters rooms by price range
    @Override
    public List<RoomResponseDto> filterByPrice(BigDecimal minPrice, BigDecimal maxPrice) {
        List<Room> rooms = roomRepository.findByPriceBetween(minPrice, maxPrice);
        return rooms.stream()
                .map(room -> new RoomResponseDto(
                        room.getId(),
                        room.getRoomType(),
                        room.getPrice(),
                        room.isAvailable(),
                        room.getHotel().getId()
                ))
                .toList();
    }
}
