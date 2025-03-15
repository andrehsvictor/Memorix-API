package andrehsvictor.memorix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class MemorixApplication {

    public static void main(String[] args) {
        SpringApplication.run(MemorixApplication.class, args);
    }

}
