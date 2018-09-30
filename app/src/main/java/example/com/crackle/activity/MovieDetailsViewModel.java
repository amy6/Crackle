package example.com.crackle.activity;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import java.util.List;

import example.com.crackle.model.Movie;
import example.com.crackle.room.MovieDao;
import example.com.crackle.room.MovieDatabase;

import static example.com.crackle.utils.Constants.LOG_TAG;

public class MovieDetailsViewModel extends AndroidViewModel {

    private LiveData<List<Movie>> movies;

    public MovieDetailsViewModel(Application application) {
        super(application);
        Log.d(LOG_TAG, "Initializing Movie Mutable LiveData object inside ViewModel");
        MovieDao movieDao = MovieDatabase.getInstance(application).movieDao();
        movies = movieDao.getFavoritesMovies();
    }

    public LiveData<List<Movie>> getFavoriteMovies() {
        return movies;
    }
}
