package topg.bimber_user_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid User Input")
public class InvalidUserInputException extends RuntimeException {
    public InvalidUserInputException(String message) {
        super(message);
    }
}
