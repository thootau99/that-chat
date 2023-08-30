package tw.thootau.thatchatapplication.Structs.Db;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "UserFromNumberMonsters")
public class UserFromNumberMonstersInDb {
    @Id
    public String id;
    @NonNull
    @Getter
    @Setter
    public String publicKey;
    @NonNull
    @Getter
    @Setter
    public String name;
}
