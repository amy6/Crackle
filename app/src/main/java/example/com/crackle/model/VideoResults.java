package example.com.crackle.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VideoResults implements Parcelable {

    @SerializedName("results")
    private List<Video> videos;

    public VideoResults(List<Video> videos) {
        this.videos = videos;
    }

    public VideoResults(Parcel parcel) {
        parcel.readTypedList(videos, Video.CREATOR);
    }

    public List<Video> getVideos() {
        return videos;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(videos);
    }

    public static final Creator<VideoResults> CREATOR = new Creator<VideoResults>() {
        @Override
        public VideoResults createFromParcel(Parcel parcel) {
            return new VideoResults(parcel);
        }

        @Override
        public VideoResults[] newArray(int i) {
            return new VideoResults[i];
        }
    };
}
