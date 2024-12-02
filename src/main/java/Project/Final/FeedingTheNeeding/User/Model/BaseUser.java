package Project.Final.FeedingTheNeeding.User.Model;

import jakarta.persistence.*;
import lombok.*;

@MappedSuperclass
@Data // automatically created getters, setters, toString and etc..
@Builder
@NoArgsConstructor // create constructor without a parameters
@AllArgsConstructor // create constructor with all the parameters
public class BaseUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;


    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;


}