package app.myjuet.com.myjuet;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.MobileAds;

import app.myjuet.com.myjuet.utilities.SharedPreferencesUtil;
import io.fabric.sdk.android.Fabric;

public class MyJuetApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-5004802474664731~4072895207");

        Fabric.with(this, new Crashlytics());
        SharedPreferencesUtil.getInstance(getApplicationContext());

    }
}
