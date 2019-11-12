package example.com.crackle.utils

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MovieApiService {

    //declare static Retrofit instance
    private var retrofit: Retrofit? = null

    //OkHttpClient to enable logging
    private val httpClient = OkHttpClient.Builder()
    private val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    /**
     * called to get a reference to Retrofit instance
     *
     * @return retrofit client to be used for API calls
     */
    val client: Retrofit
        get() {

            if (retrofit == null) {
                httpClient.addInterceptor(loggingInterceptor)
                retrofit = Retrofit.Builder()
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(httpClient.build())
                        .baseUrl(Constants.BASE_URL)
                        .build()
            }

            return retrofit!!
        }
}
