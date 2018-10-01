package example.com.crackle.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.View;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import example.com.crackle.R;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static example.com.crackle.utils.Constants.BACKDROP_IMG;
import static example.com.crackle.utils.Constants.CAST_IMG;
import static example.com.crackle.utils.Constants.ITEM_VIEW_CACHE_SIZE;
import static example.com.crackle.utils.Constants.POSTER_IMG;

public class Utils {

    /**
     * verifies if the device is connected to the internet
     *
     * @param context reference to context to access application resources
     * @return a boolean flag indicating whether device is connected to the internet
     */
    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        return networkInfo == null || !networkInfo.isConnectedOrConnecting();
    }

    /**
     * format run time of a movie to a string in the form of h:m
     *
     * @param context    reference to context to access application resources
     * @param timeInMins runtime of the movie in minutes
     * @return duration in the format of h:m
     */
    public static String formatDuration(Context context, int timeInMins) {
        String duration;
        int hours = timeInMins / 60;
        int minutes = timeInMins % 60;
        duration = String.format(context.getString(R.string.time_format), hours, minutes);
        if (hours <= 0 && minutes <= 0) {
            duration = "N/A";
        }

        //display only mins if hours = 0
        if (minutes <= 0 && hours > 0) {
            duration = duration.split(":")[1];
        }
        //display only hours if mins = 0
        if (hours <= 0 && minutes > 0) {
            duration = duration.split(":")[0];
        }

        //handle plural condition for hours
        if (hours == 1) {
            duration = duration.replace("hrs", "hr");
        }
        //handle plural condition for mins
        if (minutes == 1) {
            duration = duration.replace("mins", "min");
        }
        return duration.replace(":", "");
    }

    /**
     * parse local json file containing list of genre code and names
     *
     * @param context reference to context to access application resources
     * @return sparse array of genre code and corresponding names as key-value pairs
     */
    public static SparseArray<String> fetchAllGenres(Context context) {
        SparseArray<String> map = null;
        String json = loadJsonFromAsset(context, "genre.json");

        //parse json to get genre code and name
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray genres = jsonObject.getJSONArray("genres");
            map = new SparseArray<>();
            for (int i = 0; i < genres.length(); i++) {
                JSONObject genre = genres.getJSONObject(i);
                int id = genre.getInt("id");
                String name = genre.getString("name");

                map.put(id, name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }


    /**
     * parse local json file containing list of language code and names
     *
     * @param context reference to context to access application resources
     * @return map of language code and corresponding names as key-value pairs
     */
    public static HashMap<String, String> fetchAllLanguages(Context context) {
        HashMap<String, String> map = null;
        String json = loadJsonFromAsset(context, "language.json");

        //parse json to get language code and name
        try {
            JSONArray jsonArray = new JSONArray(json);
            map = new HashMap<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject language = jsonArray.getJSONObject(i);
                String languageCode = language.getString("iso_639_1");
                String languageName = language.getString("english_name");

                map.put(languageCode, languageName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * load and read local json file from application folder
     *
     * @param context  reference to context to access application resources
     * @param fileName name of the local json file to be read
     * @return json string associated with the loaded json file
     */
    private static String loadJsonFromAsset(Context context, String fileName) {
        String json = null;
        //open the file via an input stream and read the bytes into a buffered reader
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * set up default placeholder and error images for glide loading
     *
     * @param type image type - poster/backdrop/cast - to decide the placeholder/error images
     * @return reference to RequestOptions that defines the set properties
     */
    public static RequestOptions setupGlide(String type) {
        RequestOptions requestOptions = new RequestOptions();
        switch (type) {
            case POSTER_IMG:
            case BACKDROP_IMG:
                requestOptions
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .error(R.drawable.ic_error_outline);
                break;
            case CAST_IMG:
                requestOptions
                        .placeholder(R.drawable.ic_account_circle)
                        .error(R.drawable.ic_account_circle);
                break;
        }

        return requestOptions;
    }

    /**
     * set up RecyclerView properties
     *
     * @param context      reference to context to access application resources
     * @param recyclerView reference to RecyclerView
     */
    public static void setupRecyclerView(Context context, RecyclerView recyclerView, int layoutType) {
        switch (layoutType) {
            case 0:
                recyclerView.setLayoutManager(new GridLayoutManager(context, getSpanCount(context)));
                break;
            case 1:
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                break;
            case 2:
                recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                break;
        }
        recyclerView.setItemViewCacheSize(ITEM_VIEW_CACHE_SIZE);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
    }

    /**
     * decide the column count for a grid layout RecyclerView depending on screen size
     *
     * @param context reference to context to access application resources
     * @return column count
     */
    private static int getSpanCount(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / 180);
    }
}
