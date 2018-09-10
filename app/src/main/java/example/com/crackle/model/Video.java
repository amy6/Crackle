package example.com.crackle.model;

import com.google.gson.annotations.SerializedName;

public class Video {

    @SerializedName("id")
    private long id;
    @SerializedName("site")
    private String site;

    public Video(long id, String site) {
        this.id = id;
        this.site = site;
    }

    public long getId() {
        return id;
    }

    public String getSite() {
        return site;
    }
}
