package Project.Final.FeedingTheNeeding.User.Repository;

import Project.Final.FeedingTheNeeding.User.DTO.BaseUserDTO;
import Project.Final.FeedingTheNeeding.User.Model.BaseUser;
import Project.Final.FeedingTheNeeding.User.Model.UserStatus;

import java.util.List;
import java.util.Optional;

public interface IUserRepository {

    BaseUser save(BaseUser user);
    Optional<BaseUser> findByEmail(String email);
    Optional<BaseUser> findById(String id);
    boolean existsByEmail(String email);
    boolean existsById(String id);
    List<BaseUser> findAll();
    Void delete(BaseUser user);
    void deleteByEmail(String email);
    List<BaseUser> findByStatus(UserStatus status);

    boolean isLoggedIn(String email);
    void setLoggIn(String email, boolean loggedIn);
    void logout(String email);
    void setFirstName(String email, String firstName);
    void setLastName(String email, String lastName);
    void setPhoneNumber(String email, String phoneNumber);
    void setAddress(String email, String address);
    void setEmail(String id, String email); // maybe

}
