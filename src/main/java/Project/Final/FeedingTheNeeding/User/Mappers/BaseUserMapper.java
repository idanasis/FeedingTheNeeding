package Project.Final.FeedingTheNeeding.User.Mappers;


import Project.Final.FeedingTheNeeding.User.DTO.BaseUserDTO;
import Project.Final.FeedingTheNeeding.User.Model.BaseUser;

public interface BaseUserMapper {
    BaseUser fromDTO(BaseUserDTO baseUserDTO);
    BaseUserDTO toDTO(BaseUser baseUser);
}