package topg.bimber_user_service.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import topg.bimber_user_service.dto.HotelDto;
import topg.bimber_user_service.dto.HotelDtoFilter;
import topg.bimber_user_service.dto.HotelRequestDto;
import topg.bimber_user_service.dto.HotelResponseDto;
import topg.bimber_user_service.exceptions.*;
import topg.bimber_user_service.models.State;
import topg.bimber_user_service.models.User;
import topg.bimber_user_service.service.HotelService;
import topg.bimber_user_service.service.UserService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/hotel")
@RequiredArgsConstructor
public class HotelController {
    private final HotelService hotelService;


    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createHotel(
            @RequestParam("name") String name,
            @RequestParam("state") String state,
            @RequestParam("location") String location,
            @RequestParam("description") String description, // Fixed duplicate `state`
            @RequestParam("amenities[]") List<String> amenities,
            @RequestParam("pictures[]") List<MultipartFile> pictures) {

        try {
            HotelResponseDto response = hotelService.createHotel(name, state, location, description, amenities, pictures);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Failed to create hotel", e.getMessage()));
        }
    }

    @GetMapping("/state/{state}")
    public ResponseEntity<?> getHotelsByState(@PathVariable String state) {
        try {
            List<HotelDtoFilter> hotels = hotelService.getHotelsByState(state);

            if (hotels.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("No hotels found", "No hotels available in the state: " + state));
            }

            return ResponseEntity.ok(hotels);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve hotels", e.getMessage()));
        }
    }


    @PutMapping("/edit/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> editHotelById(@PathVariable("id") Long id, @RequestBody HotelRequestDto hotelRequestDto) {
        try {
            String message = hotelService.editHotelById(id, hotelRequestDto);
            return ResponseEntity.ok(new SuccessResponse("Hotel updated successfully", message));
        } catch (HotelNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Hotel not found", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid input", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to update hotel", e.getMessage()));
        }
    }


    @GetMapping("/hotels/{id}")
    public ResponseEntity<?> getHotelById(@PathVariable("id") Long id) {
        try {
            HotelDtoFilter hotel = hotelService.getHotelById(id);
            return ResponseEntity.ok(hotel);
        } catch (HotelNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Hotel not found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve hotel", e.getMessage()));
        }
    }


    @GetMapping("/count")
    public ResponseEntity<?> getTotalHotelsInState(@RequestParam String state) {
        try {
            int count = hotelService.getTotalHotelsInState(state);

            if (count == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new SuccessResponse("No Hotels Found", "There are no hotels available in the state: " + state));
            }

            return ResponseEntity.ok(new SuccessResponse("Total hotels retrieved successfully", String.valueOf(count)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new SuccessResponse("Invalid State", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new SuccessResponse("Failed to retrieve total hotels", e.getMessage()));
        }
    }


    @GetMapping("/most-booked/{state}")
    public ResponseEntity<?> getMostBookedHotelsByState(@PathVariable String state) {
        try {
            List<HotelDtoFilter> hotels = hotelService.getMostBookedHotelsByState(state);

            if (hotels.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new SuccessResponse("No Hotels Found", "No booked hotels found in state: " + state));
            }

            return ResponseEntity.ok(new SuccessListResponse<>("Most booked hotels retrieved successfully", hotels));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new SuccessResponse("Invalid State", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new SuccessResponse("Failed to retrieve most booked hotels", e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteHotel(@PathVariable Long id) {
        try {
            String message = hotelService.deleteHotelById(id);
            return ResponseEntity.ok(message);
        } catch (InvalidUserInputException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}


