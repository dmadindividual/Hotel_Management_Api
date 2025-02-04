package topg.bimber_user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import topg.bimber_user_service.models.Picture;

import java.util.List;

@Repository
public interface PictureRepository extends JpaRepository<Picture, Long> {
    List<Picture> findByHotelId(Long id);
}
