package example.com.crackle.database

import androidx.lifecycle.LiveData
import androidx.room.*
import example.com.crackle.model.Movie

@Dao
interface MovieDao {

    @get:Query("SELECT * FROM movie")
    val favoritesMovies: LiveData<List<Movie>>

    @Query("SELECT is_favorite FROM movie WHERE movie_id = :movieId")
    fun isFavorite(movieId: Int): Boolean

    @Query("UPDATE movie SET is_favorite = :isFavorite WHERE movie_id = :movieId")
    fun updateMovieFavorite(movieId: Int, isFavorite: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addMovieToFavorites(movie: Movie)

    @Delete
    fun removeMovieFromFavorites(movie: Movie)

    @Query("SELECT * FROM movie WHERE movie_id = :movieId")
    fun getMovie(movieId: Int): Movie

}
