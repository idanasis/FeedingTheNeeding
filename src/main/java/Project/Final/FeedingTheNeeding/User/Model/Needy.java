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
    private NeedyStatus statusForWeek; // e.g., "pending", "completed", etc.
    private int familySize;
    private Preference[] dietaryPreferences;

    @Column(length = 1000)
    private String additionalNotes;
}
