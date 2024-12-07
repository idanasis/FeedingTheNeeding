package Project.Final.FeedingTheNeeding.User.DTO;

import Project.Final.FeedingTheNeeding.User.Model.NeedyStatus;

public record NeedyDTO(BaseUserDTO baseUserDTO, NeedyStatus confirmStatus, int familySize) {
}