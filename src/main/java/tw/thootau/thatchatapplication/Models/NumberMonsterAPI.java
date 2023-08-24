package tw.thootau.thatchatapplication.Models;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tw.thootau.thatchatapplication.Interfaces.ExternalAPI;
import tw.thootau.thatchatapplication.Properties.NumberMonsterProperties;
import tw.thootau.thatchatapplication.Structs.GetLoginApiResponse;
import tw.thootau.thatchatapplication.Structs.GetMessagesApiResponse;
import tw.thootau.thatchatapplication.Structs.MessageEntry;
import tw.thootau.thatchatapplication.Structs.RecentlyChattedUser;
import java.io.IOException;
import java.util.List;

@Component
public class NumberMonsterAPI implements ExternalAPI {
    NumberMonsterProperties numberMonsterProperties;
    @Autowired
    NumberMonsterAPI(NumberMonsterProperties numberMonsterProperties) {
        this.numberMonsterProperties = numberMonsterProperties;
    }

    @Override
    public String login(String mail, String password, int applicationVersion) throws IOException {
        HttpUrl apiRequestURL = new HttpUrl.Builder()
                .scheme("https")
                .host(numberMonsterProperties.getHost())
                .addPathSegment("login")
                .addPathSegment("check")
                .addQueryParameter("mail", mail)
                .addQueryParameter("password", password)
                .addQueryParameter("applicationVersion", String.valueOf(applicationVersion)).build();
        Request request = new Request.Builder()
                .url(apiRequestURL)
                .addHeader("user-agent", "iOS 16.2 iPhone14,4 ja-JP 4.12.6 (491)")
                .build();
        OkHttpClient client = new OkHttpClient();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                ObjectMapper mapper  = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                String responseJsonString = response.body().string();
                GetLoginApiResponse apiResponse = mapper.readValue(responseJsonString, GetLoginApiResponse.class);
                return apiResponse.data.authKey;
            }
        } catch (IOException error) {
            throw error;
        }
        return "";
    }
    @Override
    public List<MessageEntry> getMessage(String authKey, String publicKey, int applicationVersion) throws IOException {
        String urlGoingToFetch = String.format("https://%s/message/get?auth_key=%s&public_key=%s&start=0&version=%d", numberMonsterProperties.getHost(), authKey, publicKey, applicationVersion);
        Request request = new Request.Builder()
                .url(urlGoingToFetch)
                .addHeader("user-agent", "iOS 16.2 iPhone14,4 ja-JP 4.12.6 (491)")
                .build();
        OkHttpClient client = new OkHttpClient();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();
                GetMessagesApiResponse apiResponse = objectMapper.readValue(response.body().string(), GetMessagesApiResponse.class);
                return apiResponse.data;
            }
        } catch (IOException error) {
            throw error;
        }
        return null;
    }

    @Override
    public List<RecentlyChattedUser> getRecentlyChattedUsers() {
        return null;
    }

    @Override
    public void sendMessage(String authKey, String publicKey, String messageContent, int applicationVersion) {

    }
}
