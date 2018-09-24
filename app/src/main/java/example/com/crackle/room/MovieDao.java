package example.com.crackle.room;

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
    List<Movie> getFavoritesMovies();

    @Query("SELECT * FROM movie WHERE movie_id = :movieId")
    Movie getMovie(String movieId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addMovieToFavorites(Movie movie);

    @Delete
    void removeMovieFromFavorites(Movie movie);
}
