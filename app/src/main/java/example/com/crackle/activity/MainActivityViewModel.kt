package example.com.crackle.activity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import example.com.crackle.database.MovieRepository
import example.com.crackle.model.Movie

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    val favoriteMovies: LiveData<List<Movie>>

    init {
        val movieRepository = MovieRepository(application)
        favoriteMovies = movieRepository.favoriteMovies
    }
}