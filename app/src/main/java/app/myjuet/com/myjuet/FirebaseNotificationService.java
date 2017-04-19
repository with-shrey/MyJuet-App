package app.myjuet.com.myjuet;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;
import java.util.Random;

import static android.R.attr.id;
import static app.myjuet.com.myjuet.R.string.url;


/**
 * Created by Shrey on 17-Apr-17.
 */

public class FirebaseNotificationService extends FirebaseMessagingService {
    Bitmap bitmapimg;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Log.v("payload", "notification");
        Intent intent = new Intent(this, DrawerActivity.class);
        intent.putExtra("fragment", 3);
        intent.putExtra("url", remoteMessage.getData().get("url"));
        intent.putExtra("containsurl", true);

        PendingIntent btn1Intent = PendingIntent.getActivity(this, 50, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("short"))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getData().get("title")).setSummaryText(remoteMessage.getData().get("by")).setBigContentTitle(remoteMessage.getData().get("message")))
                .setSmallIcon(R.mipmap.ic_launcher)
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
