package example.com.crackle;

import com.google.gson.annotations.SerializedName;

class DetailResults {

    @SerializedName("runtime")
    private int duration;
    @SerializedName("tagline")
    private String tagLine;

    public DetailResults(int duration, String tagLine) {
        this.duration = duration;
        this.tagLine = tagLine;
    }

    public int getDuration() {
        return duration;
    }

    public String getTagLine() {
        return tagLine;
    }
}
