package topg.bimber_user_service.dto;

import java.io.Serializable;

public record HotelDto(Long id, String name, String location) implements Serializable {
    private static final long serialVersionUID = 1L;
}
