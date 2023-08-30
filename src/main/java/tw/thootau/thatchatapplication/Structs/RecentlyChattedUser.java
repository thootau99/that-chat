package tw.thootau.thatchatapplication.Structs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class RecentlyChattedUser {

    @JsonProperty("message_count")
    private int messageCount;
    @JsonProperty("date")

    private long date;
    @JsonProperty("profile_image")
    private int profileImage;
    @JsonProperty("thumbnail")

    private int thumbnail;
    @JsonProperty("profile_images")

    private List<ProfileImage> profileImages;
    @JsonProperty("name")

    private String name;
    @JsonProperty("country")

    private int country;
    @JsonProperty("height")

    private int height;
    @JsonProperty("weight")

    private int weight;
    @JsonProperty("age")

    private int age;
    @JsonProperty("public_key")
    private String publicKey;
    @JsonProperty("login_date")
    private long loginDate;
    @JsonProperty("login_at")

    private LoginAt loginAt;
    @JsonProperty("is_favorite_follower")

    private int isFavoriteFollower;
    @JsonProperty("is_breeding_follower")

    private int isBreedingFollower;
    @JsonProperty("icon_type")

    private int iconType;
    @JsonProperty("howling_type")

    private int howlingType;
}
 class ProfileImage {
     @JsonProperty("image_number")

    private int imageNumber;
     @JsonProperty("mask")
    private int mask;
     @JsonProperty("file_name")

    private String fileName;
     @JsonProperty("updated_at")

    private long updatedAt;

}

class LoginAt {
    @JsonProperty("date")

    private String date;
    @JsonProperty("timezone_type")

    private int timezoneType;
    @JsonProperty("timezone")

    private String timezone;

}