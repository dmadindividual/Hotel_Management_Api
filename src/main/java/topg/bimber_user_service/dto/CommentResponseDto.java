package topg.bimber_user_service.dto;

import java.time.LocalDateTime;

public record CommentResponseDto(
        Long id,
        String content,
        LocalDateTime createdAt,
        String username
) {
}
