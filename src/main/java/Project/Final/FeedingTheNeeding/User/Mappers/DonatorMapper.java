package Project.Final.FeedingTheNeeding.User.Mappers;

import Project.Final.FeedingTheNeeding.User.DTO.DonatorDTO;
import Project.Final.FeedingTheNeeding.User.Model.Donator;

public interface DonatorMapper {
    Donator fromDTO(DonatorDTO donatorDTO);
    DonatorDTO toDTO(Donator donator);
}