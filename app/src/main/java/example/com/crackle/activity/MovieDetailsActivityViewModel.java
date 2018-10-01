package example.com.crackle.activity;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import example.com.crackle.model.Movie;
import example.com.crackle.database.MovieRepository;

public class MovieDetailsActivityViewModel extends AndroidViewModel {

    private MovieRepository movieRepository;

    public MovieDetailsActivityViewModel(@NonNull Application application) {
        super(application);
        movieRepository = new MovieRepository(application);
    }

    public boolean isFavorite(int movieId) {
        return movieRepository.isFavorite(movieId);
    }

    public void updateMovieFavorite(int movieId, boolean isFavorite) {
        movieRepository.updateMovieFavorite(movieId, isFavorite);
    }

    public void addMovieToFavorites(Movie movie) {
        movieRepository.addMovieToFavorites(movie);
    }

    public void removeMovieFromFavorites(Movie movie) {
        movieRepository.removeMovieFromFavorites(movie);
    }
}
