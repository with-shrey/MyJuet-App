package app.myjuet.com.myjuet.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import androidx.core.app.NotificationCompat;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.File;

import app.myjuet.com.myjuet.DrawerActivity;
import app.myjuet.com.myjuet.R;


@SuppressWarnings("SpellCheckingInspection")
public class FirebaseNotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().get("type").equals("mess")) {
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            String imageFileName = "Mess";
            File image = null;
            image = new File(storageDir, imageFileName + ".jpg");
            if (image.exists())
                image.delete();
            Intent intent = new Intent(this, DrawerActivity.class);
            intent.putExtra("fragment", 2);
            PendingIntent btn1Intent = PendingIntent.getActivity(this, 50, intent, PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle("Menu Updated")
                    .setContentText("Click To Refresh")
                    .setSmallIcon(R.drawable.ic_notification_icon)
                    .addAction(R.drawable.ic_refresh_black_24dp, "Refresh", btn1Intent)
                    .setContentIntent(btn1Intent)
                    .setAutoCancel(false);


            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(Integer.valueOf(remoteMessage.getData().get("id")), notificationBuilder.build());

        } else if ((remoteMessage.getData().get("type").equals("update"))) {
            SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preferencefile), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("latest_version_number", Integer.valueOf(remoteMessage.getData().get("version_no")));
            editor.putString("latest_version_code", remoteMessage.getData().get("version_code"));
            editor.putString("url", remoteMessage.getData().get("url"));
            editor.apply();
        } else {
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Intent intent = new Intent(this, DrawerActivity.class);
            intent.putExtra("fragment", 4);
            intent.putExtra("url", remoteMessage.getData().get("url"));
            intent.putExtra("containsurl", true);

            PendingIntent btn1Intent = PendingIntent.getActivity(this, 50, intent, PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle(remoteMessage.getData().get("title"))
                    .setContentText(remoteMessage.getData().get("short"))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getData().get("message")).setSummaryText(remoteMessage.getData().get("by")).setBigContentTitle(remoteMessage.getData().get("title")))
                    .setSmallIcon(R.drawable.ic_notification_icon)
                    .setSound(defaultSoundUri)
                    .addAction(R.drawable.ic_open, "DETAILS", btn1Intent)
                    .setContentIntent(btn1Intent)
                    .setSubText(remoteMessage.getData().get("by"))
                    .setAutoCancel(false);


            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(Integer.valueOf(remoteMessage.getData().get("id")), notificationBuilder.build());
        }

    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }
}
