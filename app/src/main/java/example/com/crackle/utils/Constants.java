package example.com.crackle.utils;

import example.com.crackle.BuildConfig;
import example.com.crackle.activity.MainActivity;

public class Constants {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    public static final String MOVIE = "MOVIE";

    public static final String API_KEY = BuildConfig.TMDB_API_KEY;
    public static final String BASE_URL = "https://api.themoviedb.org/3/";
    public static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    public static final String IMAGE_SIZE = "w780/";
    public static final String IMAGE_URL_SIZE = IMAGE_BASE_URL+IMAGE_SIZE;
}
