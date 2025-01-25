package topg.bimber_user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import topg.bimber_user_service.models.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
