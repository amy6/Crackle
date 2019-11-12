package example.com.crackle.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import example.com.crackle.model.Movie;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movie")
    LiveData<List<Movie>> getFavoritesMovies();

    @Query("SELECT is_favorite FROM movie WHERE movie_id = :movieId")
    boolean isFavorite(int movieId);

    @Query("UPDATE movie SET is_favorite = :isFavorite WHERE movie_id = :movieId" )
    void updateMovieFavorite(int movieId, boolean isFavorite);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addMovieToFavorites(Movie movie);

    @Delete
    void removeMovieFromFavorites(Movie movie);

    @Query("SELECT * FROM movie WHERE movie_id = :movieId")
    Movie getMovie(int movieId);

}
