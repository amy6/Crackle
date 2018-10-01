package example.com.crackle.activity;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import example.com.crackle.model.Movie;
import example.com.crackle.database.MovieRepository;

public class MainActivityViewModel extends AndroidViewModel {

    private LiveData<List<Movie>> movies;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        MovieRepository movieRepository = new MovieRepository(application);
        movies = movieRepository.getFavoriteMovies();
    }


    public LiveData<List<Movie>> getFavoriteMovies() {
        return movies;
    }
}
