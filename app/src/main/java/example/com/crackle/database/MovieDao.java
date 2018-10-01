package example.com.crackle.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

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
}
