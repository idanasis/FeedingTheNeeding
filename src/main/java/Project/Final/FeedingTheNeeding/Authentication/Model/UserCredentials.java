package Project.Final.FeedingTheNeeding.Authentication.Model;

import Project.Final.FeedingTheNeeding.User.Model.BaseUser;
import Project.Final.FeedingTheNeeding.User.Model.Donor;
import Project.Final.FeedingTheNeeding.User.Model.UserRole;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@Entity
public class UserCredentials implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private LocalDateTime lastPasswordChangeAt = LocalDateTime.now();

    @OneToOne(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "base_user_id", nullable = false)
    private Donor donor;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Prefix roles with "ROLE_" as Spring Security expects
        if (donor != null && donor.getUserCredentials() != null) {
            UserRole role = donor.getUserCredentials().getDonor().getRole();
            return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
        }
        return List.of();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return donor.isVerified();
    }

    public void setEnabled(boolean enabled) {
        donor.setVerified(enabled);
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return phoneNumber;
    }
}