package Project.Final.FeedingTheNeeding.Authentication.Exception;

public class UserDoesntExistsException extends RuntimeException {
    public UserDoesntExistsException(String message) {
        super(message);
    }
}
