package app.myjuet.com.myjuet.repository;

import android.content.Context;
import android.content.SharedPreferences;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import app.myjuet.com.myjuet.R;
import app.myjuet.com.myjuet.services.RefreshService;
import app.myjuet.com.myjuet.utilities.AppExecutors;
import app.myjuet.com.myjuet.utilities.Constants;

import app.myjuet.com.myjuet.utilities.webUtilities;

public class MasterRepo {
    private static MasterRepo mInstance;
    private AppExecutors mAppExecutors = AppExecutors.newInstance();
    Map<String, String> loginCookies = null;

    public static MasterRepo getInstance(){
        if (mInstance == null){
            mInstance = new MasterRepo();
        }
        return mInstance;
    }
    public LiveData<Constants.Status>  loginUser(Context context){
        MutableLiveData<Constants.Status> mLoginStatus = new MutableLiveData<>();
        mLoginStatus.setValue(Constants.Status.LOADING);
        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.preferencefile), Context.MODE_PRIVATE);
        String user = prefs.getString(context.getString(R.string.key_enrollment), "").toUpperCase().trim();
        String pass = prefs.getString(context.getString(R.string.key_password), "");

        mAppExecutors.networkIO().execute(() -> {
            if (!webUtilities.isConnected(context)){
                mAppExecutors.mainThread().execute(()-> {
                    mLoginStatus.setValue(Constants.Status.NO_INTERNET);
                }) ;
            }
            else if (!RefreshService.pingHost("webkiosk.juet.ac.in", 80, 5000)) {
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


                    if(res.body().contains("Invalid Password")){
                        loginCookies = null;
                        mAppExecutors.mainThread().execute(()-> {
                            mLoginStatus.setValue(Constants.Status.WRONG_PASSWORD);
                        });
                    }else  if(res.body().contains("Wrong Member")){
                        loginCookies = null;
                        mAppExecutors.mainThread().execute(()-> {
                            mLoginStatus.setValue(Constants.Status.WRONG_PASSWORD);
                        });
                    }else if(res.body().contains("Login</a>")){
                        loginCookies = null;
                        mAppExecutors.mainThread().execute(()-> {
                            mLoginStatus.setValue(Constants.Status.WRONG_PASSWORD);
                        });
                    }else{
                        loginCookies = res.cookies();
                        mAppExecutors.mainThread().execute(()->{
                            mLoginStatus.setValue(Constants.Status.SUCCESS);
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

    public Map<String, String> getLoginCookies() {
        return loginCookies;
    }

    public void setLoginCookies(Map<String, String> loginCookies) {
        this.loginCookies = loginCookies;
    }
}
