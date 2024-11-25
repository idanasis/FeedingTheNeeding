package Project.Final.FeedingTheNeeding.User.DTO;

import Project.Final.FeedingTheNeeding.User.Model.Chef;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ChefDTO extends BaseUserDTO{
    private String[] chefConstraints;

    public ChefDTO(Chef chef) {
        super(chef);
        this.chefConstraints = chef.getChefConstraints();
    }
}