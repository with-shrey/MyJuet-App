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
        Log.v("Reciever", "REcieved");
        context.startService(new Intent(context, RefreshService.class));


    }
}
