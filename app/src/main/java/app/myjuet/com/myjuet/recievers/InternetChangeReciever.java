package app.myjuet.com.myjuet.recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import app.myjuet.com.myjuet.services.RefreshService;

public class InternetChangeReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            if (!intent.getBooleanExtra("manual", false))
                context.startService(new Intent(context, RefreshService.class).putExtra("alarm", "no"));
            else {
                Log.v("background", "intent send");
                Intent service = new Intent(context, RefreshService.class);
                service.putExtra("alarm", "no");
                service.putExtra("manual", true);
                context.startService(service);

            }

        }
    }
}
