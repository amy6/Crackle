package example.com.crackle.model;

import com.google.gson.annotations.SerializedName;

public class Video {

    @SerializedName("key")
    private String key;
    @SerializedName("site")
    private String site;

    public Video(String key, String site) {
        this.key = key;
        this.site = site;
    }

    public String getKey() {
        return key;
    }

    public String getSite() {
        return site;
    }
}
