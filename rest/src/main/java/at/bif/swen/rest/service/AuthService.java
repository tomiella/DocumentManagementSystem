package at.bif.swen.rest.service;

import at.bif.swen.rest.entity.User;
import at.bif.swen.rest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder encoder;

    public Optional<User> authenticate(String username, String rawPassword) {
        Optional<User> u = users.findByUsernameIgnoreCase(username);
        if (u.isPresent()) {
            User user = u.get();
            if (!user.isEnabled() || !encoder.matches(rawPassword, user.getPasswordHash())) {
                return Optional.empty();
            }
        }
        return u;
    }
}
