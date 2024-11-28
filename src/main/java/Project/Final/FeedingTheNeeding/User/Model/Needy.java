package Project.Final.FeedingTheNeeding.User.Model;

import jakarta.persistence.Column;

public class Needy extends BaseUser {
    private NeedyStatus statusForWeek; // e.g., "pending", "completed", etc.
    private int familySize;
    private Preference[] dietaryPreferences;

    @Column(length = 1000)
    private String additionalNotes;
}
