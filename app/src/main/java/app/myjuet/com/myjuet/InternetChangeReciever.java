package app.myjuet.com.myjuet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Shrey on 05-Apr-17.
 */

public class InternetChangeReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, RefreshService.class));

    }
}
