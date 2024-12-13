package Project.Final.FeedingTheNeeding.Authentication.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {
    //private String ID;
    private String email;
    private String password;
    private String confirmPassword;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String city;
}