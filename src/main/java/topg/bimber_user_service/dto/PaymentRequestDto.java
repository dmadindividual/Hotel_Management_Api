package topg.bimber_user_service.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public record PaymentRequestDto(String userId, BigDecimal amount, String email) {
    public Map<String, Object> toMap(String reference) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("email", email);
        payload.put("amount", amount.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP));  // Fix applied
        payload.put("reference", reference);
        return payload;
    }
}
