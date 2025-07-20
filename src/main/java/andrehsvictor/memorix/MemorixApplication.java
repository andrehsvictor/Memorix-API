package andrehsvictor.memorix;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;

import andrehsvictor.memorix.user.User;
import andrehsvictor.memorix.user.UserProvider;
import andrehsvictor.memorix.user.UserRepository;
import andrehsvictor.memorix.user.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class MemorixApplication implements ApplicationRunner {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final Environment environment;

	public static void main(String[] args) {
		SpringApplication.run(MemorixApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		createMonitorUserInDevelopment();
	}

	/*
	 * Creates a monitor user for development environment.
	 * This user is used for application health monitoring and Prometheus metrics.
	 */
	private void createMonitorUserInDevelopment() {

		if (!"dev".equals(environment.getProperty("spring.profiles.active"))) {
			return;
		}

		String monitorUsername = "monitor";

		if (userRepository.existsByUsername(monitorUsername)) {
			log.debug("Monitor user already exists, skipping creation");
			return;
		}

		try {
			User monitorUser = User.builder()
					.displayName("Monitor User")
					.username(monitorUsername)
					.provider(UserProvider.LOCAL)
					.password(passwordEncoder.encode("monitor"))
					.email("monitor@memorix.io")
					.role(UserRole.MONITOR)
					.emailVerified(true)
					.build();

			userRepository.save(monitorUser);
			log.info("Monitor user created successfully for development environment");

		} catch (Exception e) {
			log.error("Failed to create monitor user: {}", e.getMessage(), e);
		}
	}
}