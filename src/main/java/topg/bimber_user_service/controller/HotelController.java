package topg.bimber_user_service.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import topg.bimber_user_service.dto.HotelDto;
import topg.bimber_user_service.dto.HotelRequestDto;
import topg.bimber_user_service.dto.HotelResponseDto;
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
    public ResponseEntity<HotelResponseDto> createHotel(@RequestBody HotelRequestDto hotelRequestDto ){
        HotelResponseDto message = hotelService.createHotel(hotelRequestDto);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/hotels")
    public ResponseEntity< List<HotelDto>> getAllHotels(){
        List<HotelDto> message = hotelService.getAllHotels();
        return ResponseEntity.ok(message);
    }

    @GetMapping("/hotels/{id}")
    public ResponseEntity<HotelResponseDto> getHotelById(@PathVariable("id")Long id){

        HotelResponseDto message = hotelService.getHotelById(id);
        return ResponseEntity.ok(message);
    }


    @PutMapping("/edit/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<HotelResponseDto > editHotelById(@PathVariable("id")Long id, @RequestBody HotelRequestDto hotelRequestDto){

        HotelResponseDto message = hotelService.editHotelById(id, hotelRequestDto);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String > deleteHotelById(@PathVariable("id")Long id ){
        String message = hotelService.deleteHotelById(id);
        return ResponseEntity.ok(message);
    }


}


