package example.com.crackle.utils;

import example.com.crackle.BuildConfig;
import example.com.crackle.activity.MainActivity;

public class Constants {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    public static final String MOVIE = "MOVIE";

    //constants associated with API calls
    public static final String API_KEY = BuildConfig.TMDB_API_KEY;
    public static final String BASE_URL = "https://api.themoviedb.org/3/";
    public static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    public static final String IMAGE_SIZE = "w780/";
    public static final String IMAGE_URL_SIZE = IMAGE_BASE_URL + IMAGE_SIZE;

    //constant to save and restore default sort order selected by the user on activity state changes
    public static final String DEFAULT_OPTION_CHECKED = "DEFAULT_OPTION_CHECKED";

    //define item type to load on pagination - grid item or progress bar
    public static final int ITEM = 0;
    public static final int PROGRESS = 1;

    //view pager tab titles
    public static final String TAB_INFO = "INFO";
    public static final String TAB_CAST = "CAST";
    public static final String TAB_REVIEWS = "REVIEWS";

    //set default max lines for review content
    public static final int MAX_LINES = 3;

    //define image types to define Glide set up options - default placeholder and error images
    public static final String POSTER_IMG = "poster";
    public static final String BACKDROP_IMG = "backdrop";
    public static final String CAST_IMG = "cast";

    //define item view cache size for RecyclerView
    public static final int ITEM_VIEW_CACHE_SIZE = 10;

    //layout types for RecyclerView
    public static final int GRID_LAYOUT = 0;
    public static final int LINEAR_LAYOUT = 1;
}
