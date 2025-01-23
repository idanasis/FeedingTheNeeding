package Project.Final.FeedingTheNeeding.driving.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@NoArgsConstructor
@ToString(exclude = "route") // Exclude the field that causes the circular reference
public class Visit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long visitId;

    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    @JsonIgnore
    private Route route;
    private String address;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private int startHour;
    private int maxHour;
    @Enumerated(EnumType.STRING)
    private VisitStatus status;
    private int priority;
    @Column(length = 1000)
    private String note;
    @Column(length = 1000)
    private String additionalNotes;
    
    public Visit(String address, String firstName, String lastName, String phoneNumber, int maxHour, VisitStatus status, String note,Route route,int priority, int startHour, String additionalNotes) {
        this.address = address;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.maxHour = maxHour;
        this.status = status;
        this.note = note;
        this.route = route;
        this.priority = priority;
        this.startHour = startHour;
        this.additionalNotes = additionalNotes;
    }
    public long getVisitId() {
        return visitId;
    }
    public String getAddress() {
        return address;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public int getMaxHour() {
        return maxHour;
    }
    public VisitStatus getStatus() {
        return status;
    }
    public String getNote() {
        return note;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public void setMaxHour(int maxHour) {
        this.maxHour = maxHour;
    }
    public void setStatus(VisitStatus status) {
        this.status = status;
    }
    public void setNote(String note) {
        this.note = note;
    }
    public int getPriority() {
        return priority;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }

}
