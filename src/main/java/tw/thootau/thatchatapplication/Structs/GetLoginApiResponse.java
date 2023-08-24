package tw.thootau.thatchatapplication.Structs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetLoginApiResponse {
    @JsonProperty("result")
    public int result;

    @JsonProperty("data")
    public GetLoginApiData data;

    public class GetLoginApiData {
        @JsonProperty("auth_key")
        public String authKey;
    }
}
