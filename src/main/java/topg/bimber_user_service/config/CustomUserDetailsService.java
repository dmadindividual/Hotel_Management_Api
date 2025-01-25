package topg.bimber_user_service.config;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import topg.bimber_user_service.models.Admin;
import topg.bimber_user_service.models.Role;
import topg.bimber_user_service.models.User;
import topg.bimber_user_service.repository.AdminRepository;
import topg.bimber_user_service.repository.UserRepository;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    public CustomUserDetailsService(UserRepository userRepository, AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Try to find User
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + Role.USER.getRole()));
            System.out.println("User Roles: " + authorities); // Debug print
            return new UserDetailsServiceImpl(
                    user.getUsername(),
                    user.getPassword(),
                    authorities
            );
        }

        // Try to find Admin
        Admin admin = adminRepository.findByUsername(username).orElse(null);
        if (admin != null) {
            var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + Role.ADMIN.getRole()));
            System.out.println("Admin Roles: " + authorities); // Debug print
            return new UserDetailsServiceImpl(
                    admin.getUsername(),
                    admin.getPassword(),
                    authorities
            );
        }

        throw new UsernameNotFoundException("User or Admin not found: " + username);
    }
}
