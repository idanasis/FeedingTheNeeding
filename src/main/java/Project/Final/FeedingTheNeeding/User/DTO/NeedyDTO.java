package Project.Final.FeedingTheNeeding.User.DTO;

import Project.Final.FeedingTheNeeding.User.Model.BaseUser;
import Project.Final.FeedingTheNeeding.User.Model.Needy;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NeedyDTO extends BaseUserDTO{
    private String allergies;
    private int quantityOfFoodPortions;

    public NeedyDTO(Needy needy) {
        super(needy);
        this.allergies = needy.getAllergies();
        this.quantityOfFoodPortions = needy.getQuantityOfFoodPortions();
    }
}
