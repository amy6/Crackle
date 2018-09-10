package example.com.crackle.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class VideoResults {

    @SerializedName("results")
    private List<Video> videos;
}
