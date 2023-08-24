package tw.thootau.thatchatapplication.Interfaces;
import tw.thootau.thatchatapplication.Structs.MessageEntry;
import tw.thootau.thatchatapplication.Structs.RecentlyChattedUser;

import java.io.IOException;
import java.util.List;

public interface ExternalAPI {

    String login(String mail, String password, int applicationVersion) throws Exception;
    List<MessageEntry> getMessage(String authKey, String publicKey, int applicationVersion) throws IOException;
    void sendMessage( String authKey, String publicKey, String messageContent, int applicationVersion );
    List<RecentlyChattedUser> getRecentlyChattedUsers();
}