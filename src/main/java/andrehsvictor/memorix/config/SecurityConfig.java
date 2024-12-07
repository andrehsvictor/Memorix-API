package andrehsvictor.memorix.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

import andrehsvictor.memorix.security.JwtFilter;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtDecoder jwtDecoder;
    private final JwtFilter jwtFilter;

    private static final String[] ALLOWED_PATHS_WITH_POST_METHOD = {
            "/v1/auth/token",
            "/v1/auth/token/refresh",
            "/v1/auth/token/revoke",
            "/v1/users",
    };

    private static final String[] ALLOWED_PATHS_WITH_GET_METHOD = {
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/images/**",
    };

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests((authorize) -> {
            authorize.requestMatchers(HttpMethod.POST, ALLOWED_PATHS_WITH_POST_METHOD).permitAll();
            authorize.requestMatchers(HttpMethod.GET, ALLOWED_PATHS_WITH_GET_METHOD).permitAll();
            authorize.anyRequest().permitAll();
        });
        http.oauth2ResourceServer((oauth2) -> oauth2.jwt((jwt) -> jwt.decoder(jwtDecoder)));
        http.addFilterAfter(jwtFilter, AuthorizationFilter.class);
        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
