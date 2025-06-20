package andrehsvictor.memorix.common.cors;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "memorix.cors")
public class CorsProperties {

    private String allowedOrigins = "*";
    private String allowedMethods = "GET,POST,PUT,DELETE,OPTIONS,PATCH,HEAD";
    private String allowedHeaders = "*";
    private Boolean allowCredentials = true;
    private Long maxAge = 3600L;

    public List<String> getAllowedOriginsList() {
        return parseCommaSeparatedValues(allowedOrigins);
    }

    public List<String> getAllowedMethodsList() {
        return parseCommaSeparatedValues(allowedMethods);
    }

    public List<String> getAllowedHeadersList() {
        return parseCommaSeparatedValues(allowedHeaders);
    }

    private List<String> parseCommaSeparatedValues(String value) {
        if (value == null || value.trim().isEmpty()) {
            return List.of();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}