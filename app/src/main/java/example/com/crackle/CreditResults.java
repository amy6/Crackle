package example.com.crackle;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CreditResults {

    @SerializedName("id")
    private int id;
    @SerializedName("cast")
    private List<Cast> castList;
    @SerializedName("crew")
    private List<Crew> crewList;

    public CreditResults(int id, List<Cast> castList, List<Crew> crewList) {
        this.id = id;
        this.castList = castList;
        this.crewList = crewList;
    }

    public int getId() {
        return id;
    }

    public List<Cast> getCastList() {
        return castList;
    }

    public List<Crew> getCrewList() {
        return crewList;
    }
}
