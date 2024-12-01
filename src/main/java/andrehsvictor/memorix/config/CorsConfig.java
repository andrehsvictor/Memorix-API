package andrehsvictor.memorix.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${memorix.cors.allowed-origins:*}")
    private String[] allowedOrigins = { "*" };

    @Value("${memorix.cors.allowed-methods:GET,POST,PUT,DELETE,PATCH}")
    private String[] allowedMethods = { "GET", "POST", "PUT", "DELETE", "PATCH" };

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods(allowedMethods);
    }

}
