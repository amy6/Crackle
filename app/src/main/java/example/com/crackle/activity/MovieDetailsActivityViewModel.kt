package example.com.crackle.activity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import example.com.crackle.database.MovieRepository
import example.com.crackle.model.Movie

class MovieDetailsActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val movieRepository: MovieRepository = MovieRepository(application)
    fun isFavorite(movieId: Int): Boolean {
        return movieRepository.isFavorite(movieId)
    }

    fun updateMovieFavorite(movieId: Int, isFavorite: Boolean) {
        movieRepository.updateMovieFavorite(movieId, isFavorite)
    }

    fun addMovieToFavorites(movie: Movie?) {
        movieRepository.addMovieToFavorites(movie!!)
    }

    fun removeMovieFromFavorites(movie: Movie?) {
        movieRepository.removeMovieFromFavorites(movie!!)
    }

}