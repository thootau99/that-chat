package tw.thootau.thatchatapplication.Structs.Db;

import lombok.Getter;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Users")
public class UserInDb {
    @Id
    public String id;
    @NonNull
    @Getter
    public long telegramId;
    @NonNull
    @Getter
    public String apiAuthKey;
    public String targetPublicKey;
}
