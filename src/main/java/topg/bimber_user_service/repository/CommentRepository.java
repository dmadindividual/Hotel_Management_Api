package topg.bimber_user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import topg.bimber_user_service.dto.CommentResponseDto;
import topg.bimber_user_service.models.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    boolean existsByUserIdAndHotelId(String userId, Long hotelId);

    List<Comment> findByHotelId(Long hotelId);

    Comment findByUserId(String userId);
}
