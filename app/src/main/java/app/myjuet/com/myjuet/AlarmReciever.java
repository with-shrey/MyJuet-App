package app.myjuet.com.myjuet;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import app.myjuet.com.myjuet.web.LoginWebkiosk;

import static app.myjuet.com.myjuet.R.id.send;


public class AlarmReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        long[] patern = {1000, 1000, 1000, 1000, 1000, 1000};
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Log.v("AlarmReciever", "Recieved");
        PendingIntent contentIntent;
        NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent drawer = new Intent(context, DrawerActivity.class);
        drawer.putExtra("fragment", 2);
        contentIntent = PendingIntent.getActivity(context, 0,
                drawer, PendingIntent.FLAG_UPDATE_CURRENT);
        String time = new SimpleDateFormat("HHmm").format(new Date());
        if (time.equals("1250")) {
            patern = new long[]{500};
        }
        String Title = intent.getStringExtra("title");
        if (Title.equals("app"))
            context.startService(new Intent(context, RefreshService.class).putExtra("alarm", "yes"));
        else if (time.equals("0000") || time.equals("0725") || time.equals("1925") || time.equals("1250")) {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setContentTitle(Title)
                            .setVibrate(patern)
                            .setContentText("Click To View Menu")
                            .setSound(defaultSoundUri)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setAutoCancel(true);
            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(0, mBuilder.build());
        }
    }
}
