package tw.thootau.thatchatapplication.Structs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

public class MessageEntry {
    @JsonProperty("id")
    public long id;

    @JsonProperty("date")
    public long date;

    @JsonProperty("align")
    public String align;

    @JsonProperty("public_key")
    public String publicKey;

    @JsonProperty("profile_image")
    public long profileImage;

    @JsonProperty("thumbnail")
    public long thumbnail;

    @JsonProperty("profile_images")
    public List<ProfileImage> profileImages;

    @JsonProperty("deletable")
    public long deletable;

    @JsonProperty("del")
    public long del;

    @JsonProperty("image")
    public String image;

    @JsonProperty("mask")
    public long mask;

    @JsonProperty("width")
    public String width;

    @JsonProperty("height")
    public String height;

    @JsonProperty("message")
    public String message;

    public static class ProfileImage {
        @JsonProperty("image_number")
        public long imageNumber;

        @JsonProperty("mask")
        public long mask;

        @JsonProperty("file_name")
        public String fileName;

        @JsonProperty("updated_at")
        public long updatedAt;

        // ゲッターとセッターを省略
    }
}