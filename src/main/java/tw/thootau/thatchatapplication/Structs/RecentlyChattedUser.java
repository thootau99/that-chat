package tw.thootau.thatchatapplication.Structs;

import lombok.Getter;

import java.util.List;

@Getter
public class RecentlyChattedUser {
    private int messageCount;
    private long date;
    private int profileImage;
    private int thumbnail;
    private List<ProfileImage> profileImages;
    private String name;
    private int country;
    private int height;
    private int weight;
    private int age;
    private String publicKey;
    private long loginDate;
    private LoginAt loginAt;
    private int isFavoriteFollower;
    private int isBreedingFollower;
    private int iconType;
    private int howlingType;
}
 class ProfileImage {
    private int imageNumber;
    private int mask;
    private String fileName;
    private long updatedAt;

}

class LoginAt {
    private String date;
    private int timezoneType;
    private String timezone;

}