package Project.Final.FeedingTheNeeding.social.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class NeederTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private String phone;
    private String statusForWeek; // e.g., "pending", "completed", etc.
    private int familySize;
    private String dietaryPreferences; // e.g., "Vegetable, No Sugar"

    @Column(length = 1000)
    private String additionalNotes;

    // Getters, setters, and constructors handled by Lombok (@Data)
}
