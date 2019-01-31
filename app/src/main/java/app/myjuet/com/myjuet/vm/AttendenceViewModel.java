package app.myjuet.com.myjuet.vm;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import android.webkit.URLUtil;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.myjuet.com.myjuet.R;
import app.myjuet.com.myjuet.data.AttendenceData;
import app.myjuet.com.myjuet.database.AppDatabase;
import app.myjuet.com.myjuet.utilities.AppExecutors;
import app.myjuet.com.myjuet.utilities.Constants;
import app.myjuet.com.myjuet.utilities.webUtilities;

import static app.myjuet.com.myjuet.services.RefreshService.pingHost;
import static app.myjuet.com.myjuet.utilities.webUtilities.isConnected;

public class AttendenceViewModel extends AndroidViewModel {
    AppExecutors mAppExecutors;
    AppDatabase mAppDatabase;
    Application context;
    Map<String, String> loginCookies;

    public AttendenceViewModel(@NonNull Application application) {
        super(application);
        context = application;
        mAppExecutors = AppExecutors.newInstance();
        mAppDatabase = AppDatabase.newInstance(application);
    }

    public LiveData<Constants.Status> loginUser(){
        MutableLiveData<Constants.Status> mLoginStatus = new MutableLiveData<>();
        mLoginStatus.setValue(Constants.Status.LOADING);
        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.preferencefile), Context.MODE_PRIVATE);
        String user = prefs.getString(context.getString(R.string.key_enrollment), "").toUpperCase().trim();
        String pass = prefs.getString(context.getString(R.string.key_password), "");

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

    public LiveData<Constants.Status> startLoading(){
        MutableLiveData<Constants.Status> dataStatus = new MutableLiveData<>();
        mAppExecutors.networkIO().execute(() -> {
            mAppDatabase.AttendenceDao().updateLoading(true);
            Document doc = null;
            try {
                doc = Jsoup.connect("https://webkiosk.juet.ac.in/StudentFiles/Academic/StudentAttendanceList.jsp")
                        .cookies(loginCookies != null  ? loginCookies : new HashMap<>())
                        .get();
                webUtilities.parseAttendencePage(mAppDatabase,doc);
                mAppExecutors.mainThread().execute(()-> {
                    dataStatus.setValue(Constants.Status.SUCCESS);
                });

            } catch (IOException e) {
                e.printStackTrace();
                mAppExecutors.mainThread().execute(()-> {
                    dataStatus.setValue(Constants.Status.FAILED);
                });
            }

        });
        return dataStatus;
    }

    public  LiveData<Constants.Status> loadDetails(){
//        mAppDatabase.AttendenceDao().updateLoading(true);
        List<AttendenceData> data = mAppDatabase.AttendenceDao().AttendanceData();
        MutableLiveData<Constants.Status> dataStatus = new MutableLiveData<>();
        for (AttendenceData datum : data) {
            mAppExecutors.networkIO().execute(() -> {
                try {

                    Document doc = null;
                    if (URLUtil.isValidUrl(datum.getSubjectUrl())) {

                        doc = Jsoup.connect(datum.getSubjectUrl())
                                .cookies(loginCookies)
                                .get();

                        webUtilities.parseAttendenceDetails(datum,mAppDatabase,doc);
                    }
                    mAppDatabase.AttendenceDao().updateLoading(datum.getId(), false);

                } catch (IOException e) {
                    e.printStackTrace();
                    mAppDatabase.AttendenceDao().updateLoading(false);
                    mAppExecutors.mainThread().execute(() -> {
                        dataStatus.setValue(Constants.Status.FAILED);
                    });

                }
            });
        }
        return dataStatus;
    }


}
