package Project.Final.FeedingTheNeeding.User.DTO;

import Project.Final.FeedingTheNeeding.User.Model.NeedyStatus;
import Project.Final.FeedingTheNeeding.User.Model.Preference;

public record NeedyDTO(BaseUserDTO baseUserDTO, NeedyStatus statusForWeek, int familySize, Preference[] dietaryPreferences, String additionalNotes) {
}
