package app.myjuet.com.myjuet.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.URLUtil;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import app.myjuet.com.myjuet.R;
import app.myjuet.com.myjuet.data.AttendenceData;
import app.myjuet.com.myjuet.data.AttendenceDetails;
import app.myjuet.com.myjuet.database.AppDatabase;
import app.myjuet.com.myjuet.utilities.AppExecutors;
import app.myjuet.com.myjuet.utilities.webUtilities;

import static app.myjuet.com.myjuet.services.RefreshService.pingHost;
import static app.myjuet.com.myjuet.utilities.webUtilities.isConnected;

public class AttendenceViewModel extends AndroidViewModel {
    AppExecutors mAppExecutors;
    AppDatabase mAppDatabase;
    Application context;
    Map<String, String> loginCookies;
    public static enum Status{LOADING,SUCCESS ,WRONG_PASSWORD, NO_INTERNET,WEBKIOSK_DOWN ,FAILED}
    public AttendenceViewModel(@NonNull Application application) {
        super(application);
        context = application;
        mAppExecutors = AppExecutors.newInstance();
        mAppDatabase = AppDatabase.newInstance(application);
    }

    public LiveData<Status> loginUser(){
        MutableLiveData<Status> mLoginStatus = new MutableLiveData<>();
        mLoginStatus.setValue(Status.LOADING);
        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.preferencefile), Context.MODE_PRIVATE);
        String user = prefs.getString(context.getString(R.string.key_enrollment), "").toUpperCase().trim();
        String pass = prefs.getString(context.getString(R.string.key_password), "");

        mAppExecutors.networkIO().execute(() -> {
            if (!isConnected(context)){
                mAppExecutors.mainThread().execute(()-> {
                    mLoginStatus.setValue(Status.NO_INTERNET);
                }) ;
            }
           else if (!pingHost("webkiosk.juet.ac.in", 80, 5000)) {
                mAppExecutors.mainThread().execute(()-> {
                    mLoginStatus.setValue(Status.WEBKIOSK_DOWN);
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
                        mLoginStatus.setValue(Status.SUCCESS);
                    });
                }else{
                    loginCookies = null;
                    mAppExecutors.mainThread().execute(()-> {
                        mLoginStatus.setValue(Status.WRONG_PASSWORD);
                    });
                }

            } catch (IOException e) {
                e.printStackTrace();
                mAppExecutors.mainThread().execute(()-> {
                    mLoginStatus.setValue(Status.FAILED);
                });
            }
        });


        return mLoginStatus;
    }

    public LiveData<Status> startLoading(){
        MutableLiveData<Status> dataStatus = new MutableLiveData<>();
        mAppExecutors.networkIO().execute(() -> {
            mAppDatabase.AttendenceDao().updateLoading(true);
            Document doc = null;
            try {
                doc = Jsoup.connect("https://webkiosk.juet.ac.in/StudentFiles/Academic/StudentAttendanceList.jsp")
                        .cookies(loginCookies)
                        .get();
                webUtilities.parseAttendencePage(mAppDatabase,doc);
                mAppExecutors.mainThread().execute(()-> {
                    dataStatus.setValue(Status.SUCCESS);
                });

            } catch (IOException e) {
                e.printStackTrace();
                mAppExecutors.mainThread().execute(()-> {
                    dataStatus.setValue(Status.FAILED);
                });
            }

        });
        return dataStatus;
    }

    public  LiveData<Status> loadDetails(){
//        mAppDatabase.AttendenceDao().updateLoading(true);
        List<AttendenceData> data = mAppDatabase.AttendenceDao().AttendanceData();
        MutableLiveData<Status> dataStatus = new MutableLiveData<>();
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
                        dataStatus.setValue(Status.FAILED);
                    });

                }
            });
        }
        return dataStatus;
    }


}
