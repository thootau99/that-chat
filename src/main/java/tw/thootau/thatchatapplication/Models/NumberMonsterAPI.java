package tw.thootau.thatchatapplication.Models;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tw.thootau.thatchatapplication.Interfaces.ExternalAPI;
import tw.thootau.thatchatapplication.Properties.NumberMonsterProperties;
import tw.thootau.thatchatapplication.Structs.*;

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
    public String login(String mail, String password, int applicationVersion) throws Exception {
        HttpUrl apiRequestURL = new HttpUrl.Builder()
                .scheme("https")
                .host(this.getApiHost())
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
            if (!response.isSuccessful()) throw new Exception("Login Failed");
            ObjectMapper mapper  = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            assert response.body() != null;
            String responseJsonString = response.body().string();
            GetLoginApiResponse apiResponse = mapper.readValue(responseJsonString, GetLoginApiResponse.class);
            return apiResponse.data.authKey;
        }
    }
    @Override
    public List<MessageEntry> getMessage(String authKey, String publicKey, int applicationVersion) throws IOException {
        String urlGoingToFetch = String.format("https://%s/message/get?auth_key=%s&public_key=%s&start=0&version=%d", this.getApiHost(), authKey, publicKey, applicationVersion);
        Request request = new Request.Builder()
                .url(urlGoingToFetch)
                .addHeader("user-agent", "iOS 16.2 iPhone14,4 ja-JP 4.12.6 (491)")
                .build();
        OkHttpClient client = new OkHttpClient();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new Exception("リクエストが失敗しました");
            ObjectMapper objectMapper = new ObjectMapper();
            assert response.body() != null;
            GetMessagesApiResponse apiResponse = objectMapper.readValue(response.body().string(), GetMessagesApiResponse.class);
            return apiResponse.data;
        } catch (IOException error) {
            throw error;
        } catch (Exception ignored) {
        }
        return null;
    }

    @Override
    public List<RecentlyChattedUser> getRecentlyChattedUsers(String authKey, int applicationVersion) throws IOException {
        String urlGoingToFetch = String.format("https://%s/message/getuser?auth_key=%s&start=0&version=%d", this.getApiHost(), authKey, applicationVersion);
        Request request = new Request.Builder()
                .url(urlGoingToFetch)
                .addHeader("user-agent", "iOS 16.2 iPhone14,4 ja-JP 4.12.6 (491)")
                .build();
        OkHttpClient client = new OkHttpClient();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new Exception("リクエストが失敗しました");
            ObjectMapper objectMapper = new ObjectMapper();
            assert response.body() != null;
            String body = response.body().string();
            GetChattedUserListResponse apiResponse = objectMapper.readValue(body, GetChattedUserListResponse.class);
            return apiResponse.data;
        } catch (IOException error) {
            throw error;
        } catch (Exception ignored) {
        }
        return null;
    }

    @Override
    public void sendMessage(String authKey, String publicKey, String messageContent, int applicationVersion) {

    }

    public String getApiHost() {
        return String.format("api.%s", this.numberMonsterProperties.getHost());
    }

    public String getCdnHost() {
        return String.format("cdn.%s", this.numberMonsterProperties.getHost());
    }
}
