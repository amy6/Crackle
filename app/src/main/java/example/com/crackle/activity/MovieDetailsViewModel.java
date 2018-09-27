package example.com.crackle.activity;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import example.com.crackle.model.Movie;

import static example.com.crackle.utils.Constants.LOG_TAG;

public class MovieDetailsViewModel extends ViewModel {

    private MutableLiveData<Movie> movie;

    public MovieDetailsViewModel() {
        Log.d(LOG_TAG, "Initializing Movie Mutable LiveData object inside ViewModel");
        movie = new MutableLiveData<>();
    }

    public MutableLiveData<Movie> getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie.postValue(movie);
    }
}
