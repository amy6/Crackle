package example.com.crackle.room;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import java.util.List;

import example.com.crackle.model.Movie;
import example.com.crackle.utils.AppExecutors;

public class MovieRepository {

    private MovieDao movieDao;
    private AppExecutors appExecutors;

    private LiveData<List<Movie>> movies;
    private boolean isFavorite;

    public MovieRepository(Application application) {
        //get reference to DAO to access database
        movieDao = MovieDatabase.getInstance(application).movieDao();
        movies = movieDao.getFavoritesMovies();

        //get reference to executor instance to handle background tasks
        appExecutors = AppExecutors.getExecutorInstance();
    }

    public LiveData<List<Movie>> getFavoriteMovies() {
        return movies;
    }

    public boolean isFavorite(int movieId) {
        appExecutors.getDiskIO().execute(() -> {
            isFavorite = movieDao.isFavorite(movieId);
        });
        return isFavorite;
    }

    public void updateMovieFavorite(int movieId, boolean isFavorite) {
        appExecutors.getDiskIO().execute(() -> {
            movieDao.updateMovieFavorite(movieId, isFavorite);
        });
    }

    public void addMovieToFavorites(Movie movie) {
        appExecutors.getDiskIO().execute(() -> {
            movieDao.addMovieToFavorites(movie);
        });
    }

    public void removeMovieFromFavorites(Movie movie) {
        appExecutors.getDiskIO().execute(() -> {
            movieDao.removeMovieFromFavorites(movie);
        });
    }

}
