package andrehsvictor.memorix.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] ALLOWED_PATHS_WITH_POST_METHOD = {
            "/api/v1/auth/token",
            "/api/v1/auth/google",
            "/api/v1/auth/refresh",
            "/api/v1/auth/revoke",
            "/api/v1/users/send-action-email",
            "/api/v1/users",
            "/api/v1/users/verify-email",
            "/api/v1/users/reset-password",
    };

    private static final String[] ALLOWED_PATHS_WITH_ANY_METHOD = {
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
    };

    private static final String[] ALLOWED_PATHS_WITH_PUT_METHOD = {
            "/api/v1/users/email"
    };

    private static final String[] ACTUATOR_ALLOWED_PATHS = {
            "/actuator/health",
    };

    private static final String[] ACTUATOR_RESTRICTED_PATHS = {
            "/actuator/info",
            "/actuator/prometheus",
            "/actuator/metrics"
    };

    @Bean
    @Order(1)
    SecurityFilterChain actuatorSecurityFilterChain(
            HttpSecurity http) throws Exception {
        http.securityMatcher("/actuator/**");
        http.csrf(AbstractHttpConfigurer::disable);
        http.sessionManagement(session -> session.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(authorize -> {
            authorize.requestMatchers(ACTUATOR_ALLOWED_PATHS).permitAll();
            authorize.requestMatchers(ACTUATOR_RESTRICTED_PATHS).hasRole("MONITOR");
            authorize.anyRequest().denyAll();
        });
        http.httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain standardSecurityFilterChain(
            HttpSecurity http,
            JwtDecoder jwtDecoder,
            AccessTokenFilter accessTokenFilter) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.sessionManagement(session -> session.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(authorize -> {
            authorize.requestMatchers(HttpMethod.POST, ALLOWED_PATHS_WITH_POST_METHOD)
                    .permitAll();
            authorize.requestMatchers(HttpMethod.PUT, ALLOWED_PATHS_WITH_PUT_METHOD)
                    .permitAll();
            authorize.requestMatchers(ALLOWED_PATHS_WITH_ANY_METHOD)
                    .permitAll();
            authorize.anyRequest().authenticated();
        });
        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder)));
        http.addFilterAfter(accessTokenFilter, AuthorizationFilter.class);
        return http.build();
    }

}
