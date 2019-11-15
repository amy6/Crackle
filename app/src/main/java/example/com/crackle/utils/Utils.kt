package example.com.crackle.utils

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.SparseArray
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import example.com.crackle.R
import example.com.crackle.utils.Constants.BACKDROP_IMG
import example.com.crackle.utils.Constants.POSTER_IMG
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*

object Utils {

    /**
     * verifies if the device is connected to the internet
     *
     * @param context reference to context to access application resources
     * @return a boolean flag indicating whether device is connected to the internet
     */
    fun checkInternetConnection(context: Context): Boolean {
        val connectivityManager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        var networkInfo: NetworkInfo? = null
        networkInfo = connectivityManager.activeNetworkInfo

        return networkInfo == null || !networkInfo.isConnectedOrConnecting
    }

    /**
     * format run time of a movie to a string in the form of h:m
     *
     * @param context    reference to context to access application resources
     * @param timeInMins runtime of the movie in minutes
     * @return duration in the format of h:m
     */
    fun formatDuration(context: Context, timeInMins: Int): String {

        if (timeInMins == 0) {
            return context.getString(R.string.not_available)
        }

        var duration: String
        val hours = timeInMins / 60
        val minutes = timeInMins % 60
        duration = String.format(context.getString(R.string.time_format), hours, minutes)
        if (hours <= 0 && minutes <= 0) {
            duration = "N/A"
        }

        //display only mins if hours = 0
        if (minutes <= 0 && hours > 0) {
            duration = duration.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
        }
        //display only hours if mins = 0
        if (hours <= 0 && minutes > 0) {
            duration = duration.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        }

        //handle plural condition for hours
        if (hours == 1) {
            duration = duration.replace("hrs", "hr")
        }
        //handle plural condition for mins
        if (minutes == 1) {
            duration = duration.replace("mins", "min")
        }
        return duration.replace(":", "")
    }

    /**
     * parse local json file containing list of genre code and names
     *
     * @param context reference to context to access application resources
     * @return sparse array of genre code and corresponding names as key-value pairs
     */
    fun fetchAllGenres(context: Context): SparseArray<String>? {
        var map: SparseArray<String>? = null
        val json = loadJsonFromAsset(context, "genre.json")

        //parse json to get genre code and name
        try {
            val jsonObject = JSONObject(json!!)
            val genres = jsonObject.getJSONArray("genres")
            map = SparseArray()
            for (i in 0 until genres.length()) {
                val genre = genres.getJSONObject(i)
                val id = genre.getInt("id")
                val name = genre.getString("name")

                map.put(id, name)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return map
    }


    /**
     * parse local json file containing list of language code and names
     *
     * @param context reference to context to access application resources
     * @return map of language code and corresponding names as key-value pairs
     */
    fun fetchAllLanguages(context: Context): HashMap<String, String>? {
        var map: HashMap<String, String>? = null
        val json = loadJsonFromAsset(context, "language.json")

        //parse json to get language code and name
        try {
            val jsonArray = JSONArray(json)
            map = HashMap()
            for (i in 0 until jsonArray.length()) {
                val language = jsonArray.getJSONObject(i)
                val languageCode = language.getString("iso_639_1")
                val languageName = language.getString("english_name")

                map[languageCode] = languageName
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return map
    }

    /**
     * load and read local json file from application folder
     *
     * @param context  reference to context to access application resources
     * @param fileName name of the local json file to be read
     * @return json string associated with the loaded json file
     */
    private fun loadJsonFromAsset(context: Context, fileName: String): String? {
        var json: String? = null
        //open the file via an input stream and read the bytes into a buffered reader
        try {
            val inputStream = context.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            json = buffer.toString(Charsets.UTF_8)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return json
    }

    /**
     * set up default placeholder and error images for glide loading
     *
     * @param type image type - poster/backdrop/cast - to decide the placeholder/error images
     * @return reference to RequestOptions that defines the set properties
     */
    fun setupGlide(type: String): RequestOptions {
        val requestOptions = RequestOptions()
        when (type) {
            POSTER_IMG, BACKDROP_IMG -> requestOptions
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .error(R.drawable.ic_error_outline)
            Constants.CAST_IMG -> requestOptions
                    .placeholder(R.drawable.ic_account_circle)
                    .error(R.drawable.ic_account_circle)
        }

        return requestOptions
    }

    /**
     * set up RecyclerView properties
     *
     * @param context      reference to context to access application resources
     * @param recyclerView reference to RecyclerView
     */
    fun setupRecyclerView(context: Context, recyclerView: RecyclerView, layoutType: Int) {
        when (layoutType) {
            0 -> recyclerView.layoutManager = GridLayoutManager(context, getSpanCount(context))
            1 -> recyclerView.layoutManager = LinearLayoutManager(context)
            2 -> recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
        recyclerView.setItemViewCacheSize(Constants.ITEM_VIEW_CACHE_SIZE)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.setHasFixedSize(true)
    }

    /**
     * decide the column count for a grid layout RecyclerView depending on screen size
     *
     * @param context reference to context to access application resources
     * @return column count
     */
    private fun getSpanCount(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        return (dpWidth / 180).toInt()
    }
}
