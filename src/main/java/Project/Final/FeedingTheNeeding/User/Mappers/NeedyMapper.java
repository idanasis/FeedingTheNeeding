package Project.Final.FeedingTheNeeding.User.Mappers;

import Project.Final.FeedingTheNeeding.User.DTO.NeedyDTO;
import Project.Final.FeedingTheNeeding.User.Model.Needy;

public interface NeedyMapper {
    Needy fromDTO(NeedyDTO needyDTO);
    NeedyDTO toDTO(Needy needy);
}