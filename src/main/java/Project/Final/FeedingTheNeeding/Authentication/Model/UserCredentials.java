package Project.Final.FeedingTheNeeding.Authentication.Model;

import Project.Final.FeedingTheNeeding.User.Model.BaseUser;
import Project.Final.FeedingTheNeeding.User.Model.Donor;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class UserCredentials {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private LocalDateTime lastPasswordChangeAt = LocalDateTime.now();

    @OneToOne
    @JoinColumn(name = "base_user_id", nullable = false)
    private Donor baseUser;
}
