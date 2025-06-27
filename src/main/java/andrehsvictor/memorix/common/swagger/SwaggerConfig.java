package andrehsvictor.memorix.common.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    private static final String BEARER_TOKEN_SCHEME = "bearer";
    private static final String JWT_BEARER_FORMAT = "JWT";
    private static final String AUTHORIZATION_SCOPE_DESCRIPTION = "Full access to API";
    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(buildApiInfo())
                .servers(buildServers())
                .components(buildComponents())
                .addSecurityItem(buildSecurityRequirement());
    }

    private Info buildApiInfo() {
        return new Info()
                .title("Memorix API")
                .description("A comprehensive flashcard learning system API that helps users create, manage, and review flashcards with spaced repetition algorithms.")
                .version("v1.0.0")
                .contact(new Contact()
                        .name("Andre Victor")
                        .email("andrehsvictor@gmail.com")
                        .url("https://github.com/andrehsvictor"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }

    private List<Server> buildServers() {
        return List.of(
                new Server()
                        .url("http://localhost:8080")
                        .description("Development server"),
                new Server()
                        .url("https://api.memorix.com")
                        .description("Production server")
        );
    }

    private Components buildComponents() {
        return new Components()
                .addSecuritySchemes(SECURITY_SCHEME_NAME, buildSecurityScheme());
    }

    private SecurityScheme buildSecurityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme(BEARER_TOKEN_SCHEME)
                .bearerFormat(JWT_BEARER_FORMAT)
                .description("JWT Bearer token authentication. Use the format: 'Bearer {your-jwt-token}'");
    }

    private SecurityRequirement buildSecurityRequirement() {
        return new SecurityRequirement().addList(SECURITY_SCHEME_NAME);
    }
}
