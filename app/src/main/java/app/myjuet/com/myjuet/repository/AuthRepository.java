package app.myjuet.com.myjuet.repository;

import android.content.Context;
import android.content.SharedPreferences;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;
import javax.inject.Singleton;

import app.myjuet.com.myjuet.R;
import app.myjuet.com.myjuet.services.RefreshService;
import app.myjuet.com.myjuet.utilities.AppExecutors;
import app.myjuet.com.myjuet.utilities.Constants;

import app.myjuet.com.myjuet.utilities.SharedPreferencesUtil;
import app.myjuet.com.myjuet.utilities.webUtilities;

@Singleton
public class AuthRepository {
    private static AuthRepository mInstance;
    private AppExecutors mAppExecutors = AppExecutors.newInstance();
    Map<String, String> loginCookies = null;
    SharedPreferencesUtil mSharedPreferencesUtil;
    @Inject
    public AuthRepository(SharedPreferencesUtil sharedPreferencesUtil) {
        this.mSharedPreferencesUtil = sharedPreferencesUtil;
    }

    public static AuthRepository getInstance(){
        if (mInstance == null){
            mInstance = new AuthRepository(null);
        }
        return mInstance;
    }
    public LiveData<Constants.Status>  loginUser(Context context){
        MutableLiveData<Constants.Status> mLoginStatus = new MutableLiveData<>();
        mLoginStatus.setValue(Constants.Status.LOADING);

        String user = mSharedPreferencesUtil.getPreferences(Constants.ENROLLMENT, "").toUpperCase().trim();
        String pass = mSharedPreferencesUtil.getPreferences(Constants.PASSWORD, "");

        mAppExecutors.networkIO().execute(() -> {
            if (!webUtilities.isConnected(context)){
                mAppExecutors.mainThread().execute(()-> {
                    mLoginStatus.setValue(Constants.Status.NO_INTERNET);
                }) ;
            }
            else if (!RefreshService.pingHost(Constants.PING_HOST, 80, 5000)) {
                mAppExecutors.mainThread().execute(()-> {
                    mLoginStatus.setValue(Constants.Status.WEBKIOSK_DOWN);
                });
            }else
                try {
                    Connection.Response res = null;
                    res = Jsoup
                            .connect(Constants.LOGIN_URL)
                            .timeout(Constants.JSOUP_TIMEOUT)
                            .data("txtInst", "Institute"
                                    , "x", ""
                                    , "DOB", "DOB"
                                    , "InstCode", "JUET"
                                    , "txtuType", "Member+Type"
                                    , "UserType", "S"
                                    , "txtCode", "Enrollment+No"
                                    , "MemberCode", user
                                    , "txtPin", "Password%2FPin"
                                    , "Password", pass
                                    , "BTNSubmit", "Submit"
                                    , "BTNReset", "Reset"
                            )
                            .method(Connection.Method.POST)
                            .execute();


                    if(res.body().contains("Invalid Password")){
                        loginCookies = null;
                        mAppExecutors.mainThread().execute(() -> {
                            mLoginStatus.setValue(Constants.Status.WRONG_PASSWORD);
                        });
                    }else  if(res.body().contains("Wrong Member")){
                        loginCookies = null;
                        mAppExecutors.mainThread().execute(() -> {
                            mLoginStatus.setValue(Constants.Status.WRONG_PASSWORD);
                        });
                    }else if(res.body().contains("Login</a>")){
                        loginCookies = null;
                        mAppExecutors.mainThread().execute(() -> {
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
