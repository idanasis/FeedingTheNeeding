package Project.Final.FeedingTheNeeding.User.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "users")
@Data // automatically created getters, setters, toString and etc..
@Builder
@NoArgsConstructor // create constructor without a parameters
@AllArgsConstructor // create constructor with all the parameters
public class BaseUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "firstName", nullable = false)
    private String firstName;
    @Column(name = "lastName", nullable = false)
    private String lastName;
    @Column(name = "phoneNumber", nullable = false)
    private String phoneNumber;
    @Column(name = "address", nullable = false)
    private String address;
    @Column(name = "city", nullable = false)
    private String city;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BaseUser baseUser = (BaseUser) o;
        return Objects.equals(id, baseUser.id) && Objects.equals(firstName, baseUser.firstName) && Objects.equals(lastName, baseUser.lastName) && Objects.equals(phoneNumber, baseUser.phoneNumber) && Objects.equals(address, baseUser.address) && Objects.equals(city, baseUser.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, phoneNumber, address, city);
    }
}