package tw.thootau.thatchatapplication.Structs;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GetChattedUserListResponse extends GeneralApiResponse {
    @JsonProperty("data")

    public List<RecentlyChattedUser> data;
    @JsonProperty("message_count")
    public int messageCount;
}
