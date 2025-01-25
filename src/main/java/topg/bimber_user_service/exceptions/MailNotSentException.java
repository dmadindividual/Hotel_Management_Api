package topg.bimber_user_service.exceptions;

public class MailNotSentException extends RuntimeException {
    public MailNotSentException(String message) {
        super(message);
    }
}
