package app.myjuet.com.myjuet;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Calendar;

import app.myjuet.com.myjuet.data.AttendenceData;
import app.myjuet.com.myjuet.data.TimeTableData;
import app.myjuet.com.myjuet.timetable.TableSettingsActivity;

import static android.content.Context.ALARM_SERVICE;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static java.lang.reflect.Array.getInt;

/**
 * Created by Shrey on 14-Apr-17.
 */

public class BootReciever extends BroadcastReceiver {
    ArrayList<TimeTableData> datatt;
    ArrayList<AttendenceData> attendenceDatas;


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("Alarms", "Set success");

        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preferencefile), Context.MODE_PRIVATE);
        Calendar calender4 = Calendar.getInstance();
        calender4.set(Calendar.HOUR_OF_DAY, 0);
        calender4.set(Calendar.MINUTE, 1);
        calender4.set(Calendar.SECOND, 0);
        PendingIntent pendingIntent3 = PendingIntent.getBroadcast(context, 56, new Intent(context, AlarmReciever.class).putExtra("title", "app"), PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am3 = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am3.setRepeating(AlarmManager.RTC_WAKEUP, calender4.getTimeInMillis(), AlarmManager.INTERVAL_HALF_DAY / 4, pendingIntent3);


        if (sharedPref.getBoolean(context.getString(R.string.key_alarm_meal), false)) {
            Calendar calender1 = Calendar.getInstance();
            calender1.set(Calendar.HOUR_OF_DAY, 7);
            calender1.set(Calendar.MINUTE, 30);
            calender1.set(Calendar.SECOND, 0);

            Calendar calender2 = Calendar.getInstance();
            calender2.set(Calendar.HOUR_OF_DAY, 19);
            calender2.set(Calendar.MINUTE, 30);
            calender2.set(Calendar.SECOND, 0);

            Calendar calender3 = Calendar.getInstance();
            calender3.set(Calendar.HOUR_OF_DAY, 13);
            calender3.set(Calendar.MINUTE, 0);
            calender3.set(Calendar.SECOND, 0);
            int minmeal = sharedPref.getInt(context.getString(R.string.key_minutes_before_meal), 15);


            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 48, new Intent(context, AlarmReciever.class).putExtra("title", "BreakFast Time").putExtra("fragmentno", 2), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, 49, new Intent(context, AlarmReciever.class).putExtra("title", "Dinner Time").putExtra("fragmentno", 2), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, 50, new Intent(context, AlarmReciever.class).putExtra("title", "Lunch Time").putExtra("fragmentno", 2), PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            am.setRepeating(AlarmManager.RTC_WAKEUP, calender1.getTimeInMillis() - (minmeal * 60 * 1000), AlarmManager.INTERVAL_DAY, pendingIntent);
            AlarmManager am1 = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            am1.setRepeating(AlarmManager.RTC_WAKEUP, calender2.getTimeInMillis() - (minmeal * 60 * 1000), AlarmManager.INTERVAL_DAY, pendingIntent1);
            AlarmManager am2 = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            am2.setRepeating(AlarmManager.RTC_WAKEUP, calender3.getTimeInMillis() - (minmeal * 60 * 1000), AlarmManager.INTERVAL_DAY, pendingIntent2);
        }
        if (sharedPref.getBoolean(context.getString(R.string.key_alarm_class), false)) {
            SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.preferencefile), Context.MODE_PRIVATE);
            final String batch = prefs.getString(context.getString(R.string.key_batch), "").toUpperCase().trim();
            final String sem = prefs.getString(context.getString(R.string.key_semester), "").toUpperCase().trim();
            File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File settings = null;
            settings = new File(storageDir, sem + "_" + batch + ".txt");
            ObjectInput ois = null;

            try {
                ois = new ObjectInputStream(new FileInputStream(settings));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                datatt = (ArrayList<TimeTableData>) ois.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                attendenceDatas = AttendenceActivity.read(context);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!datatt.isEmpty() && !attendenceDatas.isEmpty()) {
                int minclass = sharedPref.getInt(context.getString(R.string.key_minutes_before_class), 15);
                PendingIntent[][] pendingintents = new PendingIntent[6][7];
                for (int i = 0; i < 6; i++) {
                    TimeTableData tempData = new TimeTableData();
                    tempData = datatt.get(i);

                    if (tempData.getPosNine() == 0 && !attendenceDatas.get(tempData.getPosNine()).getmName().contains("LAB") && tempData.getPosTen() != 0) {
                        pendingintents[i][0] = PendingIntent.getBroadcast(context, 0 + (i), new Intent(context, AlarmReciever.class).putExtra("title", "Class @ 10:00").putExtra("fragmentno", 1), PendingIntent.FLAG_UPDATE_CURRENT);
                        Calendar calender = Calendar.getInstance();
                        calender.set(Calendar.DAY_OF_WEEK, i + 2);
                        calender.set(Calendar.HOUR_OF_DAY, 10);
                        calender.set(Calendar.MINUTE, 0);
                        calender.set(Calendar.SECOND, 0);
                        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                        am.setRepeating(AlarmManager.RTC_WAKEUP, calender.getTimeInMillis() - (minclass * 60 * 1000), AlarmManager.INTERVAL_DAY * 7, pendingintents[i][0]);
                    }
                    if (tempData.getPosTen() == 0 && !attendenceDatas.get(tempData.getPosTen()).getmName().contains("LAB") && tempData.getPosEleven() != 0) {
                        pendingintents[i][1] = PendingIntent.getBroadcast(context, 6 + i, new Intent(context, AlarmReciever.class).putExtra("title", "Class @ 11:00").putExtra("fragmentno", 1), PendingIntent.FLAG_UPDATE_CURRENT);
                        Calendar calender = Calendar.getInstance();
                        calender.set(Calendar.DAY_OF_WEEK, i + 2);
                        calender.set(Calendar.HOUR_OF_DAY, 11);
                        calender.set(Calendar.MINUTE, 0);
                        calender.set(Calendar.SECOND, 0);
                        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                        am.setRepeating(AlarmManager.RTC_WAKEUP, calender.getTimeInMillis() - (minclass * 60 * 1000), AlarmManager.INTERVAL_DAY * 7, pendingintents[i][1]);
                    }
                    if (tempData.getPosEleven() == 0 && !attendenceDatas.get(tempData.getPosEleven()).getmName().contains("LAB") && tempData.getPosTwelve() != 0) {
                        pendingintents[i][2] = PendingIntent.getBroadcast(context, 13 + i, new Intent(context, AlarmReciever.class).putExtra("title", "Class @ 12:00").putExtra("fragmentno", 1), PendingIntent.FLAG_UPDATE_CURRENT);

                        Calendar calender = Calendar.getInstance();
                        calender.set(Calendar.DAY_OF_WEEK, i + 2);
                        calender.set(Calendar.HOUR_OF_DAY, 12);
                        calender.set(Calendar.MINUTE, 0);
                        calender.set(Calendar.SECOND, 0);
                        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                        am.setRepeating(AlarmManager.RTC_WAKEUP, calender.getTimeInMillis() - (minclass * 60 * 1000), AlarmManager.INTERVAL_DAY * 7, pendingintents[i][2]);
                    }
                    if (tempData.getPosTwo() != 0) {
                        pendingintents[i][3] = PendingIntent.getBroadcast(context, 20 + i, new Intent(context, AlarmReciever.class).putExtra("title", "Class @ 02:00").putExtra("fragmentno", 1), PendingIntent.FLAG_UPDATE_CURRENT);
                        Calendar calender = Calendar.getInstance();
                        calender.set(Calendar.DAY_OF_WEEK, i + 2);
                        calender.set(Calendar.HOUR_OF_DAY, 14);
                        calender.set(Calendar.MINUTE, 0);
                        calender.set(Calendar.SECOND, 0);
                        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                        am.setRepeating(AlarmManager.RTC_WAKEUP, calender.getTimeInMillis() - (minclass * 60 * 1000), AlarmManager.INTERVAL_DAY * 7, pendingintents[i][3]);
                    }
                    if (tempData.getPosTwo() == 0 && !attendenceDatas.get(tempData.getPosTwo()).getmName().contains("LAB") && tempData.getPosThree() != 0) {
                        pendingintents[i][4] = PendingIntent.getBroadcast(context, 27 + i, new Intent(context, AlarmReciever.class).putExtra("title", "Class @ 03:00").putExtra("fragmentno", 1), PendingIntent.FLAG_UPDATE_CURRENT);

                        Calendar calender = Calendar.getInstance();
                        calender.set(Calendar.DAY_OF_WEEK, i + 2);
                        calender.set(Calendar.HOUR_OF_DAY, 15);
                        calender.set(Calendar.MINUTE, 0);
                        calender.set(Calendar.SECOND, 0);
                        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                        am.setRepeating(AlarmManager.RTC_WAKEUP, calender.getTimeInMillis() - (minclass * 60 * 1000), AlarmManager.INTERVAL_DAY * 7, pendingintents[i][4]);
                    }
                    if (tempData.getPosThree() == 0 && !attendenceDatas.get(tempData.getPosThree()).getmName().contains("LAB") && tempData.getPosFour() != 0) {
                        pendingintents[i][5] = PendingIntent.getBroadcast(context, 34 + i, new Intent(context, AlarmReciever.class).putExtra("title", "Class @ 04:00").putExtra("fragmentno", 1), PendingIntent.FLAG_UPDATE_CURRENT);

                        Calendar calender = Calendar.getInstance();
                        calender.set(Calendar.DAY_OF_WEEK, i + 2);
                        calender.set(Calendar.HOUR_OF_DAY, 16);
                        calender.set(Calendar.MINUTE, 0);
                        calender.set(Calendar.SECOND, 0);
                        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                        am.setRepeating(AlarmManager.RTC_WAKEUP, calender.getTimeInMillis() - (minclass * 60 * 1000), AlarmManager.INTERVAL_DAY * 7, pendingintents[i][5]);
                    }
                    if (tempData.getPosFour() == 0 && !attendenceDatas.get(tempData.getPosFour()).getmName().contains("LAB") && tempData.getPosFive() != 0) {
                        pendingintents[i][6] = PendingIntent.getBroadcast(context, 41 + i, new Intent(context, AlarmReciever.class).putExtra("title", "Class @ 05:00" + String.valueOf(i)).putExtra("fragmentno", 1), PendingIntent.FLAG_UPDATE_CURRENT);

                        Calendar calender = Calendar.getInstance();
                        calender.set(Calendar.DAY_OF_WEEK, i + 2);
                        calender.set(Calendar.HOUR_OF_DAY, 17);
                        calender.set(Calendar.MINUTE, 0);
                        calender.set(Calendar.SECOND, 0);
                        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                        am.setRepeating(AlarmManager.RTC_WAKEUP, calender.getTimeInMillis() - (minclass * 60 * 1000), AlarmManager.INTERVAL_DAY * 7, pendingintents[i][6]);
                    }

                }
            }
        }
        if (sharedPref.getBoolean(context.getString(R.string.key_alarm_morming), false)) {
            Calendar calender1 = Calendar.getInstance();
            calender1.set(Calendar.HOUR_OF_DAY, 8);
            calender1.set(Calendar.MINUTE, 30);
            calender1.set(Calendar.SECOND, 0);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 51, new Intent(context, AlarmReciever.class).putExtra("title", "TimeTable Of Today").putExtra("fragmentno", 1), PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            am.setRepeating(AlarmManager.RTC_WAKEUP, calender1.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        }
    }
}
