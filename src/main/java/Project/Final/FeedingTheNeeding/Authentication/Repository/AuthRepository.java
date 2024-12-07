package Project.Final.FeedingTheNeeding.Authentication.Repository;

import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Repository
public interface AuthRepository {

    void login(String email, String password);
    HashMap<String, String> getAll();
    void add(String email, String password);
    void delete(String email);
    void clear();
}
