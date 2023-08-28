package tw.thootau.thatchatapplication.Models;

import javassist.NotFoundException;
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
import tw.thootau.thatchatapplication.Service.UserService;
import tw.thootau.thatchatapplication.Structs.Db.UserInDb;
import tw.thootau.thatchatapplication.Structs.MessageEntry;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ConfigurationPropertiesScan
@Component
public class ThatChatBot extends TelegramLongPollingBot {
    private final BotProperties botProperties;
    private final NumberMonsterAPI numberMonsterAPI;

    private final UserService userService;

    private final MessageSource messageSource;
    @Autowired
    public ThatChatBot(BotProperties botProperties, NumberMonsterAPI numberMonsterAPI, UserService userService, MessageSource messageSource) {
        super(botProperties.getSecret());
        this.botProperties = botProperties;
        this.numberMonsterAPI = numberMonsterAPI;
        this.messageSource = messageSource;
        this.userService = userService;

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
                            String successfulMessage = messageSource.getMessage("message.login.successful", null, Locale.JAPAN);
                            this.sendMessage(chatId, successfulMessage);
                        } catch (Exception ex) {
                            this.sendMessage(chatId, ex.getMessage());
                        }
                    }
                    break;
                case "/set_auth_key":
                    final String setAuthKeyRegex = "/set_auth_key\\s+(?<key>.*)";
                    final Pattern setAuthKeyPattern = Pattern.compile(setAuthKeyRegex);
                    final Matcher setAuthKeyMatcher = setAuthKeyPattern.matcher(messageText);
                    boolean setAuthKeyMatched = setAuthKeyMatcher.matches();
                    if (setAuthKeyMatched) {
                        String key = setAuthKeyMatcher.group("key");
                        try {
                            userService.setAuthKey(chatId, key);
                            String successfulMessage = messageSource.getMessage("message.ok", null, Locale.JAPAN);
                            System.out.println(successfulMessage);
                            System.out.println("successfulMessage");
                            this.sendMessage(chatId, successfulMessage);
                        } catch (Exception ex) {
                            this.sendMessage(chatId, ex.getMessage());
                        }
                    }
                    break;
                case "/set_chat_target_public_key":
                    final String setChatTargetPublicKeyRegex = "/set_chat_target_public_key\\s+(?<key>.*)";
                    final Pattern setChatTargetPublicKeyPattern = Pattern.compile(setChatTargetPublicKeyRegex);
                    final Matcher setChatTargetPublicKeyMatcher = setChatTargetPublicKeyPattern.matcher(messageText);
                    boolean setChatTargetPublicKeyMatched = setChatTargetPublicKeyMatcher.matches();
                    if (setChatTargetPublicKeyMatched) {
                        String key = setChatTargetPublicKeyMatcher.group("key");
                        try {
                            String successfulMessage = messageSource.getMessage("message.ok", null, Locale.JAPAN);
                            this.sendMessage(chatId, successfulMessage);
                        } catch (Exception ex) {
                            this.sendMessage(chatId, ex.getMessage());
                        }
                    }
                    break;

                case "/get_message":
                    final String getMessageRegex = "\\/get_message\\s+(?<count>.*)";
                    final Pattern getMessagePattern = Pattern.compile(getMessageRegex);
                    final Matcher getMessageMatcher = getMessagePattern.matcher(messageText);
                    boolean getMessageMatched = getMessageMatcher.matches();
                    if (getMessageMatched) {
                        int count = Integer.parseInt(getMessageMatcher.group("count"));
                        try {
                            this.getMessage(chatId, count);
                        } catch (Exception ex) {
                            String errorMessage = messageSource.getMessage("message.failed", new String[]{ex.getMessage()}, Locale.JAPAN);
                            this.sendMessage(chatId, errorMessage);
                        }
                    }

                case "/get_self_data_in_db":
                    try {
                        UserInDb userInDb = userService.getUser(chatId);
                        this.sendMessage(chatId, userInDb.toString());
                    } catch (NotFoundException ex) {
                        String errorMessage = messageSource.getMessage("message.user.notfound", null, Locale.JAPAN);
                        this.sendMessage(chatId, errorMessage);
                    }
            }
        }
    }

    void login(long telegramId, String mail, String password) throws Exception {
        String authKey = this.numberMonsterAPI.login(mail, password, 491);
        userService.setAuthKey(telegramId, authKey);
    }

    void getMessage(long telegramId, int count) throws Exception {
            UserInDb userInDb = userService.getUser(telegramId);
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