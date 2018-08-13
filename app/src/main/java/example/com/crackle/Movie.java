package example.com.crackle;

import com.google.gson.annotations.SerializedName;

public class Movie {

    @SerializedName("poster_path")
    private String imageUrl;
    @SerializedName("backdrop_path")
    private String backdropImageUrl;
    @SerializedName("title")
    private String title;
    @SerializedName("overview")
    private String plot;
    @SerializedName("vote_average")
    private double userRating;
    @SerializedName("release_date")
    private String releaseDate;

    public Movie(String imageUrl, String backdropImageUrl, String title, String plot, double userRating, String releaseDate) {
        this.imageUrl = imageUrl;
        this.backdropImageUrl = backdropImageUrl;
        this.title = title;
        this.plot = plot;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getBackdropImageUrl() {
        return backdropImageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getPlot() {
        return plot;
    }

    public double getUserRating() {
        return userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }
}
