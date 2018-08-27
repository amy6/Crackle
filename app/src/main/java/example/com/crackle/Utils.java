package example.com.crackle;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import static android.content.Context.CONNECTIVITY_SERVICE;

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
}
