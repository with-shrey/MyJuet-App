package app.myjuet.com.myjuet;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;
import java.util.Random;


/**
 * Created by Shrey on 17-Apr-17.
 */

public class FirebaseNotificationService extends FirebaseMessagingService {
    Bitmap bitmapimg;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map data = remoteMessage.getData();
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext());

        String imgUrl;
        if (data.size() > 0) {
            String id = (String) data.get("id");
            String type = (String) data.get("type");
            String title = (String) data.get("title");
            String message = (String) data.get("message");
            String url = (String) data.get("url");
            String by = (String) data.get("by");
            if (type.equals("bigpicture")) {
                imgUrl = (String) data.get("imgUrl");
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl(imgUrl);
                final long ONE_MEGABYTE = 1024 * 1024;
                storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        bitmapimg = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, bmOptions);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
                notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle().setSummaryText(message).bigPicture(bitmapimg).setSummaryText(by));
            } else
                notificationBuilder.setContentText(message);


            Intent intent = new Intent(this, DrawerActivity.class);
            intent.putExtra("fragment", 3);
            intent.putExtra("url", url);
            intent.putExtra("containsurl", true);
            Random generator = new Random();
            PendingIntent btn1Intent = PendingIntent.getActivity(getApplicationContext(), generator.nextInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            notificationBuilder.setContentTitle(title);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notificationBuilder.setSound(defaultSoundUri);
            notificationBuilder.addAction(R.mipmap.ic_launcher, "DETAILS", btn1Intent); // #0
            notificationBuilder.setContentIntent(btn1Intent);

        }
        if (remoteMessage.getNotification() != null) {
            super.onMessageReceived(remoteMessage);
        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }
}
