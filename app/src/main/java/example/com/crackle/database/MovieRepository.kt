package example.com.crackle.database

import android.app.Application
import androidx.lifecycle.LiveData

import example.com.crackle.model.Movie
import example.com.crackle.utils.AppExecutors

class MovieRepository(application: Application) {

    private val movieDao: MovieDao = MovieDatabase.getInstance(application)!!.movieDao()
    private val appExecutors: AppExecutors

    val favoriteMovies: LiveData<List<Movie>>

    init {
        //get reference to DAO to access database
        favoriteMovies = movieDao.favoritesMovies

        //get reference to executor instance to handle background tasks
        appExecutors = AppExecutors.getExecutorInstance()
    }

    fun isFavorite(movieId: Int): Boolean {
        return movieDao.isFavorite(movieId)
    }

    fun updateMovieFavorite(movieId: Int, isFavorite: Boolean) {
        appExecutors.diskIO.execute { movieDao.updateMovieFavorite(movieId, isFavorite) }
    }

    fun addMovieToFavorites(movie: Movie) {
        appExecutors.diskIO.execute { movieDao.addMovieToFavorites(movie) }
    }

    fun removeMovieFromFavorites(movie: Movie) {
        appExecutors.diskIO.execute { movieDao.removeMovieFromFavorites(movie) }
    }

}
