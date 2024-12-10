package Project.Final.FeedingTheNeeding.Authentication.Repository;

import Project.Final.FeedingTheNeeding.Authentication.Model.UserCredentials;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;

public interface UserCredentialsRepository extends JpaRepository<UserCredentials, Long> {
    Optional<UserCredentials> findByEmail(String email);
    UserCredentials findCredentialsByEmail(String email);
    boolean existsByEmail(String email);
}