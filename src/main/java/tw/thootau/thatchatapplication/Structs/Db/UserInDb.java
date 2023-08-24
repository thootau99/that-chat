package tw.thootau.thatchatapplication.Structs.Db;

import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Users")
public class UserInDb {
    @Id
    public String id;
    @NonNull
    public long telegramId;
    @NonNull
    public String apiAuthKey;
    public String targetAuthKey;
}
