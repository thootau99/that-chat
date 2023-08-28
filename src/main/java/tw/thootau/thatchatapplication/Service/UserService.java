package tw.thootau.thatchatapplication.Service;

import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tw.thootau.thatchatapplication.Repository.UsersRepository;
import tw.thootau.thatchatapplication.Structs.Db.UserInDb;

import java.util.Optional;

@Service
public class UserService {
    private final UsersRepository usersRepository;
    @Autowired
    public UserService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }
    public UserInDb getUser(long telegramId) throws NotFoundException {
        return usersRepository.findUserInDbByTelegramId(telegramId).orElseThrow(() -> new NotFoundException(""));
    }
    public void setAuthKey(long telegramId, String authKey) {
        Optional<UserInDb> userInDb = usersRepository.findUserInDbByTelegramId(telegramId);
        userInDb.ifPresentOrElse(_userInDb -> {
            _userInDb.apiAuthKey = authKey;
            _userInDb.telegramId = telegramId;
            usersRepository.save(_userInDb);
        }, () -> {
            UserInDb _userInDb = new UserInDb();
            _userInDb.apiAuthKey = authKey;
            _userInDb.telegramId = telegramId;
            usersRepository.save(_userInDb);
        });
    }
    public void setChatTargetPublicKey(long telegramId, String targetPublicKey) throws Exception {
        UserInDb userInDb = this.getUser(telegramId);
        System.out.printf("%d: 話し相手のPublic Keyを設定して行います。", userInDb.getTelegramId());
        userInDb.setTargetPublicKey(targetPublicKey);
        usersRepository.save(userInDb);
    }
}
