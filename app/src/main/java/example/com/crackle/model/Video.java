package example.com.crackle.model;

import com.google.gson.annotations.SerializedName;

public class Video {

    @SerializedName("key")
    private String key;
    @SerializedName("site")
    private String site;
    @SerializedName("name")
    private String title;

    public Video(String key, String site, String title) {
        this.key = key;
        this.site = site;
        this.title = title;
    }

    public String getKey() {
        return key;
    }

    public String getSite() {
        return site;
    }

    public String getTitle() {
        return title;
    }
}
