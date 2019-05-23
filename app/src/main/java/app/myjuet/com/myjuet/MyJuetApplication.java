package app.myjuet.com.myjuet;

import android.app.Activity;
import android.app.Application;
import android.app.Service;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.MobileAds;

import javax.inject.Inject;

import app.myjuet.com.myjuet.di.AppInjector;
import app.myjuet.com.myjuet.utilities.SharedPreferencesUtil;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasServiceInjector;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class MyJuetApplication extends Application implements HasActivityInjector, HasServiceInjector {
    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;
    @Inject
    DispatchingAndroidInjector<Service> dispatchingServiceAndroidInjector;
    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-5004802474664731~4072895207");
        AppInjector.init(this);
        Fabric.with(this, new Crashlytics());
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }

    @Override
    public AndroidInjector<Service> serviceInjector() {
        return dispatchingServiceAndroidInjector;
    }
}
