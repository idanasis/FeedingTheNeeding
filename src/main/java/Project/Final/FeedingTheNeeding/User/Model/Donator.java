package Project.Final.FeedingTheNeeding.User.Model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Donator extends BaseUser{


    @Column(unique = true) // have to be unique in the table
    private String email;
    private String password;
    private double timeOfDonation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegistrationStatus status;
}