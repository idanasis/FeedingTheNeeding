package Project.Final.FeedingTheNeeding.Authentication.Exception;

public class InvalidJWTException extends RuntimeException {
    public InvalidJWTException(String message) {
        super(message);
    }
}
