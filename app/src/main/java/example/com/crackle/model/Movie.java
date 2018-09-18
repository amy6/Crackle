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
    @SerializedName("runtime")
    private int duration;
    @SerializedName("original_title")
    private String originalTitle;
    @SerializedName("homepage")
    private String homepage;
    @SerializedName("genre_ids")
    private ArrayList<Integer> genres;
    @SerializedName("images")
    private ImageResults imageResults;
    @SerializedName("videos")
    private VideoResults videoResults;
    @SerializedName("releases")
    private CertificationResults certificationResults;


    public Movie(int movieId, String imageUrl, String backdropImageUrl, String title, String plot, double popularity, double userRating, String releaseDate, String language, int duration, String originalTitle, String homepage, ArrayList<Integer> genres, ImageResults imageResults, VideoResults videoResults, CertificationResults certificationResults) {
        this.movieId = movieId;
        this.imageUrl = imageUrl;
        this.backdropImageUrl = backdropImageUrl;
        this.title = title;
        this.plot = plot;
        this.popularity = popularity;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.language = language;
        this.duration = duration;
        this.originalTitle = originalTitle;
        this.homepage = homepage;
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
        duration = source.readInt();
        originalTitle = source.readString();
        homepage = source.readString();
        genres = (ArrayList<Integer>) source.readSerializable();
        imageResults = source.readParcelable(ImageResults.class.getClassLoader());
        videoResults = source.readParcelable(VideoResults.class.getClassLoader());
        certificationResults = source.readParcelable(CertificationResults.class.getClassLoader());
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBackdropImageUrl() {
        return backdropImageUrl;
    }

    public void setBackdropImageUrl(String backdropImageUrl) {
        this.backdropImageUrl = backdropImageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public double getUserRating() {
        return userRating;
    }

    public void setUserRating(double userRating) {
        this.userRating = userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public ArrayList<Integer> getGenres() {
        return genres;
    }

    public void setGenres(ArrayList<Integer> genres) {
        this.genres = genres;
    }

    public ImageResults getImageResults() {
        return imageResults;
    }

    public void setImageResults(ImageResults imageResults) {
        this.imageResults = imageResults;
    }

    public VideoResults getVideoResults() {
        return videoResults;
    }

    public void setVideoResults(VideoResults videoResults) {
        this.videoResults = videoResults;
    }

    public CertificationResults getCertificationResults() {
        return certificationResults;
    }

    public void setCertificationResults(CertificationResults certificationResults) {
        this.certificationResults = certificationResults;
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
        dest.writeInt(duration);
        dest.writeString(originalTitle);
        dest.writeString(homepage);
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
