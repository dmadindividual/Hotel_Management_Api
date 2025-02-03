package topg.bimber_user_service.dto;

import topg.bimber_user_service.models.Room;
import topg.bimber_user_service.models.Comment;
import topg.bimber_user_service.models.State;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record HotelRequestDto(
        String name,
        State state,
        String location,
        String description,
        List<String> amenities,
        List<Room> rooms,
        List<Comment> comments,
        List<MultipartFile> pictures
) {
}
