package example.com.crackle.utils

import example.com.crackle.BuildConfig
import example.com.crackle.activity.MainActivity

object Constants {

    @JvmField
    val LOG_TAG = MainActivity::class.java.simpleName

    const val MOVIE = "MOVIE"

    //constants associated with API calls
    const val API_KEY = BuildConfig.TMDB_API_KEY
    const val BASE_URL = "https://api.themoviedb.org/3/"
    private const val IMAGE_BASE_URL = "http://image.tmdb.org/t/p/"
    private const val IMAGE_SIZE = "w780/"
    const val IMAGE_URL_SIZE = IMAGE_BASE_URL + IMAGE_SIZE

    //constant to save and restore default sort order selected by the user on activity state changes
    const val MOST_POPULAR_OPTION_CHECKED = "MOST_POPULAR_OPTION_CHECKED"
    const val TOP_RATED_OPTION_CHECKED = "TOP_RATED_OPTION_CHECKED"

    //save page numbers to restore paginated data
    const val MOST_POPULAR_START_PAGE = "MOST_POPULAR_START_PAGE"
    const val TOP_RATED_START_PAGE = "TOP_RATED_START_PAGE"

    //define item type to load on pagination - grid item or progress bar
    const val ITEM = 0
    const val PROGRESS = 1

    //view pager tab titles
    const val TAB_INFO = "INFO"
    const val TAB_CAST = "CAST"
    const val TAB_REVIEWS = "REVIEWS"

    //set default max lines for review content
    const val MAX_LINES = 3

    //define image types to define Glide set up options - default placeholder and error images
    const val POSTER_IMG = "poster"
    const val BACKDROP_IMG = "backdrop"
    const val CAST_IMG = "cast"

    //define item view cache size for RecyclerView
    const val ITEM_VIEW_CACHE_SIZE = 20

    //layout types for RecyclerView
    const val GRID_LAYOUT = 0
    const val LINEAR_LAYOUT_VERTICAL = 1
    const val LINEAR_LAYOUT_HORIZONTAL = 2

    //key to save data set
    const val MOVIES_LIST = "MOVIES_LIST"

    //define key for saving RecyclerView Layout Manager state
    const val RECYCLER_VIEW_LAYOUT_MANAGER_STATE = "LAYOUT_MANAGER_STATE"

    //constants for handling youtube intent to play movie trailers
    const val YOUTUBE_VID_BASE_URI = "https://youtube.com/watch?v="
    const val YOUTUBE_IMG_BASE_URI = "http://img.youtube.com/vi/"
    const val YOUTUBE_IMG_EXTENSION = "/mqdefault.jpg"
    const val SITE_FILTER_YOUTUBE = "youtube"

    //constants for tmdb profile url and google play store link for the movie
    const val TMDB_MOVIE_BASE_URI = "https://www.themoviedb.org/movie/"
    const val PLAYSTORE_BASE_URI = "market://search?q="
    const val PLAYSTORE_QUERY_PARAMETER_CATEGORY = "c"
    const val PLAYSTORE_QUERY_VALUE_CATEGORY = "movies"

    //append_to_response query parameter value
    const val APPEND_TO_RESPONSE_VALUE = "images,videos,releases"
}
