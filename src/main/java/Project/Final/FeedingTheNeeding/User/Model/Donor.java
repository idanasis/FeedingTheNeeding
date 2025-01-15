package Project.Final.FeedingTheNeeding.User.Model;


import Project.Final.FeedingTheNeeding.Authentication.DTO.RegistrationStatus;
import Project.Final.FeedingTheNeeding.Authentication.Model.UserCredentials;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = "userCredentials")
public class Donor extends BaseUser{
    @Column(nullable = true)
    private String email;
    private double timeOfDonation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegistrationStatus status;
    @OneToOne(cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore
    private UserCredentials userCredentials;

    private boolean volunteeredDuringLastMonth;

    private String verificationCode;
    private LocalDateTime verificationCodeExpiresAt;
    private boolean verified;
}