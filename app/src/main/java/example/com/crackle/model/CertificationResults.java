package example.com.crackle.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CertificationResults {

    @SerializedName("countries")
    private
    List<Certification> certificationList;

    public CertificationResults(List<Certification> certificationList) {
        this.certificationList = certificationList;
    }

    public List<Certification> getCertificationList() {
        return certificationList;
    }
}
