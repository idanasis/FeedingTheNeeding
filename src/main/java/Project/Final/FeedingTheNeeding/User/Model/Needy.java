package Project.Final.FeedingTheNeeding.User.Model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Needy extends BaseUser {

    private int familySize;
    @Enumerated(EnumType.STRING)
    private NeedyStatus confirmStatus;
}