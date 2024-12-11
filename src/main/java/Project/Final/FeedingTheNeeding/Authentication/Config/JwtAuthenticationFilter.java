//package Project.Final.FeedingTheNeeding.Authentication.Config;
//
//import Project.Final.FeedingTheNeeding.Authentication.Service.CustomUserDetailsService;
//import Project.Final.FeedingTheNeeding.Authentication.Service.JwtTokenService;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Configuration
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private final JwtTokenService jwtTokenService;
//    private final CustomUserDetailsService customUserDetailsService;
//
//    public JwtAuthenticationFilter(JwtTokenService jwtTokenService, CustomUserDetailsService customUserDetailsService) {
//        this.jwtTokenService = jwtTokenService;
//        this.customUserDetailsService = customUserDetailsService;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        final String authHeader = request.getHeader("Authorization");
//        String email = null;
//        String jwtToken = null;
//
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            jwtToken = authHeader.substring(7);
//            email = jwtTokenService.extractEmail(jwtToken);
//        }
//
//        if(email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
//
//            if(jwtTokenService.validateToken(jwtToken, userDetails)) {
//                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            }
//        }
//        filterChain.doFilter(request, response);
//    }
//}