package morgantech.com.gms.Utils;

import android.content.Context;
import android.net.ConnectivityManager;

public class Helper {

    /**
     * Method to check Internet connection
     *
     * @return
     */
    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);

        // test for connection
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            //no conection
            return false;
        }
    }

}

