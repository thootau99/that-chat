package tw.thootau.thatchatapplication;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ThatChatApplication {
	public static void main(String[] args) {
		SpringApplication.run(ThatChatApplication.class, args);
	}

}
