package example.com.crackle.activity;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import example.com.crackle.model.Movie;
import example.com.crackle.room.MovieDao;
import example.com.crackle.room.MovieDatabase;

public class MovieDetailsActivityViewModel extends AndroidViewModel {

    private final MovieDao movieDao;

    public MovieDetailsActivityViewModel(@NonNull Application application) {
        super(application);
        movieDao = MovieDatabase.getInstance(application).movieDao();
    }

    public MovieDao movieDao() {
        return movieDao;
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
