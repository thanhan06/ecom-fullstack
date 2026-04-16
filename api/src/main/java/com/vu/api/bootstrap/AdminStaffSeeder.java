package com.vu.api.bootstrap;

import com.vu.api.user.entity.Role;
import com.vu.api.user.repository.RoleRepository;
import com.vu.api.user.entity.User;
import com.vu.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("!test")
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring",
    value = "datasource.driverClassName",
havingValue = "org.postgresql.Driver")
public class AdminStaffSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final Environment env;

    @Override
    @Transactional
    public void run(String... args) {
        Role admin = ensureRole("ADMIN");
        Role staff = ensureRole("STAFF");
        Role user = ensureRole("USER"); // ensure USER role exists

        seedAccount("admin@ecom.local", read("SEED_ADMIN_PASSWORD", "Admin@123"), admin);
        seedAccount("staff@ecom.local", read("SEED_STAFF_PASSWORD", "Staff@123"), staff);
    }

    private Role ensureRole(String name) {
        return roleRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> roleRepository.save(Role.builder().name(name).toString() == null
                        ? Role.builder().name(name).build()
                        : Role.builder().name(name).build()));
    }

    private void seedAccount(String email, String rawPassword, Role role) {
        // 1) ensure user exists
        User user = userRepository.findByEmailIgnoreCase(email).orElseGet(() -> {
            User u = User.builder()
                    .email(email)
                    .passwordHash(passwordEncoder.encode(rawPassword)) // BCrypt
                    .build();
            return userRepository.save(u);
        });

        // 2) reload with roles (fetch-join)
        User userWithRoles = userRepository.findByIdWithRoles(user.getId())
                .orElseThrow(() -> new IllegalStateException("User cannot be reloaded: " + email));

        boolean alreadyHasRole = userWithRoles.getUserRoles().stream()
                .anyMatch(ur -> ur.getRole() != null
                        && ur.getRole().getName() != null
                        && role.getName().equalsIgnoreCase(ur.getRole().getName()));

        if (alreadyHasRole) return;

        // 3) assign via join entity method
        userWithRoles.addRole(role);
        userRepository.save(userWithRoles);
    }

    private String read(String key, String fallback) {
        String v = System.getenv(key);
        if (v == null || v.isBlank()) v = env.getProperty(key);
        return (v == null || v.isBlank()) ? fallback : v;
    }
}