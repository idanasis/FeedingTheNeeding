package Project.Final.FeedingTheNeeding.User.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Needy extends BaseUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String statusForWeek; // e.g., "pending", "completed", etc.
    private int familySize;
    private String dietaryPreferences; // e.g., "Vegetable, No Sugar"
    private NeedyStatus confirmStatus;

    @Column(length = 1000)
    private String additionalNotes;
}