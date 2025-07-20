package andrehsvictor.memorix.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import andrehsvictor.memorix.common.cors.CorsProperties;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsProperties corsProperties;

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
            "/v3/api-docs",
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
        http.cors(Customizer.withDefaults());
        http.sessionManagement(session -> session.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(authorize -> {
            authorize.requestMatchers(ACTUATOR_ALLOWED_PATHS).permitAll();
            authorize.requestMatchers(ACTUATOR_RESTRICTED_PATHS).hasAuthority("MONITOR");
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
        http.cors(Customizer.withDefaults());
        http.securityMatcher("/**");
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
        http.addFilterBefore(accessTokenFilter, AuthorizationFilter.class);
        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(corsProperties.getAllowedOriginsList());
        configuration.setAllowedMethods(corsProperties.getAllowedMethodsList());
        configuration.setAllowedHeaders(corsProperties.getAllowedHeadersList());
        configuration.setAllowCredentials(corsProperties.getAllowCredentials());
        configuration.setMaxAge(corsProperties.getMaxAge());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
