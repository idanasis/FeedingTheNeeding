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

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "needy_id", nullable = false)
    private Needy needy;

    private WeekStatus weekStatus;
    private String dietaryPreferences; // e.g., "Vegetable, No Sugar"

    @Column(length = 1000)
    private String additionalNotes;


}
