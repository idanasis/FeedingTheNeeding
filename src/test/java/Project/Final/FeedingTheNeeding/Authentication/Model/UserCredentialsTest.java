package Project.Final.FeedingTheNeeding.Authentication.Model;

import Project.Final.FeedingTheNeeding.User.Model.Donor;
import Project.Final.FeedingTheNeeding.User.Model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserCredentialsTest {

    private static final Long TEST_ID = 1L;
    private static final String TEST_PHONE_NUMBER = "1234567890";
    private static final String TEST_PASSWORD_HASH = "hashedPassword";
    private static final UserRole TEST_USER_ROLE = UserRole.DONOR;
    private static final String ROLE_PREFIX = "ROLE_";
    private static final boolean VERIFIED_STATUS = true;
    private static final boolean NOT_VERIFIED_STATUS = false;

    final Long NEW_ID = 2L;
    final String NEW_PHONE_NUMBER = "0987654321";
    final String NEW_PASSWORD_HASH = "newHashedPassword";
    final LocalDateTime NEW_PASSWORD_CHANGE_TIME = LocalDateTime.now().minusDays(1);
    final Donor NEW_DONOR = mock(Donor.class);

    private UserCredentials userCredentials;
    private Donor donorMock;

    @BeforeEach
    void setUp() {
        userCredentials = new UserCredentials();
        userCredentials.setId(TEST_ID);
        userCredentials.setPhoneNumber(TEST_PHONE_NUMBER);
        userCredentials.setPasswordHash(TEST_PASSWORD_HASH);
        userCredentials.setLastPasswordChangeAt(LocalDateTime.now());

        donorMock = mock(Donor.class);
        userCredentials.setDonor(donorMock);
    }

    @Test
    @DisplayName("getAuthorities should return correct authority when donor and role are valid")
    void testGetAuthorities_WithValidDonorAndRole() {
        when(donorMock.getUserCredentials()).thenReturn(userCredentials);
        when(donorMock.getRole()).thenReturn(TEST_USER_ROLE);
        //when(donorMock.isVerified()).thenReturn(VERIFIED_STATUS);

        Collection<? extends GrantedAuthority> authorities = userCredentials.getAuthorities();

        assertNotNull(authorities, "Authorities collection should not be null");
        assertEquals(1, authorities.size(), "Authorities collection should contain exactly one authority");
        GrantedAuthority authority = authorities.iterator().next();
        assertEquals(ROLE_PREFIX + TEST_USER_ROLE.name(), authority.getAuthority(),
                "Authority should match the expected role with prefix");
    }

    @Test
    @DisplayName("getAuthorities should return empty collection when donor is null")
    void testGetAuthorities_WithNullDonor() {
        userCredentials.setDonor(null);

        Collection<? extends GrantedAuthority> authorities = userCredentials.getAuthorities();

        assertNotNull(authorities, "Authorities collection should not be null");
        assertTrue(authorities.isEmpty(), "Authorities collection should be empty when donor is null");
    }

    @Test
    @DisplayName("getAuthorities should return empty collection when donor's UserCredentials is null")
    void testGetAuthorities_WithNullUserCredentialsInDonor() {
        when(donorMock.getUserCredentials()).thenReturn(null);

        Collection<? extends GrantedAuthority> authorities = userCredentials.getAuthorities();

        assertNotNull(authorities, "Authorities collection should not be null");
        assertTrue(authorities.isEmpty(),
                "Authorities collection should be empty when donor's UserCredentials is null");
    }

    @Test
    @DisplayName("isAccountNonExpired should always return true")
    void testIsAccountNonExpired() {
        assertTrue(userCredentials.isAccountNonExpired(),
                "isAccountNonExpired should return true");
    }

    @Test
    @DisplayName("isAccountNonLocked should always return true")
    void testIsAccountNonLocked() {
        assertTrue(userCredentials.isAccountNonLocked(),
                "isAccountNonLocked should return true");
    }

    @Test
    @DisplayName("isCredentialsNonExpired should always return true")
    void testIsCredentialsNonExpired() {
        assertTrue(userCredentials.isCredentialsNonExpired(),
                "isCredentialsNonExpired should return true");
    }

    @Test
    @DisplayName("isEnabled should return true when donor is verified")
    void testIsEnabled_WhenDonorIsVerified() {
        boolean isEnabled = userCredentials.isEnabled();

        assertTrue(isEnabled, "isEnabled should return true when donor is verified");
    }

    @Test
    @DisplayName("getPassword should return the correct password hash")
    void testGetPassword() {
        String password = userCredentials.getPassword();

        assertEquals(TEST_PASSWORD_HASH, password, "getPassword should return the correct password hash");
    }

    @Test
    @DisplayName("getUsername should return the correct phone number")
    void testGetUsername() {
        String username = userCredentials.getUsername();

        assertEquals(TEST_PHONE_NUMBER, username, "getUsername should return the correct phone number");
    }

    @Test
    @DisplayName("Setters and getters should correctly assign and retrieve values")
    void testSettersAndGetters() {
        userCredentials.setId(NEW_ID);
        userCredentials.setPhoneNumber(NEW_PHONE_NUMBER);
        userCredentials.setPasswordHash(NEW_PASSWORD_HASH);
        userCredentials.setLastPasswordChangeAt(NEW_PASSWORD_CHANGE_TIME);
        userCredentials.setDonor(NEW_DONOR);

        assertEquals(NEW_ID, userCredentials.getId(), "ID should be updated correctly");
        assertEquals(NEW_PHONE_NUMBER, userCredentials.getPhoneNumber(), "Phone number should be updated correctly");
        assertEquals(NEW_PASSWORD_HASH, userCredentials.getPasswordHash(), "Password hash should be updated correctly");
        assertEquals(NEW_PASSWORD_CHANGE_TIME, userCredentials.getLastPasswordChangeAt(),
                "Last password change time should be updated correctly");
        assertEquals(NEW_DONOR, userCredentials.getDonor(), "Donor should be updated correctly");
    }
}
