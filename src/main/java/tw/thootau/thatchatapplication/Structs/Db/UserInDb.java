package tw.thootau.thatchatapplication.Structs.Db;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Users")
public class UserInDb {
    @Id
    public String id;
    @NonNull
    @Getter
    @Setter
    public long telegramId;
    @NonNull
    @Getter
    @Setter
    public String apiAuthKey;

    @Getter
    @Setter
    public String targetPublicKey;
}
