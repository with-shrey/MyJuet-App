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
import java.util.Calendar;
import java.util.Date;


import static android.media.CamcorderProfile.get;
import static app.myjuet.com.myjuet.R.id.send;


public class AlarmReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        long[] patern = {1000, 1000, 1000, 1000, 1000, 1000};
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Log.v("AlarmReciever", "Recieved");
        NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        String time = new SimpleDateFormat("HHmm").format(new Date());
        if (time.equals("1250")) {
            patern = new long[]{500};
        }
        String Title = intent.getStringExtra("title");
        if (Title.equals("app"))
            context.startService(new Intent(context, RefreshService.class).putExtra("alarm", "yes"));
        else if (intent.getIntExtra("fragmentno", 2) == 2) {
            Intent drawer = new Intent(context, DrawerActivity.class);
            drawer.putExtra("fragment", intent.getIntExtra("fragmentno", 2));
            PendingIntent contentIntent = PendingIntent.getActivity(context, 52,
                    drawer, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setContentTitle(Title)
                            .setVibrate(patern)
                            .setContentText("Click To View")
                            .setSound(defaultSoundUri)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setAutoCancel(true);
            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(0, mBuilder.build());
        } else if (intent.getIntExtra("fragmentno", 2) == 1 && Title.contains("TimeTable")) {
            Calendar calender = Calendar.getInstance();
            int i = calender.get(Calendar.DAY_OF_WEEK);
            Intent drawer = new Intent(context, DrawerActivity.class);
            drawer.putExtra("fragment", intent.getIntExtra("fragmentno", 1));
            drawer.putExtra("childfragment", i);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 53,
                    drawer, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setContentTitle(Title)
                            .setVibrate(patern)
                            .setContentText("Click To View")
                            .setSound(defaultSoundUri)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setAutoCancel(true);
            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(1, mBuilder.build());
        } else if (intent.getIntExtra("fragmentno", 2) == 1 && Title.contains("Class")) {
            Calendar calender = Calendar.getInstance();
            int i = calender.get(Calendar.DAY_OF_WEEK);
            Intent drawer = new Intent(context, DrawerActivity.class);
            drawer.putExtra("fragment", intent.getIntExtra("fragmentno", 1));
            drawer.putExtra("childfragment", i);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 54,
                    drawer, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setContentTitle(Title)
                            .setVibrate(patern)
                            .setContentText("Click To View")
                            .setSound(defaultSoundUri)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setAutoCancel(true);
            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(2, mBuilder.build());
        }
    }
}
