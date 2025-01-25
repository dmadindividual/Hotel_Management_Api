package topg.bimber_user_service.dto;

public record UserCreatedDto(
        boolean success,
        String message,
        Object data
) {

}
