package me.hnguyenuml.spyday.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

/**
 * Created by jason on 11/3/16.
 */

public class MapUtils {

    public static void showToast(Context context, String message){
        Toast.makeText(context ,message, Toast.LENGTH_SHORT).show();
    }

    public static boolean verifyConnection(Context context) {
        boolean isConnected;
        ConnectivityManager conectivtyManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        isConnected = conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected();
        return isConnected;
    }

    public static String getLocation(String latitudeFinal,String longitudeFinal) {
        return "https://maps.googleapis.com/maps/api/staticmap?center=" + latitudeFinal + ","
                + longitudeFinal +"&zoom=18&size=280x280&markers=color:red|" + latitudeFinal + "," + longitudeFinal;
    }
}
