package app.myjuet.com.myjuet;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Shrey on 14-Apr-17.
 */

public class BootReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("Alarms", "Set success");
        Calendar calender1 = Calendar.getInstance();
        calender1.set(Calendar.HOUR_OF_DAY, 7);
        calender1.set(Calendar.MINUTE, 25);
        calender1.set(Calendar.SECOND, 0);

        Calendar calender2 = Calendar.getInstance();
        calender2.set(Calendar.HOUR_OF_DAY, 19);
        calender2.set(Calendar.MINUTE, 25);
        calender2.set(Calendar.SECOND, 0);

        Calendar calender3 = Calendar.getInstance();
        calender3.set(Calendar.HOUR_OF_DAY, 12);
        calender3.set(Calendar.MINUTE, 50);
        calender3.set(Calendar.SECOND, 0);

        Calendar calender4 = Calendar.getInstance();
        calender4.set(Calendar.HOUR_OF_DAY, 0);
        calender4.set(Calendar.MINUTE, 1);
        calender4.set(Calendar.SECOND, 0);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, AlarmReciever.class).putExtra("title", "BreakFast Time"), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, 1, new Intent(context, AlarmReciever.class).putExtra("title", "Dinner Time"), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, 2, new Intent(context, AlarmReciever.class).putExtra("title", "Lunch Time"), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent3 = PendingIntent.getBroadcast(context, 3, new Intent(context, AlarmReciever.class).putExtra("title", "app"), PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calender1.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        AlarmManager am1 = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am1.setRepeating(AlarmManager.RTC_WAKEUP, calender2.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent1);
        AlarmManager am2 = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am2.setRepeating(AlarmManager.RTC_WAKEUP, calender3.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent2);
        AlarmManager am3 = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am3.setRepeating(AlarmManager.RTC_WAKEUP, calender4.getTimeInMillis(), AlarmManager.INTERVAL_HALF_DAY / 4, pendingIntent3);
    }
}
