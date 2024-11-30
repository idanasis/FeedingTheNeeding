package Project.Final.FeedingTheNeeding.User.DTO;

import Project.Final.FeedingTheNeeding.User.Model.RegistrationStatus;

public record BaseUserDTO(Long ID, String firstName, String lastName, String phoneNumber, String address, String city) {

}