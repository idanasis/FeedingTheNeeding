package Project.Final.FeedingTheNeeding.social.projection;

public interface NeederTrackingProjection {
    Long getId(); // NeederTracking's ID
    Long getNeedy_Id(); // This will fetch just the needy_id
    String getWeekStatus();
    String getDietaryPreferences();
    String getAdditionalNotes();
}
