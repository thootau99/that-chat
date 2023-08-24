package tw.thootau.thatchatapplication.Config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import tw.thootau.thatchatapplication.Properties.BotProperties;

@Configuration
@EnableConfigurationProperties(BotProperties.class)
public class BotConfig {

}