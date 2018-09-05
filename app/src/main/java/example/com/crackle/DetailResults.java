package example.com.crackle;

import com.google.gson.annotations.SerializedName;

class DetailResults {

    @SerializedName("runtime")
    private int duration;
    @SerializedName("tagline")
    private String tagLine;
    @SerializedName("homepage")
    private String homepage;

    public DetailResults(int duration, String tagLine, String homepage) {
        this.duration = duration;
        this.tagLine = tagLine;
        this.homepage = homepage;
    }

    public int getDuration() {
        return duration;
    }

    public String getTagLine() {
        return tagLine;
    }

    public String getHomepage() {
        return homepage;
    }
}
