package example.com.crackle.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Movie implements Parcelable {

    @SerializedName("id")
    private int movieId;
    @SerializedName("poster_path")
    private String imageUrl;
    @SerializedName("backdrop_path")
    private String backdropImageUrl;
    @SerializedName("title")
    private String title;
    @SerializedName("overview")
    private String plot;
    @SerializedName("popularity")
    private double popularity;
    @SerializedName("vote_average")
    private double userRating;
    @SerializedName("release_date")
    private String releaseDate;
    @SerializedName("original_language")
    private String language;
    @SerializedName("genre_ids")
    private ArrayList<Integer> genres;
    @SerializedName("images")
    private ImageResults imageResults;
    @SerializedName("videos")
    private VideoResults videoResults;
    @SerializedName("releases")
    private CertificationResults certificationResults;


    public Movie(int movieId, String imageUrl, String backdropImageUrl, String title, String plot, double popularity, double userRating, String releaseDate, String language, ArrayList<Integer> genres, ImageResults imageResults, VideoResults videoResults, CertificationResults certificationResults) {
        this.movieId = movieId;
        this.imageUrl = imageUrl;
        this.backdropImageUrl = backdropImageUrl;
        this.title = title;
        this.plot = plot;
        this.popularity = popularity;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.language = language;
        this.genres = genres;
        this.imageResults = imageResults;
        this.videoResults = videoResults;
        this.certificationResults = certificationResults;
    }

    public Movie(Parcel source) {
        movieId = source.readInt();
        imageUrl = source.readString();
        backdropImageUrl = source.readString();
        title = source.readString();
        plot = source.readString();
        popularity = source.readDouble();
        userRating = source.readDouble();
        releaseDate = source.readString();
        language = source.readString();
        genres = (ArrayList<Integer>) source.readSerializable();
        imageResults = source.readParcelable(ImageResults.class.getClassLoader());
        videoResults = source.readParcelable(VideoResults.class.getClassLoader());
        certificationResults = source.readParcelable(CertificationResults.class.getClassLoader());
    }

    public int getMovieId() {
        return movieId;
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

    public double getPopularity() {
        return popularity;
    }

    public String getLanguage() {
        return language;
    }

    public ArrayList<Integer> getGenres() {
        return genres;
    }

    public ImageResults getImageResults() {
        return imageResults;
    }

    public VideoResults getVideoResults() {
        return videoResults;
    }

    public CertificationResults getCertificationResults() {
        return certificationResults;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(movieId);
        dest.writeString(imageUrl);
        dest.writeString(backdropImageUrl);
        dest.writeString(title);
        dest.writeString(plot);
        dest.writeDouble(popularity);
        dest.writeDouble(userRating);
        dest.writeString(releaseDate);
        dest.writeString(language);
        dest.writeSerializable(genres);
        dest.writeParcelable(imageResults, 0);
        dest.writeParcelable(videoResults, 0);
        dest.writeParcelable(certificationResults, 0);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
