package Project.Final.FeedingTheNeeding.social.model;

import Project.Final.FeedingTheNeeding.User.Model.Needy;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class NeederTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "needy_id", nullable = false)
    private Needy needy;
    
    @Enumerated(EnumType.STRING)
    private WeekStatus weekStatus;
    private String dietaryPreferences; // e.g., "Vegetable, No Sugar"
    private LocalDate date;

    @Column(length = 1000)
    private String additionalNotes;


}
