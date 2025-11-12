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

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        User user = authService.authenticate(req.username(), req.password());
        String token = jwtService.generateToken(
                user.getUsername(),
                props.jwtTtlSeconds(),
                Map.of("roles", user.getRoles().stream().map(r -> r.getName()).toList())
        );
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
