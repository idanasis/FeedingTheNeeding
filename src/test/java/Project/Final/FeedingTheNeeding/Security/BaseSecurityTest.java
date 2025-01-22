package Project.Final.FeedingTheNeeding.Security;

import Project.Final.FeedingTheNeeding.Authentication.Config.JwtAuthenticationFilter;
import Project.Final.FeedingTheNeeding.Authentication.Config.SecurityConfig;
import Project.Final.FeedingTheNeeding.Authentication.Service.AuthService;
import Project.Final.FeedingTheNeeding.Authentication.Service.JwtTokenService;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationProvider;

@WebMvcTest
@Import(SecurityConfig.class)
public abstract class BaseSecurityTest {

    @MockBean
    protected JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    protected AuthenticationProvider authenticationProvider;

    @MockBean
    protected AuthService authService;

    @MockBean
    protected JwtTokenService jwtTokenService;

}
