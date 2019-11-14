package example.com.crackle.utils

import example.com.crackle.listener.MovieApiClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MovieApiService {

    private val loggingInterceptor = HttpLoggingInterceptor()
            .apply { level = HttpLoggingInterceptor.Level.BODY }
    private val httpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()


    private val retrofit: Retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .baseUrl(Constants.BASE_URL)
            .build()

    val client: MovieApiClient = retrofit.create(MovieApiClient::class.java)
}
