package Project.Final.FeedingTheNeeding.cook.DTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

public class UserDTO {
    private Long donorId;
    private LocalDate date;
    private String startTime;
    private String endTime;
    private Map<String, Integer> constraints;

    public UserDTO() {}

    public UserDTO(Long donorId, LocalDate date, String startTime,
                   String endTime, Map<String, Integer> constraints) {
        this.donorId = donorId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.constraints = constraints;
    }

    // Getters
    public Long getDonorId() { return donorId; }
    public LocalDate getDate() { return date; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public Map<String, Integer> getConstraints() { return constraints; }

    // Setters
    public void setDonorId(Long donorId) { this.donorId = donorId; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public void setConstraints(Map<String, Integer> constraints) { this.constraints = constraints; }
}