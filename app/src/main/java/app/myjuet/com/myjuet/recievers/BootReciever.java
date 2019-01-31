package app.myjuet.com.myjuet.recievers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import app.myjuet.com.myjuet.R;
import app.myjuet.com.myjuet.data.AttendenceData;
import app.myjuet.com.myjuet.data.TimeTableData;
import app.myjuet.com.myjuet.database.AppDatabase;
import app.myjuet.com.myjuet.workers.RefreshWorker;

import static android.content.Context.ALARM_SERVICE;


@SuppressWarnings({"unused", "StatementWithEmptyBody"})
public class BootReciever extends BroadcastReceiver {
    ArrayList<TimeTableData> datatt = new ArrayList<>();
    ArrayList<AttendenceData> attendenceDatas = new ArrayList<>();


    @SuppressWarnings("UnusedAssignment")
    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preferencefile), Context.MODE_PRIVATE);



            PeriodicWorkRequest workRequest =new PeriodicWorkRequest.Builder(RefreshWorker.class,24, TimeUnit.HOURS)
                    .setConstraints(
                            new Constraints.Builder()
                                    .setRequiredNetworkType(NetworkType.CONNECTED)
                                    .build()
                    ).build();
            if (WorkManager.getInstance() != null)
                WorkManager.getInstance().enqueue(workRequest);

        if (sharedPref.getBoolean(context.getString(R.string.key_alarm_meal), false)) {
            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(Calendar.HOUR_OF_DAY, 7);
            calendar1.set(Calendar.MINUTE, 30);
            calendar1.set(Calendar.SECOND, 0);
            if (calendar1.before(Calendar.getInstance()))
                calendar1.add(Calendar.DATE, 1);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.set(Calendar.HOUR_OF_DAY, 19);
            calendar2.set(Calendar.MINUTE, 30);
            calendar2.set(Calendar.SECOND, 0);
            if (calendar2.before(Calendar.getInstance()))
                calendar2.add(Calendar.DATE, 1);
            Calendar calendar3 = Calendar.getInstance();
            calendar3.set(Calendar.HOUR_OF_DAY, 13);
            calendar3.set(Calendar.MINUTE, 0);
            calendar3.set(Calendar.SECOND, 0);
            if (calendar3.before(Calendar.getInstance()))
                calendar3.add(Calendar.DATE, 1);
            int minmeal = sharedPref.getInt(context.getString(R.string.key_minutes_before_meal), 15);


            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 48, new Intent(context, AlarmReciever.class).putExtra("title", "BreakFast Time").putExtra("fragmentno", 2), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, 49, new Intent(context, AlarmReciever.class).putExtra("title", "Dinner Time").putExtra("fragmentno", 2), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, 50, new Intent(context, AlarmReciever.class).putExtra("title", "Lunch Time").putExtra("fragmentno", 2), PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            am.setRepeating(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis() - (minmeal * 60 * 1000), AlarmManager.INTERVAL_DAY, pendingIntent);
            AlarmManager am1 = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            am1.setRepeating(AlarmManager.RTC_WAKEUP, calendar2.getTimeInMillis() - (minmeal * 60 * 1000), AlarmManager.INTERVAL_DAY, pendingIntent1);
            AlarmManager am2 = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            am2.setRepeating(AlarmManager.RTC_WAKEUP, calendar3.getTimeInMillis() - (minmeal * 60 * 1000), AlarmManager.INTERVAL_DAY, pendingIntent2);
        }
        if (sharedPref.getBoolean(context.getString(R.string.key_alarm_class), false)) {
            SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.preferencefile), Context.MODE_PRIVATE);
            final String batch = prefs.getString(context.getString(R.string.key_batch), "").toUpperCase().trim();
            final String sem = prefs.getString(context.getString(R.string.key_semester), "").toUpperCase().trim();
            File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            boolean mkd = false;
            if (storageDir != null && !storageDir.exists())
                mkd = storageDir.mkdir();
            if (mkd) {
            }
            File settings = null;
            settings = new File(storageDir, sem + "_" + batch + ".txt");
            ObjectInput ois = null;

            try {
                ois = new ObjectInputStream(new FileInputStream(settings));
            } catch (IOException e) {
                e.printStackTrace();
            }
            //noinspection TryWithIdenticalCatches
            try {
                if (ois != null) {
                    //noinspection unchecked
                    datatt = (ArrayList<TimeTableData>) ois.readObject();
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            try {
                attendenceDatas  = new ArrayList<>(AppDatabase.newInstance(context.getApplicationContext()).AttendenceDao().AttendanceData());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!datatt.isEmpty() && !attendenceDatas.isEmpty()) {
                int minclass = sharedPref.getInt(context.getString(R.string.key_minutes_before_class), 15);
                PendingIntent[][] pendingintents = new PendingIntent[6][7];
                for (int i = 0; i < 6; i++) {
                    @SuppressWarnings("UnusedAssignment")
                    TimeTableData tempData = new TimeTableData();
                    tempData = datatt.get(i);

                    if (tempData.getPosNine() == 0 && tempData.getPosTen() != 0) {
                        pendingintents[i][0] = PendingIntent.getBroadcast(context, (i), new Intent(context, AlarmReciever.class).putExtra("title", "Class @ 10:00").putExtra("fragmentno", 1), PendingIntent.FLAG_UPDATE_CURRENT);
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.DAY_OF_WEEK, i + 2);
                        calendar.set(Calendar.HOUR_OF_DAY, 10);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        if (calendar.before(Calendar.getInstance()))
                            calendar.add(Calendar.DATE, 7);
                        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - (minclass * 60 * 1000), AlarmManager.INTERVAL_DAY * 7, pendingintents[i][0]);
                    }
                    if (tempData.getPosTen() == 0 && !attendenceDatas.get(tempData.getPosNine()).getmName().contains("LAB") && tempData.getPosEleven() != 0) {
                        pendingintents[i][1] = PendingIntent.getBroadcast(context, 6 + i, new Intent(context, AlarmReciever.class).putExtra("title", "Class @ 11:00").putExtra("fragmentno", 1), PendingIntent.FLAG_UPDATE_CURRENT);
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.DAY_OF_WEEK, i + 2);
                        calendar.set(Calendar.HOUR_OF_DAY, 11);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        if (calendar.before(Calendar.getInstance()))
                            calendar.add(Calendar.DATE, 7);
                        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - (minclass * 60 * 1000), AlarmManager.INTERVAL_DAY * 7, pendingintents[i][1]);
                    }
                    if (tempData.getPosEleven() == 0 && tempData.getPosTwelve() != 0) {
                        pendingintents[i][2] = PendingIntent.getBroadcast(context, 13 + i, new Intent(context, AlarmReciever.class).putExtra("title", "Class @ 12:00").putExtra("fragmentno", 1), PendingIntent.FLAG_UPDATE_CURRENT);

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.DAY_OF_WEEK, i + 2);
                        calendar.set(Calendar.HOUR_OF_DAY, 12);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        if (calendar.before(Calendar.getInstance()))
                            calendar.add(Calendar.DATE, 7);
                        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - (minclass * 60 * 1000), AlarmManager.INTERVAL_DAY * 7, pendingintents[i][2]);
                    }
                    if (tempData.getPosTwo() != 0) {
                        pendingintents[i][3] = PendingIntent.getBroadcast(context, 20 + i, new Intent(context, AlarmReciever.class).putExtra("title", "Class @ 02:00").putExtra("fragmentno", 1), PendingIntent.FLAG_UPDATE_CURRENT);
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.DAY_OF_WEEK, i + 2);
                        calendar.set(Calendar.HOUR_OF_DAY, 14);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        if (calendar.before(Calendar.getInstance()))
                            calendar.add(Calendar.DATE, 7);
                        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - (minclass * 60 * 1000), AlarmManager.INTERVAL_DAY * 7, pendingintents[i][3]);
                    }
                    if (tempData.getPosTwo() == 0 && tempData.getPosThree() != 0) {
                        pendingintents[i][4] = PendingIntent.getBroadcast(context, 27 + i, new Intent(context, AlarmReciever.class).putExtra("title", "Class @ 03:00").putExtra("fragmentno", 1), PendingIntent.FLAG_UPDATE_CURRENT);

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.DAY_OF_WEEK, i + 2);
                        calendar.set(Calendar.HOUR_OF_DAY, 15);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        if (calendar.before(Calendar.getInstance()))
                            calendar.add(Calendar.DATE, 7);
                        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - (minclass * 60 * 1000), AlarmManager.INTERVAL_DAY * 7, pendingintents[i][4]);
                    }
                    if (tempData.getPosThree() == 0 && tempData.getPosFour() != 0) {
                        pendingintents[i][5] = PendingIntent.getBroadcast(context, 34 + i, new Intent(context, AlarmReciever.class).putExtra("title", "Class @ 04:00").putExtra("fragmentno", 1), PendingIntent.FLAG_UPDATE_CURRENT);

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.DAY_OF_WEEK, i + 2);
                        calendar.set(Calendar.HOUR_OF_DAY, 16);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        if (calendar.before(Calendar.getInstance()))
                            calendar.add(Calendar.DATE, 7);
                        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - (minclass * 60 * 1000), AlarmManager.INTERVAL_DAY * 7, pendingintents[i][5]);
                    }
                    if (tempData.getPosFour() == 0 && !attendenceDatas.get(tempData.getPosThree()).getmName().contains("LAB") && tempData.getPosFive() != 0) {
                        pendingintents[i][6] = PendingIntent.getBroadcast(context, 41 + i, new Intent(context, AlarmReciever.class).putExtra("title", "Class @ 05:00" + String.valueOf(i)).putExtra("fragmentno", 1), PendingIntent.FLAG_UPDATE_CURRENT);

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.DAY_OF_WEEK, i + 2);
                        calendar.set(Calendar.HOUR_OF_DAY, 17);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        if (calendar.before(Calendar.getInstance()))
                            calendar.add(Calendar.DATE, 7);
                        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - (minclass * 60 * 1000), AlarmManager.INTERVAL_DAY * 7, pendingintents[i][6]);
                    }

                }
            }
        }
        if (sharedPref.getBoolean(context.getString(R.string.key_alarm_morming), false)) {
            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(Calendar.HOUR_OF_DAY, 8);
            calendar1.set(Calendar.MINUTE, 30);
            calendar1.set(Calendar.SECOND, 0);
            if (calendar1.before(Calendar.getInstance()))
                calendar1.add(Calendar.DATE, 1);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 51, new Intent(context, AlarmReciever.class).putExtra("title", "TimeTable Of Today").putExtra("fragmentno", 1), PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            am.setRepeating(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        }
    }
}
