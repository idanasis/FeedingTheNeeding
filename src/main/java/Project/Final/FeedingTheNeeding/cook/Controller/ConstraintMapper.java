package Project.Final.FeedingTheNeeding.cook.Controller;

import Project.Final.FeedingTheNeeding.cook.DTO.PendingConstraintDTO;
import Project.Final.FeedingTheNeeding.cook.Model.CookConstraints;
import org.springframework.stereotype.Component;

@Component
public class ConstraintMapper {
    public PendingConstraintDTO toDTO(CookConstraints constraint) {
        PendingConstraintDTO dto = new PendingConstraintDTO();
        dto.constraintId = constraint.getConstraintId();
        //TODO: add dto.name = *somehow get the name given the jwt*
        dto.startTime = constraint.getStartTime();
        dto.endTime = constraint.getEndTime();
        dto.mealCount = constraint.getPlatesNum();
        dto.addr = constraint.getLocation();
        dto.date = constraint.getDate();
        dto.status = constraint.getStatus();
        return dto;
    }
}
