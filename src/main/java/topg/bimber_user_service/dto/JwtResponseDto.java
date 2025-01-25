package topg.bimber_user_service.dto;

public record JwtResponseDto(

        boolean success,
        String token
) {
}