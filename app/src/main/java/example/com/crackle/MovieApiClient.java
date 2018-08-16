package example.com.crackle;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MovieApiClient {

    @GET("movie/popular")
    Call<MovieResults> getPopularMovies (@Query("api_key") String apiKey, @Query("page") int page);

    @GET("movie/top_rated")
    Call<MovieResults> getTopRatedMovies (@Query("api_key") String apiKey, @Query("page") int page);
}
