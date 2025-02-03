package topg.bimber_user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import topg.bimber_user_service.dto.*;
import topg.bimber_user_service.exceptions.UserNotFoundInDb;
import topg.bimber_user_service.models.*;
import topg.bimber_user_service.repository.HotelRepository;
import topg.bimber_user_service.repository.RoomPictureRepository;
import topg.bimber_user_service.repository.RoomRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService implements IRoomService {
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final RoomPictureRepository roomPictureRepository;


    // Creates a new room and associates it with a hotel
    @Transactional
    @Override
    @CacheEvict(value = "availableRooms", allEntries = true)
    public RoomResponseDto createRoom(RoomRequestDto roomRequestDto, List<MultipartFile> pictures) {
        Hotel hotel = hotelRepository.findById(roomRequestDto.hotelId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found"));

        Room room = Room.builder()
                .hotel(hotel)
                .roomType(roomRequestDto.roomType())
                .price(roomRequestDto.price())
                .available(true)
                .build();

        Room savedRoom = roomRepository.save(room);

        List<RoomPicture> savedPictures = new ArrayList<>();
        if (pictures != null) {
            for (MultipartFile file : pictures) {
                RoomPicture picture = new RoomPicture();
                picture.setRoom(room);
                picture.setFileName(file.getOriginalFilename());
                picture.setFileType(file.getContentType());
                try {
                    picture.setData(file.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                savedPictures.add(roomPictureRepository.save(picture));
            }
        }

        return new RoomResponseDto(
                savedRoom.getId(),
                savedRoom.getRoomType(),
                savedRoom.getPrice(),
                savedRoom.isAvailable(),
                savedPictures.stream().map(RoomPicture::getFileName).toList()
        );
    }

    @Override
    @Transactional
    public String editRoomById(Long id, RoomRequestDto roomRequestDto) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundInDb("Room not found"));

        if (roomRequestDto.roomType() != null) {
            room.setRoomType(roomRequestDto.roomType());
        }
        if (roomRequestDto.price() != null) {
            room.setPrice(roomRequestDto.price());
        }
        if (roomRequestDto.available() != null) {
            room.setAvailable(roomRequestDto.available());
        }

        room = roomRepository.save(room);

        return "You have successfully updated room: " + room.getId();
    }


    @Override
    @Transactional
    public String deleteRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundInDb("Room not found"));
        roomRepository.delete(room);
        return "You have successfully deleted room with id:  " + room.getId();
    }


    @Override
    public List<RoomResponseDto> findAllRoomsByHotelId(Long hotelId) {
        List<Room> rooms = roomRepository.findByHotelId(hotelId);
        return rooms.stream()
                .map(room-> {
                    List<String> pictureUrls = room.getPictures().stream()
                            .map(RoomPicture::getFileName)
                            .toList();

                    return new RoomResponseDto(
                            room.getId(),
                            room.getRoomType(),
                            room.getPrice(),
                            room.isAvailable(),
                            pictureUrls
                    );


                }).collect(Collectors.toList());
    }


    @Override
    public boolean isRoomAvailable(Long id) {
        return roomRepository.findById(id)
                .map(Room::isAvailable)
                .orElseThrow(() -> new UserNotFoundInDb("Room with ID " + id + " not found"));
    }



    @Override
    public List<RoomResponseDto> findAllAvailableHotelRooms(Long hotelId) {
        List<Room> availableRooms = roomRepository.findByHotelIdAndAvailable(hotelId, true);
        return availableRooms.stream()
                .map(room-> {
                    List<String> pictureUrls = room.getPictures().stream()
                            .map(RoomPicture::getFileName)
                            .toList();
                    return new RoomResponseDto(
                            room.getId(),
                            room.getRoomType(),
                            room.getPrice(),
                            room.isAvailable(),
                            pictureUrls
                    );
                }).collect(Collectors.toList());
    }


    @Override
    public RoomResponseDto deactivateRoomByHotelId(Long hotelId, Long roomId) {
        Room room = roomRepository.findByHotelIdAndId(hotelId, roomId)
                .orElseThrow(() -> new UserNotFoundInDb("Room with ID " + roomId + " not found in hotel with ID " + hotelId));

        room.setAvailable(false);
        room = roomRepository.save(room);
        List<String> pictureUrls = room.getPictures().stream()
                .map(RoomPicture::getFileName)
                .toList();

        return new RoomResponseDto(
                room.getId(),
                room.getRoomType(),
                room.getPrice(),
                room.isAvailable(),
                pictureUrls
        );
    }




    @Override
    public RoomResponseDto activateRoomByHotelId(Long hotelId, Long roomId) {
        Room room = roomRepository.findByHotelIdAndId(hotelId, roomId)
                .orElseThrow(() -> new UserNotFoundInDb("Room with ID " + roomId + " not found in hotel with ID " + hotelId));

        room.setAvailable(true);
        room = roomRepository.save(room);
        List<String> pictureUrls = room.getPictures().stream()
                .map(RoomPicture::getFileName)
                .toList();

        return new RoomResponseDto(
                room.getId(),
                room.getRoomType(),
                room.getPrice(),
                room.isAvailable(),
                pictureUrls
        );
    }


    @Override
    public List<RoomResponseDto> filterHotelRoomByType(Long hotelId, String type) {
        List<Room> rooms = roomRepository.findByHotelIdAndRoomType(hotelId, RoomType.valueOf(type.toUpperCase()));

        return rooms.stream()
                .map(room-> {
                    List<String> pictureUrls = room.getPictures().stream()
                            .map(RoomPicture::getFileName)
                            .toList();
                    return new RoomResponseDto(
                            room.getId(),
                            room.getRoomType(),
                            room.getPrice(),
                            room.isAvailable(),
                            pictureUrls
                    );
                }).collect(Collectors.toList());
    }


    @Override
    public List<RoomResponseDto> filterByPriceAndState(BigDecimal minPrice, BigDecimal maxPrice, String state) {
        List<Room> rooms = roomRepository.findByPriceBetweenAndHotel_State(minPrice, maxPrice, State.valueOf(state.toUpperCase()));


        return rooms.stream()
                .map(room-> {
                    List<String> pictureUrls = room.getPictures().stream()
                            .map(RoomPicture::getFileName)
                            .toList();
                    return new RoomResponseDto(
                            room.getId(),
                            room.getRoomType(),
                            room.getPrice(),
                            room.isAvailable(),
                            pictureUrls
                    );
                }).collect(Collectors.toList());
    }


}
