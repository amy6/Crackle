package example.com.crackle;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static example.com.crackle.Constants.LOG_TAG;

public class Utils {

    public static final String POSTER_IMG = "poster";
    public static final String BACKDROP_IMG = "backdrop";
    public static final String CAST_IMG = "cast";

    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public static String formatDuration(Context context, int timeInMins) {
        String duration;
        int hours = timeInMins / 60;
        int minutes = timeInMins % 60;
        duration = String.format(context.getString(R.string.time_format), hours, minutes);
        if (hours <= 0 && minutes <= 0) {
            duration = "N/A";
        }
        if (minutes <= 0 && hours > 0) {
            duration = duration.split(":") [1];
        }
        if (hours <= 0 && minutes > 0) {
            duration = duration.split(":") [0];
        }
        if (hours == 1) {
            duration = duration.replace("hrs", "hr");
        }
        if (minutes == 1) {
            duration = duration.replace("mins", "min");
        }
        return duration.replace(":", "");
    }

    public static HashMap<Integer, String> fetchAllGenres(Context context) {
        HashMap<Integer, String> map = null;
        String json = loadJsonFromAsset(context, "genre.json");
        Log.d(LOG_TAG, json);
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray genres = jsonObject.getJSONArray("genres");
            map = new HashMap<>();
            for (int i = 0; i < genres.length() ; i ++) {
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

    public static HashMap<String, String> fetchAllLanguages(Context context) {
        HashMap<String, String> map = null;
        String json = loadJsonFromAsset(context, "language.json");
        Log.d(LOG_TAG, json);
        try {
            JSONArray jsonArray = new JSONArray(json);
            map = new HashMap<>();
            for (int i=0 ; i < jsonArray.length(); i++) {
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

    private static String loadJsonFromAsset(Context context, String fileName) {
        String json = null;
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

    public static RequestOptions setupGlide(String type) {
        RequestOptions requestOptions = new RequestOptions();
        switch (type) {
            case POSTER_IMG:
            case BACKDROP_IMG:
                requestOptions
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
}
