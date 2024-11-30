package Project.Final.FeedingTheNeeding.User.DTO;

import Project.Final.FeedingTheNeeding.User.Model.RegistrationStatus;

public record DonatorDTO(BaseUserDTO baseUserDTO, String email, String password, RegistrationStatus registrationStatus) {
}
