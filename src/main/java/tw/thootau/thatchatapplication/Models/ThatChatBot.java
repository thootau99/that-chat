package tw.thootau.thatchatapplication.Models;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import tw.thootau.thatchatapplication.Properties.BotProperties;
import tw.thootau.thatchatapplication.Repository.UsersRepository;
import tw.thootau.thatchatapplication.Structs.Db.UserInDb;
import tw.thootau.thatchatapplication.Structs.MessageEntry;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ConfigurationPropertiesScan
@Component
public class ThatChatBot extends TelegramLongPollingBot {
    private final BotProperties botProperties;
    private final NumberMonsterAPI numberMonsterAPI;
    private final UsersRepository usersRepository;

    private MessageSource messageSource;
    @Autowired
    public ThatChatBot(BotProperties botProperties, NumberMonsterAPI numberMonsterAPI, UsersRepository usersRepository, MessageSource messageSource) {
        super(botProperties.getSecret());
        System.out.printf(messageSource.getMessage(
                "message.bot.init",
                new String[]{botProperties.getSecret(), botProperties.getName()},
                Locale.JAPANESE));
        this.botProperties = botProperties;
        this.numberMonsterAPI = numberMonsterAPI;
        this.usersRepository = usersRepository;
        this.messageSource = messageSource;

        try {
            // Create the TelegramBotsApi object to register your bots
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            // Register your newly created AbilityBot
            botsApi.registerBot(this);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            switch (messageText.split(" ")[0]) {
                case "/login":
                    final String loginRegex = "/login\\s+(?<mail>.*)\\s+(?<password>.*)";
                    final Pattern loginPattern = Pattern.compile(loginRegex);
                    final Matcher loginMatcher = loginPattern.matcher(messageText);
                    boolean loginMatch = loginMatcher.matches();
                    if (loginMatch) {

                        String mail = loginMatcher.group("mail");
                        String password = loginMatcher.group("password");
                        try {
                            this.login(chatId, mail, password);
                            this.sendMessage(chatId, "ログイン成功しました");
                        } catch (Exception ex) {
                            this.sendMessage(chatId, ex.getMessage());
                        }
                    }
                    break;
                case "/set_auth_key":
                    final String setAuthKeyRegex = "/set_auth_key\\s+(?<key>.*)";
                    final Pattern setAuthKeyPattern = Pattern.compile(setAuthKeyRegex);
                    final Matcher setAuthKeyMatcher = setAuthKeyPattern.matcher(messageText);
                    Boolean setAuthKeyMatched = setAuthKeyMatcher.matches();
                    if (setAuthKeyMatched) {
                        String key = setAuthKeyMatcher.group("key");
                        try {
                            this.setAuthKey(chatId, key);
                            this.sendMessage(chatId, "OK");
                        } catch (Exception ex) {
                            this.sendMessage(chatId, ex.getMessage());
                        }
                    }
                    break;
                case "/set_chat_target_public_key":
                    final String setChatTargetPublicKeyRegex = "/set_chat_target_public_key\\s+(?<key>.*)";
                    final Pattern setChatTargetPublicKeyPattern = Pattern.compile(setChatTargetPublicKeyRegex);
                    final Matcher setChatTargetPublicKeyMatcher = setChatTargetPublicKeyPattern.matcher(messageText);
                    Boolean setChatTargetPublicKeyMatched = setChatTargetPublicKeyMatcher.matches();
                    if (setChatTargetPublicKeyMatched) {
                        String key = setChatTargetPublicKeyMatcher.group("key");
                        try {
                            this.setChatTargetPublicKey(chatId, key);
                            this.sendMessage(chatId, "OK");
                        } catch (Exception ex) {
                            this.sendMessage(chatId, ex.getMessage());
                        }
                    }
                    break;

                case "/get_message":
                    final String getMessageRegex = "\\/get_message\\s+(?<count>.*)";
                    final Pattern getMessagePattern = Pattern.compile(getMessageRegex);
                    final Matcher getMessageMatcher = getMessagePattern.matcher(messageText);
                    Boolean getMessageMatched = getMessageMatcher.matches();
                    if (getMessageMatched) {
                        int count = Integer.parseInt(getMessageMatcher.group("count"));
                        try {
                            this.getMessage(chatId, count);
                        } catch (Exception ex) {
                            this.sendMessage(chatId, String.format("メッセージが送信失敗しました。Reason: %s", ex.getMessage()));
                        }
                    }

                case "/get_self_data_in_db":
                    Optional<UserInDb> userInDb = this.getUserInDb(chatId);
                    userInDb.ifPresentOrElse(_userInDb -> {
                        this.sendMessage(chatId, _userInDb.toString());
                    }, () -> {
                        this.sendMessage(chatId, "can't find user");
                    });


            }
        }
    }

    void login(long telegramId, String mail, String password) throws Exception {
        String authKey = this.numberMonsterAPI.login(mail, password, 491);
        setAuthKey(telegramId, authKey);
    }

    void setAuthKey(long telegramId, String authKey) {
        Optional<UserInDb> userInDb = this.getUserInDb(telegramId);
        userInDb.ifPresentOrElse(_userInDb -> {
            System.out.println("Update User");
            _userInDb.apiAuthKey = authKey;
            _userInDb.telegramId = telegramId;
            System.out.println(_userInDb.toString());
            usersRepository.save(_userInDb);
        }, () -> {
            System.out.println("New User");
            UserInDb _userInDb = new UserInDb();
            _userInDb.apiAuthKey = authKey;
            _userInDb.telegramId = telegramId;
            usersRepository.save(_userInDb);
        });
    }

    void setChatTargetPublicKey(long telegramId, String targetPublicKey) throws Exception {
        Optional<UserInDb> userInDb = this.getUserInDb(telegramId);
        if (userInDb.isEmpty()) throw new Exception("ユーザーはデータベースに存在していまえん。ログインした後もう一度試してください。");
        userInDb.ifPresent(_userInDb -> {
            System.out.printf("%d: 話し相手のPublic Keyを設定して行います。", _userInDb.getTelegramId());
            _userInDb.targetPublicKey = targetPublicKey;
            usersRepository.save(_userInDb);
        });

    }

    Optional<UserInDb> getUserInDb(long telegramId) {
        return this.usersRepository.findUserInDbByTelegramId(telegramId);
    }

    void getMessage(long telegramId, int count) throws Exception {
            Optional<UserInDb> thisUser = usersRepository.findUserInDbByTelegramId(telegramId);
            if (thisUser.isPresent()) {
                UserInDb userInDb = thisUser.get();
                if (userInDb.targetPublicKey == null || userInDb.apiAuthKey == null) throw new Exception("ユーザーはまだログインしていません。ログインしてください。");
                List<MessageEntry> messagesEntry;
                try {
                    messagesEntry = this.numberMonsterAPI.getMessage(userInDb.apiAuthKey, userInDb.targetPublicKey, 491);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                List<String> messages = messagesEntry.stream().map(messageEntry -> messageEntry.message).toList();
                System.out.println(String.join("\n", messages));
            }
    }

    void sendMessage(long telegramId, String content) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(telegramId);
        sendMessage.setText(content);
        try {
            execute(sendMessage);
        } catch (TelegramApiException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String getBotUsername() {
        return this.botProperties.getName();
    }
}/**/