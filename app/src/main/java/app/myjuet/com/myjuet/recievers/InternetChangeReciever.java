package app.myjuet.com.myjuet.recievers;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.core.app.ActivityCompat;
import androidx.legacy.content.WakefulBroadcastReceiver;
import android.util.Log;

import app.myjuet.com.myjuet.services.RefreshService;

public class InternetChangeReciever extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            if (!intent.getBooleanExtra("manual", false))
                ActivityCompat.startForegroundService(context, new Intent(context, RefreshService.class).putExtra("alarm", "no").putExtra("reciever", 1));
            else {
                Log.v("background", "intent send");
                Intent service = new Intent(context, RefreshService.class);
                service.putExtra("alarm", "no");
                service.putExtra("manual", true);
                service.putExtra("reciever", 1);
                startWakefulService(context, service);

            }

        }

        completeWakefulIntent(intent);
    }
}
