package topg.bimber_user_service.exceptions;

public class SuccessResponse {
    private String status;
    private String message;

    public SuccessResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}

