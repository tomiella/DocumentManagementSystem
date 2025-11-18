package at.bif.swen.rest.config;

import at.bif.swen.rest.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Configuration
@EnableMethodSecurity // optional: needed only if you use @PreAuthorize etc.
public class SecurityConfig {

    private final JwtService jwtService;

    public SecurityConfig(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> {}).csrf(AbstractHttpConfigurer::disable).sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).authorizeHttpRequests(auth -> auth.requestMatchers("/auth/**").permitAll().requestMatchers(HttpMethod.GET, "/actuator/health").permitAll().anyRequest().authenticated()).addFilterBefore(new JwtAuthFilter(jwtService), UsernamePasswordAuthenticationFilter.class).httpBasic(Customizer.withDefaults());

        return http.build();
    }

    static class JwtAuthFilter extends OncePerRequestFilter {
        private final JwtService jwtService;

        JwtAuthFilter(JwtService jwtService) {
            this.jwtService = jwtService;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
            String auth = request.getHeader("Authorization");
            if (auth != null && auth.startsWith("Bearer ")) {
                String token = auth.substring(7);
                try {
                    var jws = jwtService.parse(token);
                    String username = jws.getBody().getSubject();
                    @SuppressWarnings("unchecked") var roles = (List<String>) jws.getBody().get("roles");
                    var authorities = roles == null ? List.<SimpleGrantedAuthority>of() : roles.stream().map(SimpleGrantedAuthority::new).toList();

                    var authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
                    org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"invalid_token\"}");
                    return;
                }
            }
            chain.doFilter(request, response);
        }
    }
}
