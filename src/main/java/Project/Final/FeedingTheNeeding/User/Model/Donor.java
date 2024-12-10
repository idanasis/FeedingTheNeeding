package Project.Final.FeedingTheNeeding.User.Model;


import Project.Final.FeedingTheNeeding.Authentication.DTO.RegistrationStatus;
import Project.Final.FeedingTheNeeding.Authentication.Model.UserCredentials;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Donor extends BaseUser{
    @Column(nullable = false, unique = true)
    private String email;
    private double timeOfDonation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegistrationStatus status;
    @OneToOne
    private UserCredentials userCredentials;

    @Override
    public String getUserTypeDescription() {
        return "Donator";
    }
}