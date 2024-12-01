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


    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String city;


}