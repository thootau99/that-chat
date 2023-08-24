package tw.thootau.thatchatapplication.Structs;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GetChattedUserListResponse {
    public int result;
    public List<ProfileItem> data;
    public int messageCount;

    // Getters and setters

    public class ProfileItem {
        @JsonProperty("message_count")
        public int messageCount;
        public long date;
        @JsonProperty("profile_image")
        public int profileImage;
        public int thumbnail;
        @JsonProperty("profile_images")
        public List<ProfileImage> profileImages;
        public String name;
        public int country;
        public int height;
        public int weight;
        public int age;
        @JsonProperty("public_key")
        public String publicKey;
        @JsonProperty("login_date")
        public long loginDate;
        @JsonProperty("login_at")
        public LoginAt loginAt;
        @JsonProperty("is_favorite_follower")
        public int isFavoriteFollower;
        @JsonProperty("is_breeding_follower")
        public int isBreedingFollower;
        @JsonProperty("icon_type")
        public int iconType;
        @JsonProperty("howling_type")
        public int howlingType;

        // Getters and setters

        public class ProfileImage {
            @JsonProperty("image_number")
            public int imageNumber;
            public int mask;
            @JsonProperty("file_name")
            public String fileName;
            @JsonProperty("updated_at")
            public long updatedAt;

            // Getters and setters
        }

        public class LoginAt {
            public String date;
            @JsonProperty("timezone_type")
            public int timezoneType;
            public String timezone;

        }
    }
}
