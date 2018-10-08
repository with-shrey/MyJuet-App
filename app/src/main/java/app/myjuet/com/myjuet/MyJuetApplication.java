package app.myjuet.com.myjuet;

import android.app.Application;

import app.myjuet.com.myjuet.utilities.SharedPreferencesUtil;

public class MyJuetApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferencesUtil.getInstance(getApplicationContext());
    }
}
