package example.com.crackle.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ImageResults implements Parcelable {

    @SerializedName("backdrops")
    private
    List<Image> backdrops;

    public ImageResults(List<Image> backdrops) {
        this.backdrops = backdrops;
    }

    public ImageResults(Parcel parcel) {
        parcel.readTypedList(backdrops, Image.CREATOR);
    }

    public List<Image> getBackdrops() {
        return backdrops;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(backdrops);
    }

    public static final Creator<ImageResults> CREATOR = new Creator<ImageResults>() {
        @Override
        public ImageResults createFromParcel(Parcel parcel) {
            return new ImageResults(parcel);
        }

        @Override
        public ImageResults[] newArray(int i) {
            return new ImageResults[i];
        }
    };
}
