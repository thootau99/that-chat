package tw.thootau.thatchatapplication.Environments;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class NumberMonsterEnvironment {
    @Value("${REMOTE_API_HOST}")
    private String host;
}
