package app.myjuet.com.myjuet;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

import app.myjuet.com.myjuet.utilities.SharedPreferencesUtil;
import io.fabric.sdk.android.Fabric;

public class MyJuetApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        SharedPreferencesUtil.getInstance(getApplicationContext());

    }
}
