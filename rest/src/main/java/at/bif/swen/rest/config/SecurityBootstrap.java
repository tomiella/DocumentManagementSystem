package at.bif.swen.rest.config;

import at.bif.swen.rest.entity.Role;
import at.bif.swen.rest.entity.User;
import at.bif.swen.rest.repository.RoleRepository;
import at.bif.swen.rest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class SecurityBootstrap {

    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;

    @Bean
    CommandLineRunner seedAdmin() {
        return args -> {
            Role adminRole = roles.findByName("ROLE_ADMIN")
                    .orElseGet(() -> roles.save(Role.builder().name("ROLE_ADMIN").build()));

            users.findByUsernameIgnoreCase("admin").orElseGet(() -> {
                User admin = User.builder()
                        .id(UUID.randomUUID())
                        .username("admin")
                        .passwordHash(encoder.encode("admin")) // change in prod
                        .enabled(true)
                        .roles(Set.of(adminRole))
                        .build();
                return users.save(admin);
            });

            Role userRole = roles.findByName("ROLE_USER")
                    .orElseGet(() -> roles.save(Role.builder().name("ROLE_USER").build()));

            users.findByUsernameIgnoreCase("user").orElseGet(() -> {
                User u = User.builder()
                        .id(UUID.randomUUID())
                        .username("user")
                        .passwordHash(encoder.encode("user"))
                        .enabled(true)
                        .roles(Set.of(userRole))
                        .build();
                return users.save(u);
            });
        };
    }
}
