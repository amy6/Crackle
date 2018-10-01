package example.com.crackle.room;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import java.util.List;

import example.com.crackle.model.Movie;

public class MovieRepository {

    private MovieDao movieDao;
    private LiveData<List<Movie>> movies;

    public MovieRepository(Application application) {
        movieDao = MovieDatabase.getInstance(application).movieDao();
        movies = movieDao.getFavoritesMovies();
    }

    public LiveData<List<Movie>> getFavoriteMovies() {
        return movies;
    }

    public boolean isFavorite(int movieId) {
        return movieDao.isFavorite(movieId);
    }

    public void updateMovieFavorite(int movieId, boolean isFavorite) {
        movieDao.updateMovieFavorite(movieId, isFavorite);
    }

    public void addMovieToFavorites(Movie movie) {
        movieDao.addMovieToFavorites(movie);
    }

    public void removeMovieFromFavorites(Movie movie) {
        movieDao.removeMovieFromFavorites(movie);
    }

}
