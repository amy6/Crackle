package example.com.crackle.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ImageResults {

    @SerializedName("backdrops")
    List<Image> backdrops;

    public ImageResults(List<Image> backdrops) {
        this.backdrops = backdrops;
    }

    public List<Image> getBackdrops() {
        return backdrops;
    }
}
