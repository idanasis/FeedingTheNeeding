package Project.Final.FeedingTheNeeding.User.Repository;

import Project.Final.FeedingTheNeeding.Authentication.DTO.RegistrationStatus;
import Project.Final.FeedingTheNeeding.User.Model.Donor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface DonorRepository extends UserRepository<Donor> {
    Optional<Donor> findByEmail(String email);
    Optional<Donor> findByPhoneNumber(String phoneNumber);
    Optional<Donor> findByVerificationCode(String verificationCode);
    List<Donor> findByStatus(RegistrationStatus string);
}