package tw.thootau.thatchatapplication.Structs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetLoginApiResponse extends GeneralApiResponse {
    @JsonProperty("data")
    public GetLoginApiData data;

    public static class GetLoginApiData {
        @JsonProperty("auth_key")
        public String authKey;
    }
}
