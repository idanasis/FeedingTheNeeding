package Project.Final.FeedingTheNeeding.Authentication.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NeedyRegistrationRequest {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private int familySize;
    private String street;
}

