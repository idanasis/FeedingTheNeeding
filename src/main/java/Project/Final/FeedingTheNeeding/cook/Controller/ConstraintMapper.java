package Project.Final.FeedingTheNeeding.cook.Controller;

import Project.Final.FeedingTheNeeding.cook.DTO.PendingConstraintDTO;
import Project.Final.FeedingTheNeeding.cook.Model.CookConstraints;
import org.springframework.stereotype.Component;

@Component
public class ConstraintMapper {
    public PendingConstraintDTO toDTO(CookConstraints constraint, String name, String phoneNumber) {
        PendingConstraintDTO dto = new PendingConstraintDTO();
        dto.constraintId = constraint.getConstraintId();
        dto.name = name;
        dto.phoneNumber = phoneNumber;
        dto.startTime = constraint.getStartTime();
        dto.endTime = constraint.getEndTime();
        dto.constraints = constraint.getConstraints();
        dto.address = constraint.getLocation();
        dto.date = constraint.getDate();
        dto.status = constraint.getStatus();
        return dto;
    }
}
