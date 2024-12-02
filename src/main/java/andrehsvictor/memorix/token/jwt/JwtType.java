package andrehsvictor.memorix.token.jwt;

import lombok.Getter;

@Getter
public enum JwtType {
    ACCESS("access"),
    REFRESH("refresh");

    private final String type;

    JwtType(String type) {
        this.type = type;
    }
}
