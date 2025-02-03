package topg.bimber_user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import topg.bimber_user_service.models.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
