package example.com.crackle.listener

import example.com.crackle.model.CreditResults
import example.com.crackle.model.Movie
import example.com.crackle.model.MovieResults
import example.com.crackle.model.ReviewResults
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiClient {

    @GET("movie/popular")
    fun getPopularMovies(@Query("api_key") apiKey: String, @Query("page") page: Int): Call<MovieResults>

    @GET("movie/top_rated")
    fun getTopRatedMovies(@Query("api_key") apiKey: String, @Query("page") page: Int): Call<MovieResults>

    @GET("movie/{movie_id}/credits")
    fun getMovieCredits(@Path("movie_id") movieId: Int, @Query("api_key") apiKey: String): Call<CreditResults>

    @GET("movie/{movie_id}/reviews")
    fun getMovieReviews(@Path("movie_id") movieId: Int, @Query("api_key") apiKey: String): Call<ReviewResults>

    @GET("movie/{movie_id}")
    fun getMovieDetails(@Path("movie_id") movieId: Int, @Query("api_key") apiKey: String, @Query("append_to_response") append_to_response: String): Call<Movie>

    @GET("movie/{movie_id}")
    fun getMovieDetails(@Path("movie_id") movieId: Int, @Query("api_key") apiKey: String): Call<Movie>

}
