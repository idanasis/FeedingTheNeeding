package Project.Final.FeedingTheNeeding.User.Repository;

public interface IUserRepository {

    boolean isLoggedIn(String email);
    void setLoggIn(String email, boolean loggedIn);
    void logout(String email);
    void setFirstName(String email, String firstName);
    void setLastName(String email, String lastName);
    void setPhoneNumber(String email, String phoneNumber);
    void setAddress(String email, String address);
    void setEmail(String id, String email); // maybe

}
