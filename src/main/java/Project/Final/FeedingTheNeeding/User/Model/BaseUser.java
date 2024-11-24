package Project.Final.FeedingTheNeeding.User.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data // automatically created getters, setters, toString and etc..
@Builder
@NoArgsConstructor // create constructor without a parameters
@AllArgsConstructor // create constructor with all the parameters
public class BaseUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(unique = true) // have to be unique in the table
    private String email;

    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String city;

    @Enumerated(EnumType.STRING)
    private UserStatus status;
}
