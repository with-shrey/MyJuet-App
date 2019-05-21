package app.myjuet.com.myjuet.vm;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Map;

import app.myjuet.com.myjuet.R;
import app.myjuet.com.myjuet.database.AppDatabase;
import app.myjuet.com.myjuet.utilities.AppExecutors;
import app.myjuet.com.myjuet.utilities.Constants;

import static app.myjuet.com.myjuet.services.RefreshService.pingHost;
import static app.myjuet.com.myjuet.utilities.webUtilities.isConnected;

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

    public LiveData<Constants.Status> loginUser(String user,String pass){
        MutableLiveData<Constants.Status> mLoginStatus = new MutableLiveData<>();
        mLoginStatus.setValue(Constants.Status.LOADING);
        SharedPreferences.Editor prefs = context.getSharedPreferences(context.getString(R.string.preferencefile), Context.MODE_PRIVATE).edit();


        mAppExecutors.networkIO().execute(() -> {
            if (!isConnected(context)){
                mAppExecutors.mainThread().execute(()-> {
                    mLoginStatus.setValue(Constants.Status.NO_INTERNET);
                }) ;
            }
            else if (!pingHost("webkiosk.juet.ac.in", 80, 5000)) {
                mAppExecutors.mainThread().execute(()-> {
                    mLoginStatus.setValue(Constants.Status.WEBKIOSK_DOWN);
                });
            }else
                try {
                    Connection.Response res = null;
                    res = Jsoup
                            .connect("https://webkiosk.juet.ac.in/CommonFiles/UserAction.jsp")
                            .data("txtInst", "Institute"
                                    , "InstCode", "JUET"
                                    , "txtuType", "Member+Type"
                                    , "UserType", "S"
                                    , "txtCode", "Enrollment+No"
                                    , "MemberCode", user
                                    , "txtPin", "Password%2FPin"
                                    , "Password", pass
                                    , "BTNSubmit", "Submit"
                            )
                            .method(Connection.Method.POST)
                            .execute();


                    if(!res.body().contains("Invalid Password")){
                        loginCookies = res.cookies();
                        mAppExecutors.mainThread().execute(()->{
                            prefs.putString(context.getString(R.string.key_enrollment), user);
                            prefs.putString(context.getString(R.string.key_password), pass);
                            prefs.putBoolean("autosync", true);
                            prefs.apply();
                            mLoginStatus.setValue(Constants.Status.SUCCESS);
                        });
                    }else{
                        loginCookies = null;
                        mAppExecutors.mainThread().execute(()-> {
                            mLoginStatus.setValue(Constants.Status.WRONG_PASSWORD);
                        });
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    mAppExecutors.mainThread().execute(()-> {
                        mLoginStatus.setValue(Constants.Status.FAILED);
                    });
                }
        });


        return mLoginStatus;
    }
}
