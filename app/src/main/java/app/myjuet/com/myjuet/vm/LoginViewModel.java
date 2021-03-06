package app.myjuet.com.myjuet.vm;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

import org.jsoup.Connection;

import java.io.IOException;
import java.util.Map;

import app.myjuet.com.myjuet.R;
import app.myjuet.com.myjuet.database.AppDatabase;
import app.myjuet.com.myjuet.utilities.AppExecutors;
import app.myjuet.com.myjuet.utilities.Constants;

import static app.myjuet.com.myjuet.services.RefreshService.pingHost;
import static app.myjuet.com.myjuet.utilities.webUtilities.isConnected;
import static app.myjuet.com.myjuet.utilities.webUtilities.login;

public class LoginViewModel extends AndroidViewModel {
    AppExecutors mAppExecutors;
    AppDatabase mAppDatabase;
    Application context;
    Map<String, String> loginCookies;
    public LoginViewModel(@NonNull Application application) {
        super(application);
        context = application;
        mAppExecutors = AppExecutors.newInstance();
        mAppDatabase = AppDatabase.newInstance(application);
    }

    public LiveData<Constants.Status> loginUser(String user,String pass, String dob){
        MutableLiveData<Constants.Status> mLoginStatus = new MutableLiveData<>();
        mLoginStatus.setValue(Constants.Status.LOADING);
        SharedPreferences.Editor prefs = context.getSharedPreferences(context.getString(R.string.preferencefile), Context.MODE_PRIVATE).edit();


        mAppExecutors.networkIO().execute(() -> {
            if (!isConnected(context)){
                mAppExecutors.mainThread().execute(()-> {
                    mLoginStatus.setValue(Constants.Status.NO_INTERNET);
                }) ;
            }
            else if (new Constants(context).INST_CODE.equals("JUET") && !pingHost(new Constants(getApplication()).HOST_URL, 80, 5000)) {
                mAppExecutors.mainThread().execute(()-> {
                    mLoginStatus.setValue(Constants.Status.WEBKIOSK_DOWN);
                });
            }else
                try {
                    Pair<Connection.Response,Connection.Response> res = login(context, user, dob, pass);
                    if (res.second.body().contains("[ Signin Action ]")
                            || res.second.body().contains("Please give the correct Institute name and Enrollment No")
                            || res.second.body().contains("Invalid Password")
                            || res.second.body().contains("Wrong Member Type or Code")
                            || res.second.body().contains("Invalid Login Credentials")
                            || res.second.body().contains("NullPointerException")) {
                        loginCookies = null;
                        mAppExecutors.mainThread().execute(()-> {
                            mLoginStatus.setValue(Constants.Status.WRONG_PASSWORD);
                        });
                    }else{
                        loginCookies = res.second.cookies();
                        mAppExecutors.mainThread().execute(()->{
                            prefs.putString(context.getString(R.string.key_enrollment), user);
                            prefs.putString(context.getString(R.string.key_password), pass);
                            prefs.putString(Constants.DOB, dob);
                            prefs.putBoolean("autosync", true);
                            prefs.apply();
                            mLoginStatus.setValue(Constants.Status.SUCCESS);
                        });
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("error", "Login Error",e);
                    mAppExecutors.mainThread().execute(()-> {
                        mLoginStatus.setValue(Constants.Status.FAILED);
                    });
                }
        });


        return mLoginStatus;
    }
}
