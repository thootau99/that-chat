package tw.thootau.thatchatapplication.Models;

import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import tw.thootau.thatchatapplication.Environments.BotEnvironment;
import tw.thootau.thatchatapplication.Service.UserFromNumberMonstersService;
import tw.thootau.thatchatapplication.Service.UserService;
import tw.thootau.thatchatapplication.Structs.Db.UserFromNumberMonstersInDb;
import tw.thootau.thatchatapplication.Structs.Db.UserInDb;
import tw.thootau.thatchatapplication.Structs.MessageEntry;
import tw.thootau.thatchatapplication.Structs.RecentlyChattedUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ThatChatBot extends TelegramLongPollingBot {
    private final BotEnvironment botEnvironment;
    private final NumberMonsterAPI numberMonsterAPI;
    private final UserService userService;
    private final UserFromNumberMonstersService userFromNumberMonstersService;
    private final MessageSource messageSource;

    @Autowired
    public ThatChatBot(BotEnvironment botEnvironment, NumberMonsterAPI numberMonsterAPI, UserService userService, MessageSource messageSource, UserFromNumberMonstersService userFromNumberMonstersService) {
        super(botEnvironment.getSecret());
        this.botEnvironment = botEnvironment;
        this.numberMonsterAPI = numberMonsterAPI;
        this.messageSource = messageSource;
        this.userService = userService;
        this.userFromNumberMonstersService = userFromNumberMonstersService;
        SetMyCommands setMyCommands = new SetMyCommands();
        setMyCommands.setCommands(Arrays.asList(
                new BotCommand("/login", messageSource.getMessage("command.login", null, Locale.JAPAN)),
                new BotCommand("/set_auth_key", messageSource.getMessage("command.set_auth_key", null, Locale.JAPAN)),
                new BotCommand("/set_chat_target_public_key", messageSource.getMessage("command.set_chat_target_public_key", null, Locale.JAPAN)),
                new BotCommand("/get_message", messageSource.getMessage("command.get_message", null, Locale.JAPAN)),
                new BotCommand("/get_self_data_in_db", messageSource.getMessage("command.get_self_data_in_db", null, Locale.JAPAN)),
                new BotCommand("/get_recently_chatted_targets", messageSource.getMessage("command.get_recently_chatted_targets", null, Locale.JAPAN)),
                new BotCommand("/send_message", messageSource.getMessage("command.send_message", null, Locale.JAPAN))
        ));
        try {
            // Create the TelegramBotsApi object to register your bots
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            // Register your newly created AbilityBot
            botsApi.registerBot(this);
            this.execute(setMyCommands);
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
                case "/login" -> {
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
                }
                case "/set_auth_key" -> {
                    final String setAuthKeyRegex = "/set_auth_key\\s+(?<key>.*)";
                    final Pattern setAuthKeyPattern = Pattern.compile(setAuthKeyRegex);
                    final Matcher setAuthKeyMatcher = setAuthKeyPattern.matcher(messageText);
                    boolean setAuthKeyMatched = setAuthKeyMatcher.matches();
                    if (setAuthKeyMatched) {
                        String key = setAuthKeyMatcher.group("key");
                        try {
                            userService.setAuthKey(chatId, key);
                            String successfulMessage = messageSource.getMessage("message.ok", null, Locale.JAPAN);
                            this.sendMessage(chatId, successfulMessage);
                        } catch (Exception ex) {
                            this.sendMessage(chatId, ex.getMessage());
                        }
                    }
                }
                case "/set_chat_target_public_key" -> {
                    final String setChatTargetPublicKeyRegex = "/set_chat_target_public_key\\s+(?<key>.*)";
                    final Pattern setChatTargetPublicKeyPattern = Pattern.compile(setChatTargetPublicKeyRegex);
                    final Matcher setChatTargetPublicKeyMatcher = setChatTargetPublicKeyPattern.matcher(messageText);
                    boolean setChatTargetPublicKeyMatched = setChatTargetPublicKeyMatcher.matches();
                    setChatTargetPublicKey(chatId, setChatTargetPublicKeyMatcher, setChatTargetPublicKeyMatched);
                }
                case "/get_message" -> {
                    final String getMessageRegex = "/get_message\\s+(?<count>.*)";
                    final Pattern getMessagePattern = Pattern.compile(getMessageRegex);
                    final Matcher getMessageMatcher = getMessagePattern.matcher(messageText);
                    boolean getMessageMatched = getMessageMatcher.matches();
                    if (getMessageMatched) {
                        int count = Integer.parseInt(getMessageMatcher.group("count"));
                        try {
                            List<String> messages = this.getMessage(chatId, count);
                            this.sendMessage(chatId, String.join("\n", messages));
                        } catch (Exception ex) {
                            String errorMessage = messageSource.getMessage("message.failed", new String[]{ex.getMessage()}, Locale.JAPAN);
                            this.sendMessage(chatId, errorMessage);
                        }
                    }
                }
                case "/send_message" -> {
                    final String sendMessageRegex = "/send_message (?<messageContent>[^$]*)";
                    final Pattern sendMessagePattern = Pattern.compile(sendMessageRegex);
                    final Matcher sendMessageMatcher = sendMessagePattern.matcher(messageText);
                    boolean sendMessageMatched = sendMessageMatcher.matches();
                    if (sendMessageMatched) {
                        String messageContent = sendMessageMatcher.group("messageContent");
                        try {
                            UserInDb userInDb = userService.getUser(chatId);
                            Boolean result = this.numberMonsterAPI.sendMessage(userInDb.apiAuthKey, userInDb.targetPublicKey, messageContent, 462);
                            if (result) this.sendMessage(chatId, "OK");
                            else this.sendMessage(chatId, "not ok");
                        } catch (Exception ex) {
                            String errorMessage = messageSource.getMessage("message.failed", new String[]{ex.getMessage()}, Locale.JAPAN);
                            this.sendMessage(chatId, errorMessage);
                        }
                    }
                }
                case "/get_recently_chatted_targets" -> {
                    try {
                        List<RecentlyChattedUser> userLists = this.getRecentlyChattedUsers(chatId);
                        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                        userLists.forEach(user -> {
                            List<InlineKeyboardButton> rowInline = new ArrayList<>();
                            InlineKeyboardButton button = new InlineKeyboardButton();
                            String callbackMessage = String.format("/set_chat_target_public_key %s", user.getPublicKey());
                            button.setText(user.getName());
                            button.setCallbackData(callbackMessage);
                            rowInline.add(button);
                            rowsInline.add(rowInline);
                        });
                        markupInline.setKeyboard(rowsInline);
                        SendMessage message = new SendMessage();
                        message.setChatId(chatId);
                        message.setText(messageSource.getMessage("message.dialog.set_public_key", null, Locale.JAPAN));
                        message.setReplyMarkup(markupInline);
                        this.sendMessage(message);
                    } catch (Exception ignored) {
                        String errorMessage = messageSource.getMessage("message.user.notfound", null, Locale.JAPAN);
                        this.sendMessage(chatId, errorMessage);
                    }
                }
                case "/get_self_data_in_db" -> {
                    try {
                        UserInDb userInDb = userService.getUser(chatId);
                        this.sendMessage(chatId, userInDb.toString());
                    } catch (NotFoundException ex) {
                        String errorMessage = messageSource.getMessage("message.user.notfound", null, Locale.JAPAN);
                        this.sendMessage(chatId, errorMessage);
                    }
                }
            }
        } else if (update.hasCallbackQuery()) {
            // Set variables
            String callbackMessage = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callbackMessage.split(" ")[0].equals("/set_chat_target_public_key")) {
                final String setChatTargetPublicKeyRegex = "/set_chat_target_public_key\\s+(?<key>.*)";
                final Pattern setChatTargetPublicKeyPattern = Pattern.compile(setChatTargetPublicKeyRegex);
                final Matcher setChatTargetPublicKeyMatcher = setChatTargetPublicKeyPattern.matcher(callbackMessage);
                boolean setChatTargetPublicKeyMatched = setChatTargetPublicKeyMatcher.matches();
                setChatTargetPublicKey(chatId, setChatTargetPublicKeyMatcher, setChatTargetPublicKeyMatched);
            }

        }
    }

    private void setChatTargetPublicKey(long chatId, Matcher setChatTargetPublicKeyMatcher, boolean setChatTargetPublicKeyMatched) {
        if (setChatTargetPublicKeyMatched) {
            String key = setChatTargetPublicKeyMatcher.group("key");
            try {
                this.setChatTargetPublicKey(chatId, key);
                String successfulMessage = messageSource.getMessage("message.ok", null, Locale.JAPAN);
                this.sendMessage(chatId, successfulMessage);
            } catch (Exception ex) {
                this.sendMessage(chatId, ex.getMessage());
            }
        }
    }

    void login(long telegramId, String mail, String password) throws Exception {
        String authKey = this.numberMonsterAPI.login(mail, password, 491);
        userService.setAuthKey(telegramId, authKey);
    }

    void setChatTargetPublicKey(long telegramId, String targetPublicKey) throws Exception {
        userService.setChatTargetPublicKey(telegramId, targetPublicKey);
    }

    List<String> getMessage(long telegramId, int count) throws Exception {
        UserInDb userInDb = userService.getUser(telegramId);
        String errorMessage = messageSource.getMessage("message.user.notfound", null, Locale.JAPAN);
        if (userInDb.targetPublicKey == null) throw new Exception(errorMessage);
        List<MessageEntry> messagesEntry;
        try {
            messagesEntry = this.numberMonsterAPI.getMessage(userInDb.apiAuthKey, userInDb.targetPublicKey, 491);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String targetName;
        try {
            UserFromNumberMonstersInDb targetUser = userFromNumberMonstersService.getByPublicKey(userInDb.targetPublicKey);
            targetName = targetUser.getName();
        } catch (Exception ex) {
            targetName = "話し相手";
        }
        String finalTargetName = targetName;
        return messagesEntry.stream().limit(count).map(messageEntry -> {
            String fromUser = messageEntry.align.equals("0") ? "自分:" : String.format("%s:", finalTargetName);
            if (messageEntry.message != null)
                return fromUser + messageEntry.message;
            else
                return fromUser + String.format("https://%s/images/send/%s", numberMonsterAPI.getCdnHost(), messageEntry.image);
        }).toList();
    }

    List<RecentlyChattedUser> getRecentlyChattedUsers(long telegramId) throws Exception {
        UserInDb userInDb = userService.getUser(telegramId);
        return numberMonsterAPI.getRecentlyChattedUsers(userInDb.apiAuthKey, 492);
    }

    void sendMessage(long telegramId, String content) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(telegramId);
        sendMessage.setText(content);
        sendMessage.setDisableWebPagePreview(true);
        try {
            execute(sendMessage);
        } catch (TelegramApiException ex) {
            throw new RuntimeException(ex);
        }
    }

    void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String getBotUsername() {
        return this.botEnvironment.getName();
    }
}/**/