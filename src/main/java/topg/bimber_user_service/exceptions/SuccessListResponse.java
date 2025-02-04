package topg.bimber_user_service.exceptions;

import java.util.List;

public class SuccessListResponse<T> {
    private String status;
    private List<T> data;

    public SuccessListResponse(String status, List<T> data) {
        this.status = status;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public List<T> getData() {
        return data;
    }
}
