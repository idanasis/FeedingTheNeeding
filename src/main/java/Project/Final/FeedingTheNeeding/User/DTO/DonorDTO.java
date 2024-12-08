package Project.Final.FeedingTheNeeding.User.DTO;

import Project.Final.FeedingTheNeeding.Authentication.DTO.RegistrationStatus;

public record DonorDTO(BaseUserDTO baseUserDTO, String email, double timeOfDonation, RegistrationStatus registrationStatus) {
}