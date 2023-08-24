package tw.thootau.thatchatapplication.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tw.thootau.thatchatapplication.Structs.Db.UserInDb;

import java.util.Optional;

public interface UsersRepository extends MongoRepository<UserInDb, String> {
    Optional<UserInDb> findUserInDbByTelegramId(long telegramId);
}
