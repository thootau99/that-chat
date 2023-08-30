package tw.thootau.thatchatapplication.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import tw.thootau.thatchatapplication.Repository.UserFromNumberMonstersRepository;
import tw.thootau.thatchatapplication.Structs.Db.UserFromNumberMonstersInDb;

import java.util.Locale;
import java.util.Optional;

@Service
public class UserFromNumberMonstersService {

    private final UserFromNumberMonstersRepository userFromNumberMonstersRepository;
    private final MessageSource messageSource;
    @Autowired
    public UserFromNumberMonstersService(UserFromNumberMonstersRepository userFromNumberMonstersRepository, MessageSource messageSource) {
        this.userFromNumberMonstersRepository = userFromNumberMonstersRepository;
        this.messageSource = messageSource;
    }
    public UserFromNumberMonstersInDb getByPublicKey(String publicKey) throws Exception {
        Optional<UserFromNumberMonstersInDb> user = userFromNumberMonstersRepository.findUserFromNumberMonstersInDbByPublicKey(publicKey);
        if (user.isPresent()) return user.get();
        else throw new Exception(messageSource.getMessage("message.user.notfound", null, Locale.JAPAN));
    }

    public void setUser(String publicKey, String name) {
        UserFromNumberMonstersInDb user;
        try {
            user = this.getByPublicKey(publicKey);
        } catch (Exception ex) {
            user = new UserFromNumberMonstersInDb();
        }
        user.setPublicKey(publicKey);
        user.setName(name);
        userFromNumberMonstersRepository.save(user);
    }
}
