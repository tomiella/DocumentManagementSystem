package at.bif.swen.rest.service;

import at.bif.swen.rest.entity.User;
import at.bif.swen.rest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder encoder;

    public User authenticate(String username, String rawPassword) {
        User u = users.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if (!u.isEnabled() || !encoder.matches(rawPassword, u.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }
        return u;
    }
}
