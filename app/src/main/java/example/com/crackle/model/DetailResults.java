package example.com.crackle.model;

import com.google.gson.annotations.SerializedName;

public class DetailResults {

    @SerializedName("runtime")
    private int duration;
    @SerializedName("original_title")
    private String originalTitle;
    @SerializedName("homepage")
    private String homepage;

    public DetailResults(int duration, String originalTitle, String homepage) {
        this.duration = duration;
        this.originalTitle = originalTitle;
        this.homepage = homepage;
    }

    public int getDuration() {
        return duration;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getHomepage() {
        return homepage;
    }
}
