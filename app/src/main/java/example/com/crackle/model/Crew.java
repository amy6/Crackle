package example.com.crackle.model;

import com.google.gson.annotations.SerializedName;

public class Crew {

    @SerializedName("name")
    private String name;
    @SerializedName("job")
    private String job;

    public Crew(String name, String job) {
        this.name = name;
        this.job = job;
    }

    public String getName() {
        return name;
    }

    public String getJob() {
        return job;
    }
}
