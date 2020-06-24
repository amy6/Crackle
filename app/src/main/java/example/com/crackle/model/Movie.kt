package example.com.crackle.model

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "movie")
class Movie : Parcelable {
    @PrimaryKey
    @ColumnInfo(name = "movie_id")
    @SerializedName("id")
    var movieId = 0
    @ColumnInfo(name = "poster_path")
    @SerializedName("poster_path")
    var imageUrl: String? = null
    @ColumnInfo(name = "backdrop_path")
    @SerializedName("backdrop_path")
    var backdropImageUrl: String? = null
    @ColumnInfo(name = "title")
    @SerializedName("title")
    var title: String? = null
    @ColumnInfo(name = "overview")
    @SerializedName("overview")
    var plot: String? = null
    @ColumnInfo(name = "popularity")
    @SerializedName("popularity")
    var popularity = 0.0
    @ColumnInfo(name = "vote_average")
    @SerializedName("vote_average")
    var userRating = 0.0
    @ColumnInfo(name = "release_date")
    @SerializedName("release_date")
    var releaseDate: String? = null
    @ColumnInfo(name = "original_language")
    @SerializedName("original_language")
    var language: String? = null
    @ColumnInfo(name = "runtime")
    @SerializedName("runtime")
    var duration = 0
    @ColumnInfo(name = "original_title")
    @SerializedName("original_title")
    var originalTitle: String? = null
    @ColumnInfo(name = "homepage")
    @SerializedName("homepage")
    var homepage: String? = null
    @SerializedName("genre_ids")
    var genres: ArrayList<Int>? = null
    @Ignore
    @SerializedName("images")
    var imageResults: ImageResults? = null
    @Ignore
    @SerializedName("videos")
    var videoResults: VideoResults? = null
    @Ignore
    @SerializedName("releases")
    var certificationResults: CertificationResults? = null
    @ColumnInfo(name = "is_favorite")
    var isFavorite = false

    @Ignore
    constructor()

    //this constructor will be used by Room
    constructor(movieId: Int, imageUrl: String?, backdropImageUrl: String?, title: String?, plot: String?, popularity: Double, userRating: Double, releaseDate: String?, language: String?, duration: Int, originalTitle: String?, homepage: String?, isFavorite: Boolean) {
        this.movieId = movieId
        this.imageUrl = imageUrl
        this.backdropImageUrl = backdropImageUrl
        this.title = title
        this.plot = plot
        this.popularity = popularity
        this.userRating = userRating
        this.releaseDate = releaseDate
        this.language = language
        this.duration = duration
        this.originalTitle = originalTitle
        this.homepage = homepage
        this.isFavorite = isFavorite
    }

    @Ignore
    constructor(movieId: Int, imageUrl: String?, backdropImageUrl: String?, title: String?, plot: String?, popularity: Double, userRating: Double, releaseDate: String?, language: String?, duration: Int, originalTitle: String?, homepage: String?, genres: ArrayList<Int>?, imageResults: ImageResults?, videoResults: VideoResults?, certificationResults: CertificationResults?, isFavorite: Boolean) {
        this.movieId = movieId
        this.imageUrl = imageUrl
        this.backdropImageUrl = backdropImageUrl
        this.title = title
        this.plot = plot
        this.popularity = popularity
        this.userRating = userRating
        this.releaseDate = releaseDate
        this.language = language
        this.duration = duration
        this.originalTitle = originalTitle
        this.homepage = homepage
        this.genres = genres
        this.imageResults = imageResults
        this.videoResults = videoResults
        this.certificationResults = certificationResults
        this.isFavorite = isFavorite
    }

    @Ignore
    constructor(source: Parcel) {
        movieId = source.readInt()
        imageUrl = source.readString()
        backdropImageUrl = source.readString()
        title = source.readString()
        plot = source.readString()
        popularity = source.readDouble()
        userRating = source.readDouble()
        releaseDate = source.readString()
        language = source.readString()
        duration = source.readInt()
        originalTitle = source.readString()
        homepage = source.readString()
        genres = source.readSerializable() as ArrayList<Int>?
        imageResults = source.readParcelable(ImageResults::class.java.classLoader)
        videoResults = source.readParcelable(VideoResults::class.java.classLoader)
        certificationResults = source.readParcelable(CertificationResults::class.java.classLoader)
        isFavorite = source.readByte().toInt() != 0
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(movieId)
        dest.writeString(imageUrl)
        dest.writeString(backdropImageUrl)
        dest.writeString(title)
        dest.writeString(plot)
        dest.writeDouble(popularity)
        dest.writeDouble(userRating)
        dest.writeString(releaseDate)
        dest.writeString(language)
        dest.writeInt(duration)
        dest.writeString(originalTitle)
        dest.writeString(homepage)
        dest.writeSerializable(genres)
        dest.writeParcelable(imageResults, 0)
        dest.writeParcelable(videoResults, 0)
        dest.writeParcelable(certificationResults, 0)
        dest.writeByte((if (isFavorite) 1 else 0).toByte())
    }

    companion object {
        @JvmField val CREATOR: Creator<Movie?> =  object : Creator<Movie?> {
            override fun createFromParcel(source: Parcel): Movie? {
                return Movie(source)
            }

            override fun newArray(size: Int): Array<Movie?> {
                return arrayOfNulls(size)
            }
        }
    }
}