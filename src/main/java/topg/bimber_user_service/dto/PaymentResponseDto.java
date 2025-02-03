package topg.bimber_user_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentResponseDto {
    private String status;
    private String message;
    private Data data;

    @Getter
    @Setter
    public static class Data {
        private String authorization_url;
    }
}
