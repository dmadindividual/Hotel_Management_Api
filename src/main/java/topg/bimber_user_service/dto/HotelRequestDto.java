package topg.bimber_user_service.dto;

import topg.bimber_user_service.models.Room;

import java.util.List;

public record HotelRequestDto(
        String name,
        String location,
        List<Room> rooms
) {
}
