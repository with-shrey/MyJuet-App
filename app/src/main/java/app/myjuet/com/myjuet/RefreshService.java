package app.myjuet.com.myjuet;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

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
import java.util.Locale;

import app.myjuet.com.myjuet.data.AttendenceData;
import app.myjuet.com.myjuet.web.SettingsActivity;
import app.myjuet.com.myjuet.web.webUtilities;

import static app.myjuet.com.myjuet.web.webUtilities.AttendenceCrawler;


@SuppressWarnings({"RedundantStringConstructorCall", "UnusedAssignment", "TryWithIdenticalCatches"})
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    protected void onHandleIntent(Intent intent) {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        File directory = new File(getFilesDir().getAbsolutePath()
                + File.separator + "serlization");
        if (!directory.exists()) {
            boolean dir = directory.mkdirs();

        }
        String date = "date.srl";
        String DateString = new String();
        Boolean today = false;
        ObjectInput dateinput = null;
        //noinspection TryWithIdenticalCatches
        try {
            dateinput = new ObjectInputStream(new FileInputStream(directory
                    + File.separator + date));
            DateString = (String) dateinput.readObject();
            Date dateobj = new Date();
            SimpleDateFormat formattor = new SimpleDateFormat("dd/MMM HH:mm", Locale.getDefault());
            String temp = formattor.format(dateobj);
            temp = temp.substring(0, temp.indexOf(" "));
            if (DateString.substring(0, DateString.indexOf(" ")).equals(temp)) {
                today = true;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SharedPreferences prefs = getSharedPreferences(getString(R.string.preferencefile), Context.MODE_PRIVATE);
        String Url = "https://webkiosk.juet.ac.in/CommonFiles/UserAction.jsp";
        String mAttendence = "https://webkiosk.juet.ac.in/StudentFiles/Academic/StudentAttendanceList.jsp";

        String user = prefs.getString(getString(R.string.key_enrollment), "").toUpperCase().trim();
        String pass = prefs.getString(getString(R.string.key_password), "");
        String PostParam = "txtInst=Institute&InstCode=JUET&txtuType=Member+Type&UserType=S&txtCode=Enrollment+No&MemberCode=" + user + "&txtPin=Password%2FPin&Password=" + pass + "&BTNSubmit=Submit";
        ArrayList<AttendenceData> DataAttendence = new ArrayList<>();
        String Content = " ";

        if ((!user.equals("") || !pass.equals("")) && pingHost("webkiosk.juet.ac.in", 80, 5000) && !today && isConnected) {
            File directoryFile = new File(getFilesDir().getAbsolutePath()
                    + File.separator + "serlization" + File.separator + "MessgeScreenList.srl");
            boolean deleted;
            if (directoryFile.exists()) {
                deleted = directoryFile.delete();

            }
            sendNotification("Attendence Sync started", 1);
            try {
                    CookieHandler.setDefault(new CookieManager());
                    webUtilities.sendPost(Url, PostParam);
                    Content = webUtilities.GetPageContent(mAttendence);
                sendNotification("Sync in Progress...", 1);

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!Content.equals(" ")) {
                DataAttendence = AttendenceCrawler(Content);
                //REturn statement
            } else {
                DataAttendence.clear();
            }
            if (!DataAttendence.isEmpty()) {
                Date dateobj = new Date();
                SimpleDateFormat formattor = new SimpleDateFormat("dd/MMM HH:mm", Locale.getDefault());

                String filename = "MessgeScreenList.srl";
                ObjectOutput out = null;
                ObjectOutput dateout = null;
                DateString = formattor.format(dateobj);

                try {
                    out = new ObjectOutputStream(new FileOutputStream(directory
                            + File.separator + filename));
                    dateout = new ObjectOutputStream(new FileOutputStream(directory
                            + File.separator + date));
                    out.flush();
                    out.writeObject(DataAttendence);
                    dateout.flush();
                    dateout.writeObject(DateString);
                    out.close();
                    dateout.close();
                } catch (FileNotFoundException e) {

                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            sendNotification("Attendence Synced successfully " + DateString, 1);
        } else if (user.equals("") || pass.equals(""))
            sendNotification("Please Enter Login Details", 0);
        else if (today || !isConnected) ;
        else if (!pingHost("webkiosk.juet.ac.in", 80, 5000)) ;

        //  sendNotification("Webkiosk Down/Unreachable", 1);

        else
            sendNotification("Wrong Credentials", 1);


    }

    private void sendNotification(String msg, int i) {
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
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setContentTitle("Attendence")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setContentText(msg)
                        .setSmallIcon(R.drawable.ic_notification_icon)
                        .setAutoCancel(true);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(3, mBuilder.build());
    }

}
