package tw.thootau.thatchatapplication.Structs;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public class GetMessagesApiResponse {
    @JsonProperty("result")
    public String result;
    @JsonProperty("data")
    public List<MessageEntry> data;
    @JsonProperty("message_count")
    public long messageCount;
}
