package example.com.crackle.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Certification implements Parcelable {

    @SerializedName("certification")
    private String certification;
    @SerializedName("iso_3166_1")
    private String iso;

    public Certification(String certification, String iso) {
        this.certification = certification;
        this.iso = iso;
    }

    public Certification(Parcel parcel) {
        certification = parcel.readString();
        iso = parcel.readString();
    }

    public String getCertification() {
        return certification;
    }

    public String getIso() {
        return iso;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(certification);
        parcel.writeString(iso);
    }

    public static final Creator<Certification> CREATOR = new Creator<Certification>() {
        @Override
        public Certification createFromParcel(Parcel parcel) {
            return new Certification(parcel);
        }

        @Override
        public Certification[] newArray(int i) {
            return new Certification[i];
        }
    };
}
