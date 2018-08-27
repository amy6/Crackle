package example.com.crackle;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

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

    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public static String formatDuration(int timeInMins) {
        String duration;
        int hours = timeInMins / 60;
        int minutes = timeInMins % 60;
        duration = String.format("%dh : %02dm", hours, minutes);
        return duration;
    }

    public static HashMap<Integer, String> fetchAllGenres(Context context) {
        HashMap<Integer, String> map = null;
        String json = loadJsonFromAsset(context);
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

    private static String loadJsonFromAsset(Context context) {
        String json = null;
        try {
            InputStream inputStream = context.getAssets().open("genre.json");
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
}
