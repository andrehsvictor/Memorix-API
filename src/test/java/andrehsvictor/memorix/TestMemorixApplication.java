package andrehsvictor.memorix;

import org.springframework.boot.SpringApplication;

public class TestMemorixApplication {

	public static void main(String[] args) {
		SpringApplication.from(MemorixApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
