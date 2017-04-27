package app.myjuet.com.myjuet.recievers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;

import app.myjuet.com.myjuet.DrawerActivity;
import app.myjuet.com.myjuet.R;
import app.myjuet.com.myjuet.services.RefreshService;


public class AlarmReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        long[] patern = {1000, 1000, 1000, 1000, 1000, 1000};
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preferencefile), Context.MODE_PRIVATE);

        Uri timetableuri = Uri.parse(sharedPref.getString(context.getString(R.string.key_notification_tt), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString()));
        Uri messuri = Uri.parse(sharedPref.getString(context.getString(R.string.key_notification_mess), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString()));

        NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

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
                            .setSound(messuri)
                            .setSmallIcon(R.drawable.ic_notification_icon)
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
                            .setSound(timetableuri)
                            .setSmallIcon(R.drawable.ic_notification_icon)
                            .setAutoCancel(true);
            mBuilder.setContentIntent(contentIntent);
            if (i != 1)
            mNotificationManager.notify(1, mBuilder.build());
        } else if (intent.getIntExtra("fragmentno", 2) == 1 && Title.contains("Class")) {
            Calendar calender = Calendar.getInstance();
            int i = calender.get(Calendar.DAY_OF_WEEK);
            Intent classes = new Intent(context, DrawerActivity.class);
            classes.putExtra("fragment", intent.getIntExtra("fragmentno", 1));
            classes.putExtra("childfragment", i);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 54,
                    classes, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setContentTitle(Title)
                            .setVibrate(patern)
                            .setContentText("Click To View")
                            .setSound(timetableuri)
                            .setSmallIcon(R.drawable.ic_notification_icon)
                            .setAutoCancel(true);
            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(2, mBuilder.build());
        }
    }
}
