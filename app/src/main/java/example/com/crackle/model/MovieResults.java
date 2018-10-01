package example.com.crackle.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieResults {

    @SerializedName("results")
    private List<Movie> movies;

    public MovieResults(List<Movie> movies) {
        this.movies = movies;
    }

    public List<Movie> getMovies() {
        return movies;
    }
}
