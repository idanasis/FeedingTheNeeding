package Project.Final.FeedingTheNeeding.User.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Table(name = "needy")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Needy extends BaseUser {
    private int familySize;
    private NeedyStatus confirmStatus;
}
