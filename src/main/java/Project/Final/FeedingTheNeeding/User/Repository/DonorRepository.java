package Project.Final.FeedingTheNeeding.User.Repository;

import Project.Final.FeedingTheNeeding.User.Model.Donor;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface DonorRepository extends UserRepository<Donor> {
    Optional<Donor> findByEmail(String email);
    Optional<Donor> findByVerificationCode(String verificationCode);
}