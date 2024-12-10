package Project.Final.FeedingTheNeeding.User.Mappers;

import Project.Final.FeedingTheNeeding.User.DTO.DonorDTO;
import Project.Final.FeedingTheNeeding.User.Model.Donor;

public interface DonorMapper {
    Donor fromDTO(DonorDTO donatorDTO);
    DonorDTO toDTO(Donor donor);
}