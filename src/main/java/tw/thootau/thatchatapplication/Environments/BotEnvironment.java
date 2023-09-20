package tw.thootau.thatchatapplication.Environments;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class BotEnvironment {
    @Value("${BOT_SECRET}")
    private String secret;

    public String getName() {
        return "that-chat-bot";
    }
}
