package andrehsvictor.memorix.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.validation.constraints.NotBlank;

@Validated
@EnableWebMvc
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @NotBlank
    @Value("${memorix.cors.allowed.origins:*}")
    private String allowedOrigins = "*";

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] origins;
        if (allowedOrigins.equals("*")) {
            origins = new String[] { "*" };
        } else {
            origins = allowedOrigins.split(",");
        }
        registry.addMapping("/**").allowedOrigins(origins).allowedMethods("*").allowedHeaders("*");
    }
}
