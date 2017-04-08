package app.myjuet.com.myjuet;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import app.myjuet.com.myjuet.data.AttendenceData;
import app.myjuet.com.myjuet.web.webUtilities;
import app.myjuet.com.myjuet.AttendenceActivity;

import static app.myjuet.com.myjuet.AttendenceActivity.Error;
import static app.myjuet.com.myjuet.AttendenceActivity.HOST_DOWN;
import static app.myjuet.com.myjuet.web.webUtilities.AttendenceCrawler;

/**
 * Created by Shrey on 05-Apr-17.
 */

public class RefreshService extends IntentService {
    public RefreshService() {
        super("RefreshService");
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

    @Override
    protected void onHandleIntent(Intent intent) {
        File directory = new File(getFilesDir().getAbsolutePath()
                + File.separator + "serlization");
        String date = "date.srl";
        String DateString = new String();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String Url = "https://webkiosk.juet.ac.in/CommonFiles/UserAction.jsp";
        String mAttendence = "https://webkiosk.juet.ac.in/StudentFiles/Academic/StudentAttendanceList.jsp";

        String user = prefs.getString(getString(R.string.enrollment), getString(R.string.defaultuser));
        String pass = prefs.getString(getString(R.string.password), getString(R.string.defaultpassword));
        String PostParam = "txtInst=Institute&InstCode=JUET&txtUType=Member+Type&UserType=S&txtCode=Enrollment No&MemberCode=" + user + "&txtPIN=Password%2FPin&Password=" + pass + "&BTNSubmit=Submit";
        ArrayList<AttendenceData> DataAttendence = new ArrayList<>();
        String Content = " ";
        Log.v("Shrey", user + " " + pass);

        if ((!user.equals(getString(R.string.defaultuser)) || !pass.equals(getString(R.string.defaultpassword))) && pingHost("webkiosk.juet.ac.in", 80, 5000)) {
            sendNotification("Attendence Sync started");
            try {

                    CookieHandler.setDefault(new CookieManager());
                    webUtilities.sendPost(Url, PostParam);
                    Content = webUtilities.GetPageContent(mAttendence);
                sendNotification("Attendence Synced 25%");
                webUtilities.conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!Content.equals(" ")) {
                DataAttendence = AttendenceCrawler(Content);
                //REturn statement
            } else {
                DataAttendence.clear();
                Log.v("Shrey", "content Empty");
            }
            if (!DataAttendence.isEmpty()) {
                Date dateobj = new Date();
                SimpleDateFormat formattor = new SimpleDateFormat("dd/MMM HH:mm");

                String filename = "MessgeScreenList.srl";
                ObjectOutput out = null;
                ObjectOutput dateout = null;
                DateString = formattor.format(dateobj);

                try {
                    out = new ObjectOutputStream(new FileOutputStream(directory
                            + File.separator + filename));
                    out.flush();
                    dateout = new ObjectOutputStream(new FileOutputStream(directory
                            + File.separator + date));
                    dateout.flush();
                    out.writeObject(DataAttendence);
                    dateout.writeObject(DateString);
                    out.close();
                    dateout.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            sendNotification("Attendence Synced successfully " + DateString);
        } else if (!user.equals(getString(R.string.defaultuser)) || !pass.equals(getString(R.string.defaultpassword)))
            sendNotification("Wrong Credentials");
        else if (pingHost("webkiosk.juet.ac.in", 80, 5000))
            sendNotification("Webkiosk Down/Unreachable");
        else sendNotification("Unknown Error");


    }

    private void sendNotification(String msg) {
        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), DrawerActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setContentTitle("Attendence")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setContentText(msg)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(1, mBuilder.build());
    }

}
