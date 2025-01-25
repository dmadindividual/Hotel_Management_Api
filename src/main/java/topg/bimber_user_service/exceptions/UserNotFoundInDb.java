package topg.bimber_user_service.exceptions;

public class UserNotFoundInDb extends RuntimeException {
    public UserNotFoundInDb(String message) {
        super(message);
    }
}
