package Project.Final.FeedingTheNeeding.User.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "needy")
@Data
@EqualsAndHashCode(callSuper = true)
public class Needy extends BaseUser {
    private String allergies;
    private int quantityOfFoodPortions;
}
