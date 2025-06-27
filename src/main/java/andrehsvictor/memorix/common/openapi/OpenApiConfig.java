package andrehsvictor.memorix.common.openapi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Memorix API")
                        .description("API for Memorix - A modern flashcard application with spaced repetition")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Andre Henrique Silva Victor")
                                .email("andrehsvictor@gmail.com")
                                .url("https://github.com/andrehsvictor"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token for authentication. Format: Bearer {token}")))
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("Development server"))
                .addServersItem(new Server()
                        .url("https://api.memorix.com")
                        .description("Production server"));
    }
}
