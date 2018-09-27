package example.com.crackle.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

@Entity(tableName = "movie")
public class Movie implements Parcelable {

    @PrimaryKey
    @ColumnInfo(name = "movie_id")
    @SerializedName("id")
    private int movieId;

    @ColumnInfo(name = "poster_path")
    @SerializedName("poster_path")
    private String imageUrl;

    @ColumnInfo(name = "backdrop_path")
    @SerializedName("backdrop_path")
    private String backdropImageUrl;

    @ColumnInfo(name = "title")
    @SerializedName("title")
    private String title;

    @ColumnInfo(name = "overview")
    @SerializedName("overview")
    private String plot;

    @ColumnInfo(name = "popularity")
    @SerializedName("popularity")
    private double popularity;

    @ColumnInfo(name = "vote_average")
    @SerializedName("vote_average")
    private double userRating;

    @ColumnInfo(name = "release_date")
    @SerializedName("release_date")
    private String releaseDate;

    @ColumnInfo(name = "original_language")
    @SerializedName("original_language")
    private String language;

    @ColumnInfo(name = "runtime")
    @SerializedName("runtime")
    private int duration;

    @ColumnInfo(name = "original_title")
    @SerializedName("original_title")
    private String originalTitle;

    @ColumnInfo(name = "homepage")
    @SerializedName("homepage")
    private String homepage;

    @Ignore
    @SerializedName("genre_ids")
    private ArrayList<Integer> genres;

    @Ignore
    @SerializedName("images")
    private ImageResults imageResults;

    @Ignore
    @SerializedName("videos")
    private VideoResults videoResults;

    @Ignore
    @SerializedName("releases")
    private CertificationResults certificationResults;

    @ColumnInfo(name = "is_favorite")
    private boolean isFavorite;

    @Ignore
    public Movie() {
    }

    //this constructor will be used by Room
    public Movie(int movieId, String imageUrl, String backdropImageUrl, String title, String plot, double popularity, double userRating, String releaseDate, String language, int duration, String originalTitle, String homepage, boolean isFavorite) {
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
        this.isFavorite = isFavorite;
    }

    @Ignore
    public Movie(int movieId, String imageUrl, String backdropImageUrl, String title, String plot, double popularity, double userRating, String releaseDate, String language, int duration, String originalTitle, String homepage, ArrayList<Integer> genres, ImageResults imageResults, VideoResults videoResults, CertificationResults certificationResults, boolean isFavorite) {
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
        this.isFavorite = isFavorite;
    }


    @Ignore
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
        isFavorite = source.readByte() != 0;
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

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
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
        dest.writeByte((byte) (isFavorite ? 1 : 0));
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
