package app.myjuet.com.myjuet.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import androidx.lifecycle.LifecycleService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import app.myjuet.com.myjuet.DrawerActivity;
import app.myjuet.com.myjuet.R;
import app.myjuet.com.myjuet.database.AppDatabase;
import app.myjuet.com.myjuet.utilities.Constants;
import app.myjuet.com.myjuet.utilities.SettingsActivity;
import app.myjuet.com.myjuet.vm.AttendenceViewModel;

import static app.myjuet.com.myjuet.utilities.webUtilities.isConnected;


@SuppressWarnings({"RedundantStringConstructorCall", "UnusedAssignment", "TryWithIdenticalCatches"})
public class RefreshService extends LifecycleService {
    AttendenceViewModel mAttendenceViewModel;
    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel() {
        String channelId = "my_service";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_HIGH);
        chan.setLightColor(Color.GREEN);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        service.createNotificationChannel(chan);
        return channelId;
    }
    void startForeground() {
        String channelId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = createNotificationChannel();
        } else {
            // If earlier version channel ID is not used
            channelId = "";
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setOngoing(true)
                .setContentTitle("MyJuet Is Running")
                .setContentText("Waiting For Sync to Finish")
                .setSmallIcon(R.drawable.ic_notification_icon);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            notificationBuilder.setCategory(Notification.CATEGORY_SERVICE);
        Notification notification = notificationBuilder.build();
        startForeground(101, notification);

    }
    @Override
    public void onCreate() {
        super.onCreate();
        startForeground();
    }

    public static boolean pingHost(String host, int port, int timeout) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), timeout);
            socket.close();
            return true;
        } catch (IOException e) {
            return false; // Either timeout or unreachable or failed DNS lookup.
        }
    }
    void handleStatusCode(Constants.Status status){
            switch (status){
                case LOADING:
                    break;
                case SUCCESS:
                    break;
                case WRONG_PASSWORD:
                    sendNotification("Incorrect Credentials", 0, false);
                    stopSelf();
                    break;
                case NO_INTERNET:
                    sendNotification("Connection Error", 1, false);
                    stopSelf();
                    break;
                case WEBKIOSK_DOWN:
                    sendNotification("Unable To Reach Webkiosk", 1, false);
                    stopSelf();
                    break;
                case FAILED:
                    sendNotification("Failed To Load", 1, false);
                    stopSelf();
                    break;
            }
    }
    String DateString;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (intent != null) {
            mAttendenceViewModel = new AttendenceViewModel(getApplication());
            boolean isConnected = isConnected(this);
            DateString = new String();
            Boolean today = false;
            SharedPreferences prefs = getSharedPreferences(getString(R.string.preferencefile), Context.MODE_PRIVATE);

            if (!intent.getBooleanExtra("manual", false)) {
                DateString = prefs.getString(Constants.DATE, null);
                Date dateobj = new Date();
                SimpleDateFormat formattor = new SimpleDateFormat("dd/MMM HH:mm", Locale.getDefault());
                String temp = formattor.format(dateobj);
                temp = temp.substring(0, temp.indexOf(" "));
                if (DateString != null && DateString.substring(0, DateString.indexOf(" ")).equals(temp)) {
                    today = true;
                }
            }

            String user = prefs.getString(getString(R.string.key_enrollment), "").toUpperCase().trim();
            String pass = prefs.getString(getString(R.string.key_password), "");
            boolean temp = prefs.getBoolean("autosync", true) || intent.getBooleanExtra("manual", false);
            if ((!user.equals("") || !pass.equals("")) && !today && isConnected && temp) {
                sendNotification("Logging In...", 1, false);
                mAttendenceViewModel.loginUser().observe(this, status -> {
                    if (status == Constants.Status.SUCCESS) {
                        sendNotification("Sync in Progress...", 1, false);
                        mAttendenceViewModel.startLoading().observe(this, status1 -> {
                            if (status1 == Constants.Status.SUCCESS) {
                                Date dateobj = new Date();
                                SimpleDateFormat formattor = new SimpleDateFormat("dd/MMM HH:mm", Locale.getDefault());
                                String DateString = formattor.format(dateobj);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString(Constants.DATE, DateString);
                                editor.commit();
                                sendNotification("Syncing Details In Progress...", 1, false);
                                AppDatabase.newInstance(this).AttendenceDao().AttendanceLoadingPendingCountObserver().observe(this, number -> {
                                    if (number != null && number <= 0) {
                                        sendNotification("Attendence Synced successfully ", 1, false);
                                        stopSelf();
                                    }
                                });
                                mAttendenceViewModel.loadDetails().observe(this, status2 -> {
                                    if (status2 == Constants.Status.SUCCESS) {
                                        sendNotification("Attendence Synced successfully " + DateString, 1, false);
                                        stopSelf();
                                    } else {
                                        handleStatusCode(status2);
                                    }
                                });
                            } else {
                                handleStatusCode(status1);
                            }
                        });
                    } else {
                        handleStatusCode(status);
                    }
                });
            } else if (user.equals("") || pass.equals("")) {
                sendNotification("Please Enter Login Details", 0, false);
                stopSelf();
            } else if (today || !isConnected) {
                stopSelf();
            } else {
                sendNotification("Wrong Credentials", 0, false);
                stopSelf();
            }
            return START_STICKY;
        }
        return START_NOT_STICKY;

    }

    private void sendNotification(String msg, int i, boolean onGoing) {
        PendingIntent contentIntent;
        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (i == 1) {
            contentIntent = PendingIntent.getActivity(getApplicationContext(), 55,
                    new Intent(getApplicationContext(), DrawerActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            contentIntent = PendingIntent.getActivity(getApplicationContext(), 55,
                    new Intent(getApplicationContext(), SettingsActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        }
        String channelId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = createNotificationChannel();
        } else {
            // If earlier version channel ID is not used
            channelId = "";
        }
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext(),channelId)
                        .setContentTitle("Attendence")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setContentText(msg)
                        .setSmallIcon(R.drawable.ic_notification_icon)
                        .setAutoCancel(true);
        if (onGoing)
            mBuilder.setOngoing(true);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(105, mBuilder.build());
    }

    @Override
    public void onDestroy() {
        AppDatabase.newInstance(this).AttendenceDao().updateLoading(false);
        super.onDestroy();
    }

}
