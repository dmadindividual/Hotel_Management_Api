package topg.bimber_user_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import topg.bimber_user_service.dto.RoomRequestDto;
import topg.bimber_user_service.dto.RoomResponseDto;
import topg.bimber_user_service.exceptions.ErrorResponse;
import topg.bimber_user_service.exceptions.SuccessResponse;
import topg.bimber_user_service.exceptions.UserNotFoundInDb;
import topg.bimber_user_service.models.User;
import topg.bimber_user_service.service.RoomService;
import topg.bimber_user_service.service.UserService;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createRoom(
            @RequestPart("room") String roomJson,
            @RequestPart(value = "pictures", required = false) List<MultipartFile> pictures
    ) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            RoomRequestDto roomRequestDto = objectMapper.readValue(roomJson, RoomRequestDto.class);

            // ✅ Validate request data
            if (roomRequestDto.roomType() == null) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Invalid Input", "Room type cannot be null"));
            }
            if (roomRequestDto.price() == null || roomRequestDto.price().compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Invalid Input", "Price must be greater than zero"));
            }
            if (roomRequestDto.available() == null) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Invalid Input", "Availability status must be specified"));
            }
            if (roomRequestDto.hotelId() == null || roomRequestDto.hotelId() <= 0) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Invalid Input", "Hotel ID must be a valid positive number"));
            }

            // ✅ Call service to create the room
            RoomResponseDto response = roomService.createRoom(roomRequestDto, pictures);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Invalid JSON", "Malformed JSON format for room data."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Server Error", e.getMessage()));
        }
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> editRoomById(@PathVariable Long id, @RequestBody RoomRequestDto roomRequestDto) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Invalid Input", "Room ID must be a valid positive number"));
            }
            if (roomRequestDto.roomType() == null) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Invalid Input", "Room type cannot be null"));
            }
            if (roomRequestDto.price() == null || roomRequestDto.price().compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Invalid Input", "Price must be greater than zero"));
            }
            if (roomRequestDto.available() == null) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Invalid Input", "Availability status must be specified"));
            }
            if (roomRequestDto.hotelId() == null || roomRequestDto.hotelId() <= 0) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Invalid Input", "Hotel ID must be a valid positive number"));
            }

            String response = roomService.editRoomById(id, roomRequestDto);
            return ResponseEntity.ok(new SuccessResponse("Success", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Server Error", e.getMessage()));
        }
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteRoomById(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Invalid Input", "Room ID must be a valid positive number"));
            }
            String response = roomService.deleteRoomById(id);
            return ResponseEntity.ok(new SuccessResponse("Success", response));
        } catch (UserNotFoundInDb e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Not Found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Server Error", e.getMessage()));
        }
    }


    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<?> findAllRoomsByHotelId(@PathVariable Long hotelId) {
        try {
            if (hotelId == null || hotelId <= 0) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Invalid Input", "Hotel ID must be a valid positive number"));
            }

            List<RoomResponseDto> rooms = roomService.findAllRoomsByHotelId(hotelId);

            if (rooms.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Not Found", "No rooms found for the given hotel ID"));
            }

            return ResponseEntity.ok(rooms);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Server Error", e.getMessage()));
        }
    }


    @GetMapping("/{id}/availability")
    public ResponseEntity<?> isRoomAvailable(@PathVariable Long id) {
        try {
            // ✅ Validate Room ID
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Invalid Input", "Room ID must be a valid positive number"));
            }

            // ✅ Check if room exists
            Boolean isAvailable = roomService.isRoomAvailable(id);
            if (isAvailable == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Not Found", "Room with ID " + id + " not found"));
            }

            return ResponseEntity.ok(isAvailable);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Server Error", e.getMessage()));
        }
    }



    @GetMapping("/hotel/{hotelId}/available")
    public ResponseEntity<?> findAllAvailableHotelRooms(@PathVariable Long hotelId) {
        try {
            if (hotelId == null || hotelId <= 0) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Invalid Input", "Hotel ID must be a valid positive number"));
            }

            List<RoomResponseDto> availableRooms = roomService.findAllAvailableHotelRooms(hotelId);
            if (availableRooms.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Not Found", "No available rooms found for hotel ID " + hotelId));
            }

            return ResponseEntity.ok(availableRooms);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Server Error", e.getMessage()));
        }
    }


    @PutMapping("/hotel/{hotelId}/deactivate/{roomId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deactivateRoomByHotelId(@PathVariable Long hotelId, @PathVariable Long roomId) {
        try {
            if (hotelId == null || hotelId <= 0 || roomId == null || roomId <= 0) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Invalid Input", "Hotel ID and Room ID must be valid positive numbers"));
            }

            RoomResponseDto response = roomService.deactivateRoomByHotelId(hotelId, roomId);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Not Found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Server Error", e.getMessage()));
        }
    }



    @PutMapping("/hotel/{hotelId}/activate/{roomId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> activateRoomByHotelId(@PathVariable Long hotelId, @PathVariable Long roomId) {
        try {
            if (hotelId == null || hotelId <= 0 || roomId == null || roomId <= 0) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Invalid Input", "Hotel ID and Room ID must be valid positive numbers"));
            }

            RoomResponseDto response = roomService.activateRoomByHotelId(hotelId, roomId);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Not Found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Server Error", e.getMessage()));
        }
    }


    @GetMapping("/hotel/{hotelId}/filter")
    public ResponseEntity<?> filterHotelRoomByType(@PathVariable Long hotelId, @RequestParam String type) {
        try {
            if (hotelId == null || hotelId <= 0 || type == null || type.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Invalid Input", "Hotel ID must be a positive number and type cannot be empty"));
            }

            List<RoomResponseDto> filteredRooms = roomService.filterHotelRoomByType(hotelId, type);
            if (filteredRooms.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Not Found", "No rooms found for type " + type + " in hotel ID " + hotelId));
            }

            return ResponseEntity.ok(filteredRooms);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Server Error", e.getMessage()));
        }
    }



    @GetMapping("/filter/price")
    public ResponseEntity<?> filterByPriceAndState(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            @RequestParam String state) {
        try {
            if (minPrice == null || maxPrice == null || minPrice.compareTo(BigDecimal.ZERO) < 0 || maxPrice.compareTo(minPrice) < 0) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Invalid Input", "Price range must be valid and minPrice should be less than maxPrice"));
            }

            if (state == null || state.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Invalid Input", "State cannot be empty"));
            }

            List<RoomResponseDto> filteredRooms = roomService.filterByPriceAndState(minPrice, maxPrice, state);
            if (filteredRooms.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Not Found", "No rooms found in " + state + " within the given price range"));
            }

            return ResponseEntity.ok(filteredRooms);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Server Error", e.getMessage()));
        }
    }

}

