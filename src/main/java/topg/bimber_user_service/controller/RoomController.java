package topg.bimber_user_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import topg.bimber_user_service.dto.RoomRequestDto;
import topg.bimber_user_service.dto.RoomResponseDto;
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
    public ResponseEntity<RoomResponseDto> createRoom(
            @RequestPart("room") String roomJson,
            @RequestPart(value = "pictures", required = false) List<MultipartFile> pictures
    ) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        RoomRequestDto roomRequestDto = objectMapper.readValue(roomJson, RoomRequestDto.class);

        // Call service to create the room
        RoomResponseDto response = roomService.createRoom(roomRequestDto, pictures);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }



        // ✅ Edit a Room
        @PutMapping("/{id}")
        public ResponseEntity<String> editRoomById(@PathVariable Long id, @RequestBody RoomRequestDto roomRequestDto) {
            return ResponseEntity.ok(roomService.editRoomById(id, roomRequestDto));
        }

        // ✅ Delete a Room
        @DeleteMapping("/{id}")
        public ResponseEntity<String> deleteRoomById(@PathVariable Long id) {
            return ResponseEntity.ok(roomService.deleteRoomById(id));
        }

        // ✅ Get all Rooms by Hotel ID
        @GetMapping("/hotel/{hotelId}")
        public ResponseEntity<List<RoomResponseDto>> findAllRoomsByHotelId(@PathVariable Long hotelId) {
            return ResponseEntity.ok(roomService.findAllRoomsByHotelId(hotelId));
        }

        // ✅ Check if Room is Available
        @GetMapping("/{id}/availability")
        public ResponseEntity<Boolean> isRoomAvailable(@PathVariable Long id) {
            return ResponseEntity.ok(roomService.isRoomAvailable(id));
        }

        // ✅ Get all Available Rooms in a Hotel
        @GetMapping("/hotel/{hotelId}/available")
        public ResponseEntity<List<RoomResponseDto>> findAllAvailableHotelRooms(@PathVariable Long hotelId) {
            return ResponseEntity.ok(roomService.findAllAvailableHotelRooms(hotelId));
        }

        // ✅ Deactivate a Room by Hotel ID
        @PutMapping("/hotel/{hotelId}/deactivate/{roomId}")
        public ResponseEntity<RoomResponseDto> deactivateRoomByHotelId(
                @PathVariable Long hotelId, @PathVariable Long roomId) {
            return ResponseEntity.ok(roomService.deactivateRoomByHotelId(hotelId, roomId));
        }

        // ✅ Activate a Room by Hotel ID
        @PutMapping("/hotel/{hotelId}/activate/{roomId}")
        public ResponseEntity<RoomResponseDto> activateRoomByHotelId(
                @PathVariable Long hotelId, @PathVariable Long roomId) {
            return ResponseEntity.ok(roomService.activateRoomByHotelId(hotelId, roomId));
        }

        // ✅ Filter Rooms by Type in a Hotel
        @GetMapping("/hotel/{hotelId}/filter")
        public ResponseEntity<List<RoomResponseDto>> filterHotelRoomByType(
                @PathVariable Long hotelId, @RequestParam String type) {
            return ResponseEntity.ok(roomService.filterHotelRoomByType(hotelId, type));
        }

        // ✅ Filter Rooms by Price Range and State
        @GetMapping("/filter/price")
        public ResponseEntity<List<RoomResponseDto>> filterByPriceAndState(
                @RequestParam BigDecimal minPrice,
                @RequestParam BigDecimal maxPrice,
                @RequestParam String state) {
            return ResponseEntity.ok(roomService.filterByPriceAndState(minPrice, maxPrice, state));
        }
    }

