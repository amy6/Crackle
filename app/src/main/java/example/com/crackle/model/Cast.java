package example.com.crackle.model;

import com.google.gson.annotations.SerializedName;

public class Cast {

    @SerializedName("name")
    private String name;
    @SerializedName("profile_path")
    private String profileUrl;

    public Cast(String name, String profileUrl) {
        this.name = name;
        this.profileUrl = profileUrl;
    }

    public String getName() {
        return name;
    }

    public String getProfileUrl() {
        return profileUrl;
    }
}
