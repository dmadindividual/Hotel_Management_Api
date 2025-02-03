package topg.bimber_user_service.dto;

import java.io.Serializable;
import java.util.List;

public record HotelResponseDto(
        boolean success,
        HotelDto hotel,
        List<PictureResponseDto> pictures
) implements Serializable {
    private static final long serialVersionUID = 1L;
}
