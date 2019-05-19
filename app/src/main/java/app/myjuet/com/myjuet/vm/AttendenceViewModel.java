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
import app.myjuet.com.myjuet.repository.MasterRepo;
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
    MasterRepo mMasterRepo;
    public AttendenceViewModel(@NonNull Application application) {
        super(application);
        context = application;
        mMasterRepo = MasterRepo.getInstance();
        mAppExecutors = AppExecutors.newInstance();
        mAppDatabase = AppDatabase.newInstance(application);
    }

    public LiveData<Constants.Status> loginUser(){


        return mMasterRepo.loginUser(context);
    }

    public LiveData<Constants.Status> startLoading(){
        MutableLiveData<Constants.Status> dataStatus = new MutableLiveData<>();
        mAppExecutors.networkIO().execute(() -> {
            mAppDatabase.AttendenceDao().updateLoading(true);
            Document doc = null;
            try {
                doc = Jsoup.connect("https://webkiosk.juet.ac.in/StudentFiles/Academic/StudentAttendanceList.jsp")
                        .timeout(Constants.JSOUP_TIMEOUT)
                        .cookies(mMasterRepo.getLoginCookies())
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
                                .timeout(Constants.JSOUP_TIMEOUT)
                                .cookies(mMasterRepo.getLoginCookies())
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
