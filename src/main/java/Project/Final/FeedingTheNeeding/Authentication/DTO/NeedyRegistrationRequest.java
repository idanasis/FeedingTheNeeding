package Project.Final.FeedingTheNeeding.Authentication.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NeedyRegistrationRequest {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String city;
    private int familySize;
    private String dietaryPreferences; // e.g., "Vegetable, No Sugar"
    private String additionalNotes;
}
