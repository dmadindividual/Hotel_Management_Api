package topg.bimber_user_service.dto;

import topg.bimber_user_service.models.State;

import java.util.List;

public record HotelDtoFilter(
        Long id,
        String name,
        State state,
        String location,// Adding state to match the updated hotel model
        List<String> amenities,
        String description,
        List<String> pictureUrls
) {
}
