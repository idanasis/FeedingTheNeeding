package Project.Final.FeedingTheNeeding.User.Model;


import Project.Final.FeedingTheNeeding.Authentication.DTO.RegistrationStatus;
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
    private double timeOfDonation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegistrationStatus status;


    @Override
    public String getUserTypeDescription() {
        return "Donator";
    }
}