package app.myjuet.com.myjuet;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


@SuppressWarnings("SpellCheckingInspection")
public class FirebaseNotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = new Intent(this, DrawerActivity.class);
        intent.putExtra("fragment", 3);
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

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }
}
