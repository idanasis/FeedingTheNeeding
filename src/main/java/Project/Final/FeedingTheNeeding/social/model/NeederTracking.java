package Project.Final.FeedingTheNeeding.social.model;

import Project.Final.FeedingTheNeeding.User.Model.Needy;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class NeederTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER) // Eagerly fetch the related Needy entity
    @JoinColumn(name = "needy_id", referencedColumnName = "id", nullable = false)
    private Needy needy;

    private String statusForWeek; // e.g., "pending", "completed", etc.
    private int familySize;
    private String dietaryPreferences; // e.g., "Vegetable, No Sugar"

    @Column(length = 1000)
    private String additionalNotes;
}
