package Project.Final.FeedingTheNeeding.User.Mappers;

import Project.Final.FeedingTheNeeding.User.DTO.BaseUserDTO;
import Project.Final.FeedingTheNeeding.User.DTO.DonatorDTO;
import Project.Final.FeedingTheNeeding.User.Model.Donator;

public class DonatorMapperImpl implements DonatorMapper {

    @Override
    public Donator fromDTO(DonatorDTO donatorDTO) {
        Donator donator = new Donator();
        donator.setId(donatorDTO.baseUserDTO().ID());
        donator.setFirstName(donatorDTO.baseUserDTO().firstName());
        donator.setLastName(donatorDTO.baseUserDTO().lastName());
        donator.setPhoneNumber(donatorDTO.baseUserDTO().phoneNumber());
        donator.setAddress(donatorDTO.baseUserDTO().address());
        donator.setCity(donatorDTO.baseUserDTO().city());
        donator.setEmail(donatorDTO.email());
        donator.setPassword(donatorDTO.password());
        donator.setTimeOfDonation(donatorDTO.timeOfDonation());
        donator.setStatus(donatorDTO.registrationStatus());
        return donator;
    }

    @Override
    public DonatorDTO toDTO(Donator donator) {
        BaseUserDTO baseUserDTO = new BaseUserDTO(donator.getId(), donator.getFirstName(), donator.getLastName(), donator.getPhoneNumber(), donator.getAddress(), donator.getCity());
        return new DonatorDTO(baseUserDTO, donator.getEmail(), donator.getPassword(), donator.getTimeOfDonation(), donator.getStatus());
    }
}
