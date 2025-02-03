package topg.bimber_user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import topg.bimber_user_service.models.RoomPicture;

@Repository
public interface RoomPictureRepository extends JpaRepository<RoomPicture, Long> {
}
