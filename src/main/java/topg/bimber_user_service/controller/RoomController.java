package topg.bimber_user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RoomResponseDto> createRoom(@RequestBody RoomRequestDto roomRequestDto) {

        RoomResponseDto createdRoom = roomService.createRoom(roomRequestDto);
        return ResponseEntity.status(201).body(createdRoom); // Return created room with HTTP 201 status
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RoomResponseDto> editRoom(@PathVariable Long id, @RequestBody RoomRequestDto roomRequestDto) {

        RoomResponseDto updatedRoom = roomService.editRoomById(id, roomRequestDto);
        return ResponseEntity.ok(updatedRoom); // Return updated room with HTTP 200 status
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> deleteRoom(@PathVariable Long id) {

        String responseMessage = roomService.deleteRoomById(id);
        return ResponseEntity.ok(responseMessage); // Return success message with HTTP 200 status
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<RoomResponseDto>> findAllRoomsByHotel(@PathVariable Long hotelId) {

        List<RoomResponseDto> rooms = roomService.findAllRoomsByHotelId(hotelId);
        return ResponseEntity.ok(rooms); // Return list of rooms with HTTP 200 status
    }

    @GetMapping("/availability/{id}")
    public ResponseEntity<Boolean> isRoomAvailable(@PathVariable Long id) {
        boolean isAvailable = roomService.isRoomAvailable(id);
        return ResponseEntity.ok(isAvailable); // Return boolean value indicating availability with HTTP 200 status
    }

    @GetMapping("/available")
    public ResponseEntity<List<RoomResponseDto>> findAllAvailableRooms() {
        List<RoomResponseDto> availableRooms = roomService.findAllAvailableRooms();
        return ResponseEntity.ok(availableRooms); // Return list of available rooms with HTTP 200 status
    }

    @PatchMapping("/deactivate/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RoomResponseDto> deactivateRoom(@PathVariable Long id) {
        RoomResponseDto deactivatedRoom = roomService.deactivateRoom(id);
        return ResponseEntity.ok(deactivatedRoom); // Return deactivated room with HTTP 200 status
    }

    @PatchMapping("/reactivate/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RoomResponseDto> reactivateRoom(@PathVariable Long id) {
        RoomResponseDto reactivatedRoom = roomService.reactivateRoom(id);
        return ResponseEntity.ok(reactivatedRoom); // Return reactivated room with HTTP 200 status
    }

    @GetMapping("/filter/type")
    public ResponseEntity<List<RoomResponseDto>> filterRoomsByType(@RequestParam String type) {
        List<RoomResponseDto> filteredRooms = roomService.filterRooms(type);
        return ResponseEntity.ok(filteredRooms); // Return filtered rooms by type with HTTP 200 status
    }


    @GetMapping("/filter/price")
    public ResponseEntity<List<RoomResponseDto>> filterRoomsByPrice(
            @RequestParam BigDecimal minPrice, @RequestParam BigDecimal maxPrice) {
        List<RoomResponseDto> filteredRooms = roomService.filterByPrice(minPrice, maxPrice);
        return ResponseEntity.ok(filteredRooms); // Return filtered rooms by price with HTTP 200 status
    }
}