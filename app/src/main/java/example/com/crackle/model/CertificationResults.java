package example.com.crackle.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CertificationResults implements Parcelable {

    @SerializedName("countries")
    private
    List<Certification> certificationList;

    public CertificationResults(List<Certification> certificationList) {
        this.certificationList = certificationList;
    }

    public CertificationResults(Parcel parcel) {
        parcel.readTypedList(certificationList, Certification.CREATOR);
    }

    public List<Certification> getCertificationList() {
        return certificationList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(certificationList);
    }

    public static final Creator<CertificationResults> CREATOR = new Creator<CertificationResults>() {
        @Override
        public CertificationResults createFromParcel(Parcel parcel) {
            return new CertificationResults(parcel);
        }

        @Override
        public CertificationResults[] newArray(int i) {
            return new CertificationResults[i];
        }
    };
}
