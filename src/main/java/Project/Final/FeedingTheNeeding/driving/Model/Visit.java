package Project.Final.FeedingTheNeeding.driving.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Visit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long visitId;

    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;
    private String address;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private int maxHour;
    private VisitStatus status;
    @Column(length = 1000)
    private String note;
    
    public Visit(String address, String firstName, String lastName, String phoneNumber, int maxHour, VisitStatus status, String note,Route route) {
        this.address = address;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.maxHour = maxHour;
        this.status = status;
        this.note = note;
        this.route = route;
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

}
