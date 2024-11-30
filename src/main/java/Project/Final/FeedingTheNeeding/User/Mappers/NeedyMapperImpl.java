package Project.Final.FeedingTheNeeding.User.Mappers;

import Project.Final.FeedingTheNeeding.User.DTO.BaseUserDTO;
import Project.Final.FeedingTheNeeding.User.DTO.NeedyDTO;
import Project.Final.FeedingTheNeeding.User.Model.Needy;

public class NeedyMapperImpl implements NeedyMapper {
    @Override
    public Needy fromDTO(NeedyDTO needyDTO) {
        Needy needy = new Needy();
        needy.setId(needyDTO.baseUserDTO().ID());
        needy.setFirstName(needyDTO.baseUserDTO().firstName());
        needy.setLastName(needyDTO.baseUserDTO().lastName());
        needy.setPhoneNumber(needyDTO.baseUserDTO().phoneNumber());
        needy.setAddress(needyDTO.baseUserDTO().address());
        needy.setCity(needyDTO.baseUserDTO().city());
        needy.setStatusForWeek(needyDTO.statusForWeek());
        needy.setFamilySize(needyDTO.familySize());
        needy.setDietaryPreferences(needyDTO.dietaryPreferences());
        needy.setAdditionalNotes(needyDTO.additionalNotes());
        return needy;
    }

    @Override
    public NeedyDTO toDTO(Needy needy) {
        BaseUserDTO baseUserDTO = new BaseUserDTO(needy.getId(), needy.getFirstName(), needy.getLastName(), needy.getPhoneNumber(), needy.getAddress(), needy.getCity());
        return new NeedyDTO(baseUserDTO, needy.getStatusForWeek(), needy.getFamilySize(), needy.getDietaryPreferences(), needy.getAdditionalNotes());
    }
}
