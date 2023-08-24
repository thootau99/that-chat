package tw.thootau.thatchatapplication.Models;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
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
import tw.thootau.thatchatapplication.Structs.Enums.TelegramCommandType;
import tw.thootau.thatchatapplication.Structs.MessageEntry;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ConfigurationPropertiesScan
@Component
public class ThatChatBot extends TelegramLongPollingBot {
    private final BotProperties botProperties;
    private final NumberMonsterAPI numberMonsterAPI;
    private final UsersRepository usersRepository;
    @Autowired
    public ThatChatBot(BotProperties botProperties, NumberMonsterAPI numberMonsterAPI, UsersRepository usersRepository) {
        super(botProperties.getSecret());
        System.out.printf("initing bot with %s, %s", botProperties.getSecret(), botProperties.getName());
        this.botProperties = botProperties;
        this.numberMonsterAPI = numberMonsterAPI;
        this.usersRepository = usersRepository;

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

            System.out.printf("%d: %s", chatId, messageText);

            switch (messageText.split(" ")[0]) {
                case "/login":
                    final String loginRegex = "/login\\s+(?<mail>.*)\\s+(?<password>.*)";
                    final Pattern loginPattern = Pattern.compile(loginRegex);
                    final Matcher loginMatcher = loginPattern.matcher(messageText);
                    boolean match = loginMatcher.matches();
                    try {
                        String mail = loginMatcher.group("mail");
                        String password = loginMatcher.group("password");
                        this.login(chatId, mail, password);
                        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), "OK");
                        execute(sendMessage);
                    } catch (Exception e) {
                        SendMessage sendErrorMessage = new SendMessage();
                        sendErrorMessage.setChatId(chatId);
                        sendErrorMessage.setText(e.toString());
                        try {
                            execute(sendErrorMessage);
                        } catch (Exception sendError) {
                            System.out.println(sendError);
                        }
                    }
                    break;
                case "/set_auth_key":
                    final String setAuthKeyRegex = "/set_auth_key\\s+(?<key>.*)";
                    final Pattern setAuthKeyPattern = Pattern.compile(setAuthKeyRegex);
                    final Matcher setAuthKeyMatcher = setAuthKeyPattern.matcher(messageText);
                    setAuthKeyMatcher.matches();
                    try {
                        String key = setAuthKeyMatcher.group("key");
                        this.setAuthKey(chatId, key);
                    } catch (Exception e) {
                        SendMessage sendErrorMessage = new SendMessage();
                        sendErrorMessage.setChatId(chatId);
                        sendErrorMessage.setText(e.toString());
                        try {
                            execute(sendErrorMessage);
                        } catch (Exception sendError) {
                            System.out.println(sendError);
                        }
                    }
                    break;

                case "/get_message":
                    final String getMessageRegex = "\\/get_message\\s+(?<count>.*)";
                    final Pattern getMessagePattern = Pattern.compile(getMessageRegex);
                    final Matcher getMessageMatcher = getMessagePattern.matcher(messageText);
                    getMessageMatcher.matches();
                    try {
                        int count = Integer.parseInt(getMessageMatcher.group("count"));
                        this.getMessage(chatId, count);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
            }
        }
    }

    void login(long telegramId, String mail, String password) throws IOException {
        try {
            String authKey = this.numberMonsterAPI.login(mail, password, 491);
            setAuthKey(telegramId, authKey);

        } catch (Exception error) {
            SendMessage s = new SendMessage();
        }
    }

    void setAuthKey(long telegramId, String authKey) {
        Optional<UserInDb> userInDb = this.usersRepository.findUserInDbByTelegramId(telegramId);
        userInDb.ifPresentOrElse(_userInDb -> {
            System.out.println("Update User");
            _userInDb.apiAuthKey = authKey;
            _userInDb.telegramId = telegramId;
            System.out.println(authKey);
            usersRepository.save(_userInDb);
        }, () -> {
            System.out.println("New User");
            UserInDb _userInDb = new UserInDb();
            _userInDb.apiAuthKey = authKey;
            _userInDb.telegramId = telegramId;
            usersRepository.save(_userInDb);
        });

    }

    void getMessage(long telegramId, int count) {
            Optional<UserInDb> thisUser = usersRepository.findUserInDbByTelegramId(telegramId);
            thisUser.ifPresent(userInDb -> {
                List<MessageEntry> messagesEntry;
                try {
                    messagesEntry = this.numberMonsterAPI.getMessage(userInDb.apiAuthKey, userInDb.targetAuthKey, 491);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                List<String> messages = messagesEntry.stream().map(messageEntry -> messageEntry.message).toList();

                System.out.println(String.join("\n", messages));
            });
    }

    void executeMessage(TelegramCommandType commandType) {
        switch (commandType) {
            case setAuthKey -> {
                break;
            }
        }
    }

    @Override
    public String getBotUsername() {
        return this.botProperties.getName();
    }
}/**/