package app.myjuet.com.myjuet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import app.myjuet.com.myjuet.data.AttendenceData;

/**
 * Created by Shrey on 05-Apr-17.
 */

public class InternetChangeReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        File directory = new File(context.getFilesDir().getAbsolutePath()
                + File.separator + "serlization");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        String date = "date.srl";
        Boolean today = false;
        String DateString;
        ObjectInput dateinput = null;

        try {
            dateinput = new ObjectInputStream(new FileInputStream(directory
                    + File.separator + date));
            DateString = (String) dateinput.readObject();
            Date dateobj = new Date();
            SimpleDateFormat formattor = new SimpleDateFormat("dd/MMM HH:mm");
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
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();


        if (isConnected && !today) {
        context.startService(new Intent(context, RefreshService.class));

        }

    }
}
