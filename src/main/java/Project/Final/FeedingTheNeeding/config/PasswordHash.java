package Project.Final.FeedingTheNeeding.config;

import Project.Final.FeedingTheNeeding.driving.Fascade.DrivingFascade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class PasswordHash {
    private static final Logger logger = LogManager.getLogger(PasswordHash.class);

    public static String hashPassword(String password) {
        logger.info("start-hashPassword");
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        logger.info("end-hashPassword. hashedPassword: {}", hashedPassword);
        return hashedPassword;
    }

    public static boolean verifyPassword(String password, String hashedPassword) {
        logger.info("start-verifyPassword {}", hashedPassword);
        return BCrypt.checkpw(password, hashedPassword);
    }

}
