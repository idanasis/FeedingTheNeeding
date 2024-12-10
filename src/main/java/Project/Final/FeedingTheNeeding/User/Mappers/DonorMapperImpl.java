package Project.Final.FeedingTheNeeding.User.Mappers;

import Project.Final.FeedingTheNeeding.User.DTO.BaseUserDTO;
import Project.Final.FeedingTheNeeding.User.DTO.DonorDTO;
import Project.Final.FeedingTheNeeding.User.Model.Donor;

public class DonorMapperImpl implements DonorMapper {

    @Override
    public Donor fromDTO(DonorDTO donorDTO) {
        Donor donor = new Donor();
        donor.setId(donorDTO.baseUserDTO().ID());
        donor.setFirstName(donorDTO.baseUserDTO().firstName());
        donor.setLastName(donorDTO.baseUserDTO().lastName());
        donor.setPhoneNumber(donorDTO.baseUserDTO().phoneNumber());
        donor.setAddress(donorDTO.baseUserDTO().address());
        donor.setCity(donorDTO.baseUserDTO().city());
        donor.setEmail(donorDTO.email());
        donor.setTimeOfDonation(donorDTO.timeOfDonation());
        donor.setStatus(donorDTO.registrationStatus());
        return donor;
    }

    @Override
    public DonorDTO toDTO(Donor donor) {
        BaseUserDTO baseUserDTO = new BaseUserDTO(donor.getId(), donor.getFirstName(), donor.getLastName(), donor.getPhoneNumber(), donor.getAddress(), donor.getCity());
        return new DonorDTO(baseUserDTO, donor.getEmail(), donor.getTimeOfDonation(), donor.getStatus());
    }
}