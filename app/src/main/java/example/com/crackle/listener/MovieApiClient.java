package example.com.crackle.listener;

import example.com.crackle.model.ImageResults;
import example.com.crackle.model.Movie;
import example.com.crackle.model.MovieResults;
import example.com.crackle.model.ReviewResults;
import example.com.crackle.model.CreditResults;
import example.com.crackle.model.DetailResults;
import example.com.crackle.model.VideoResults;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieApiClient {

    @GET("movie/popular")
    Call<MovieResults> getPopularMovies (@Query("api_key") String apiKey, @Query("page") int page);

    @GET("movie/top_rated")
    Call<MovieResults> getTopRatedMovies (@Query("api_key") String apiKey, @Query("page") int page);

    @GET("movie/{movie_id}/credits")
    Call<CreditResults> getMovieCredits (@Path("movie_id") int movieId, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/reviews")
    Call<ReviewResults> getMovieReviews (@Path("movie_id") int movieId, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}")
    Call<Movie> getMovieDetails (@Path("movie_id") int movieId, @Query("api_key") String apiKey, @Query("append_to_response") String append_to_response);

    @GET("movie/{movie_id}")
    Call<Movie> getMovieDetails (@Path("movie_id") int movieId, @Query("api_key") String apiKey);

}
