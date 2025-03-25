package andrehsvictor.memorix.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {

    private static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static String hash(String password) {
        return passwordEncoder.encode(password);
    }
}
