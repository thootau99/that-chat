package tw.thootau.thatchatapplication.Structs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown = true)

public class GeneralApiResponse {
    @JsonProperty("result")
    public int result;
}
