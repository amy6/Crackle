package example.com.crackle;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieApiService {

    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static Retrofit retrofit;

    public static Retrofit getClient () {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BASE_URL)
                    .build();
        }
        return retrofit;
    }
}
