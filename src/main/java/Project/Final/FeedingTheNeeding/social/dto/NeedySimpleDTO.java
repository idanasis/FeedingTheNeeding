package Project.Final.FeedingTheNeeding.social.dto;

import Project.Final.FeedingTheNeeding.User.Model.Needy;
import lombok.Data;

// DTO for Needy fields
@Data
public class NeedySimpleDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String city;
    private String additionalNotes;

    // Add other specific fields you want to expose

    // Constructor
    public NeedySimpleDTO(Needy needy,String additionalNotes) {
        this.id = needy.getId();
        this.firstName = needy.getFirstName();
        this.lastName = needy.getLastName();
        this.phoneNumber = needy.getPhoneNumber();
        this.address = needy.getAddress();
        this.city = needy.getCity();
        this.additionalNotes=additionalNotes;
        // Map other fields as needed
    }

    // Getters and setters
}
