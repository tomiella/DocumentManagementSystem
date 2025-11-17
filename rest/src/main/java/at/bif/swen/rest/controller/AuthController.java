package at.bif.swen.rest.controller;

import at.bif.swen.rest.dto.LoginRequest;
import at.bif.swen.rest.dto.LoginResponse;
import at.bif.swen.rest.entity.User;
import at.bif.swen.rest.service.AuthService;
import at.bif.swen.rest.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        Optional<User> u = authService.authenticate(req.username(), req.password());
        if (u.isPresent()) {
            User user = u.get();
            String token = jwtService.generateToken(user.getUsername(), Map.of("roles", user.getRoles().stream().map(r -> r.getName()).toList()));
            return ResponseEntity.ok(new LoginResponse(token));
        }
        return ResponseEntity.status(401).body(new LoginResponse("Invalid username or password"));
    }
}
