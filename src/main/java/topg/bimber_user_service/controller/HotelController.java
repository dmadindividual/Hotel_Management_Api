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
    private final UserService userService;


    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<HotelResponseDto> createHotel(
            @RequestParam("name") String name,
            @RequestParam("state") String state,
            @RequestParam("state") String location,
            @RequestParam("state") String description,
            @RequestParam("amenities[]") List<String> amenities,
            @RequestParam("pictures[]") List<MultipartFile> pictures) {

        // Call the service to create the hotel with the provided data
        HotelResponseDto message = hotelService.createHotel(name, state,location, description, amenities, pictures);

        // Return the response
        return ResponseEntity.ok(message);
    }

    @GetMapping("/state/{state}")
    public ResponseEntity<List<HotelDtoFilter>> getHotelsByState(@PathVariable String state) {
        List<HotelDtoFilter> hotels = hotelService.getHotelsByState(state);
        return ResponseEntity.ok(hotels);
    }

    @PutMapping("/edit/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String > editHotelById(@PathVariable("id")Long id, @RequestBody HotelRequestDto hotelRequestDto){

        String message = hotelService.editHotelById(id, hotelRequestDto);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/hotels/{id}")
    public ResponseEntity<HotelDtoFilter> getHotelById(@PathVariable("id")Long id){
        HotelDtoFilter message = hotelService.getHotelById(id);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getTotalHotelsInState(@RequestParam String state) {
        try {
            return ResponseEntity.ok(hotelService.getTotalHotelsInState(state));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(0); // Return 0 if invalid state
        }
    }


}


