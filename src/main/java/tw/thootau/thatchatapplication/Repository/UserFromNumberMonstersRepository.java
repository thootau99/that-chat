package tw.thootau.thatchatapplication.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tw.thootau.thatchatapplication.Structs.Db.UserFromNumberMonstersInDb;

import java.util.Optional;

public interface UserFromNumberMonstersRepository extends MongoRepository<UserFromNumberMonstersInDb, String> {
    Optional<UserFromNumberMonstersInDb> findUserFromNumberMonstersInDbByPublicKey(String publicKey);
}
