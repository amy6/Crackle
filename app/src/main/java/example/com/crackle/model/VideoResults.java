package example.com.crackle.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VideoResults {

    @SerializedName("results")
    private List<Video> videos;

    public VideoResults(List<Video> videos) {
        this.videos = videos;
    }

    public List<Video> getVideos() {
        return videos;
    }
}
