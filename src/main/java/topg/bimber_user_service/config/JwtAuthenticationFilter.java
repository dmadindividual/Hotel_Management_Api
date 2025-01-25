package topg.bimber_user_service.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Getter
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    public void setJwtUtils(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Extract JWT token from the Authorization header
        String jwt = jwtUtils.getJwtFromHeader(request);
        if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // Validate the token and extract the username
                String username = jwtUtils.getUsernameFromToken(jwt);

                // Load user details by username
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Validate the token with user details
                if (jwtUtils.validateToken(jwt)) {
                    // Create authentication token and set it in the context
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } catch (Exception e) {
                // Log or handle invalid token exception
                System.err.println("JWT Token validation failed: " + e.getMessage());
            }
        }

        // Proceed with the next filter in the chain
        filterChain.doFilter(request, response);
    }
}
