package Project.Final.FeedingTheNeeding.Authentication.Repository;

import Project.Final.FeedingTheNeeding.Authentication.Exception.PasswordIncorrectException;
import Project.Final.FeedingTheNeeding.Authentication.Exception.UserDoesntExistsException;
import Project.Final.FeedingTheNeeding.Authentication.Exception.UserAlreadyExistsException;
import Project.Final.FeedingTheNeeding.config.PasswordHash;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Repository
public class AuthRepositoryImpl implements AuthRepository {

    private static HashMap<String, String> emailAndPassword;
    private static final Logger logger = LogManager.getLogger(AuthRepositoryImpl.class);

    public AuthRepositoryImpl() {
        emailAndPassword = new HashMap<>();
    }

    @Override
    public void login(String email, String password) {
        logger.info("start-login, email: {}", email);
        if(!hasMember(email)){
            logger.info("user does not exist");
            throw new UserDoesntExistsException("user does not exist");
        }
        if(!isPasswordCorrect(email, password)){
            logger.info("password does not match");
            throw new PasswordIncorrectException("password does not match");
        }
        logger.info("end-login");
    }

    private boolean hasMember(String email) {
        return emailAndPassword.containsKey(email);
    }

    private boolean isPasswordCorrect(String email, String password) {
        logger.info("start-isPasswordCorrect, email: {}", email);
        boolean out = PasswordHash.verifyPassword(password, emailAndPassword.get(email));
        logger.info("end-isPasswordCorrect, returnValue:{}", out);
        return out;
    }

    @Override
    public HashMap<String, String> getAll() {
        return emailAndPassword;
    }

    @Override
    public void add(String email, String password) {
        logger.info("start-add, email: {}", email);
        if(hasMember(email))
            throw new UserAlreadyExistsException("user already exists");
        emailAndPassword.put(email, PasswordHash.hashPassword(password));
        logger.info("end-add");
    }

    @Override
    public void delete(String email) {
        logger.info("start-delete, email: {}", email);
        emailAndPassword.remove(email);
        logger.info("end-delete, email: {}", email);
    }

    @Override
    public void clear() {

    }
}
