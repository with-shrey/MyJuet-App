package app.myjuet.com.myjuet.repository;

import android.content.Context;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.inject.Singleton;

import app.myjuet.com.myjuet.data.AttendenceData;
import app.myjuet.com.myjuet.database.AttendenceDataDao;
import app.myjuet.com.myjuet.database.AttendenceDetailsDao;
import app.myjuet.com.myjuet.database.DateSheetDao;
import app.myjuet.com.myjuet.database.ExamMarksDao;
import app.myjuet.com.myjuet.database.SeatingPlanDao;
import app.myjuet.com.myjuet.utilities.AppExecutors;
import app.myjuet.com.myjuet.utilities.Constants;
import app.myjuet.com.myjuet.utilities.SharedPreferencesUtil;
import app.myjuet.com.myjuet.utilities.webUtilities;
import timber.log.Timber;

import static app.myjuet.com.myjuet.utilities.Constants.CURRENT_SEMESTER;

@Singleton
public class AttendenceRepository {
    private final AppExecutors mExecutors;
    private final AttendenceDataDao mAttendenceDataDao;
    private final AttendenceDetailsDao mAttendenceDetailsDao;
    private final DateSheetDao mDateSheetDao;
    private final ExamMarksDao mExamMarksDao;
    private final SeatingPlanDao mSeatingPlanDao;
    private final AuthRepository mAuthRepository;
    private final Context mContext;
    
    @Inject
    public AttendenceRepository(AppExecutors executors,
                                AttendenceDetailsDao attendenceDetailsDao,
                                AttendenceDataDao attendenceDataDao,
                                DateSheetDao dateSheetDao,
                                ExamMarksDao examMarksDao,
                                SeatingPlanDao seatingPlanDao,
                                AuthRepository authRepository,
                                Context context) {
        this.mExecutors = executors;
        this.mAttendenceDataDao = attendenceDataDao;
        this.mAuthRepository = authRepository;
        this.mDateSheetDao = dateSheetDao;
        this.mSeatingPlanDao =seatingPlanDao;
        this.mExamMarksDao = examMarksDao;
        this.mContext = context;
        this.mAttendenceDetailsDao = attendenceDetailsDao;
    }
    public LiveData<Boolean> checkLoginStatus(Constants.Status status){
        MutableLiveData<Boolean> mShouldLoad = new MutableLiveData<>();
        switch (status){

            case LOADING:
                break;
            case SUCCESS:
                break;
            case WRONG_PASSWORD:
                break;
            case NO_INTERNET:
                break;
            case WEBKIOSK_DOWN:
                break;
            case FAILED:
                break;
        }
        return mShouldLoad;
    }
    public LiveData<Constants.Status> loadData(){
        MutableLiveData<Constants.Status> statusMutableLiveData = new MutableLiveData<>();
        mAuthRepository.loginUser(mContext).observeForever(status -> {
            statusMutableLiveData.postValue(status!= Constants.Status.SUCCESS ? status: Constants.Status.LOGGED_IN);
            if (status == Constants.Status.SUCCESS){
                this.startLoading().observeForever(status1 -> {
                    statusMutableLiveData.postValue(status1!= Constants.Status.SUCCESS ? status1: Constants.Status.LOADING);
                    if (status1 == Constants.Status.SUCCESS){
                        this.loadDetails().observeForever(statusMutableLiveData::postValue);
                    }
                });
            }
        });
        return statusMutableLiveData;
    }

    public LiveData<Constants.Status> startLoading(){
        MutableLiveData<Constants.Status> data = new MutableLiveData<>();

            mExecutors.networkIO().execute(() -> {
                mAttendenceDataDao.updateLoading(true);
                Document doc = null;
                try {
                    Connection connection = Jsoup
                            .connect(Constants.ATTENDENCE_LIST)
                            .timeout(Constants.JSOUP_TIMEOUT)
                            .cookies(mAuthRepository.getLoginCookies())
                            .method(Connection.Method.GET);
                            doc  = connection.execute().parse();
                    int oldSem  = SharedPreferencesUtil.getInstance(mContext)
                            .getPreferences(CURRENT_SEMESTER, -1);
                    String body = doc.body().text();
                    int index = body.indexOf("Current Semester:");
                    if (index > -1) {
                        body = body.substring(index+18, index+19);
                        int currentSem = Integer.parseInt(body);
                        body = null;
                        if (currentSem != oldSem){
                            SharedPreferencesUtil.getInstance(mContext)
                                    .savePreferences(CURRENT_SEMESTER, currentSem);
                            mAttendenceDataDao.clearData();
                            mAttendenceDetailsDao.clearData();
                            mExamMarksDao.deleteAll();
                            mSeatingPlanDao.deleteAll();
                            mDateSheetDao.deleteAll();
                        }
                        webUtilities.parseAttendencePage(mAttendenceDataDao, doc);
                        data.postValue(Constants.Status.SUCCESS);
                    }else {
                        data.postValue(Constants.Status.FAILED);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                        data.postValue(Constants.Status.FAILED);
                }

            });
        return data;
    }

    public  LiveData<Constants.Status> loadDetails(){
//        mAppDatabase.AttendenceDao().updateLoading(true);
        List<AttendenceData> data = mAttendenceDataDao.AttendanceData();
        MutableLiveData<Constants.Status> dataStatus = new MutableLiveData<>();
        List<Future<?>> list = new ArrayList<>();
            for (AttendenceData datum : data) {
                ExecutorService executorService = (ExecutorService) mExecutors.networkIO();
                list.add(executorService.submit(() -> {
                    try {

                        Document doc = null;
                        if (URLUtil.isValidUrl(datum.getSubjectUrl())) {

                            doc = Jsoup.connect(datum.getSubjectUrl())
                                    .timeout(Constants.JSOUP_TIMEOUT)
                                    .cookies(mAuthRepository.getLoginCookies())
                                    .get();

                            webUtilities.parseAttendenceDetails(datum, mAttendenceDataDao, mAttendenceDetailsDao, doc);
                        }
                        mAttendenceDataDao.updateLoading(datum.getId(), false);

                    } catch (IOException e) {
                        e.printStackTrace();
                        mAttendenceDataDao.updateLoading(false);
                        dataStatus.postValue(Constants.Status.FAILED);

                    }
                }));
            }
        mExecutors.diskIO().execute(() -> {
            for (Future<?> future : list) {
                try {
                    future.get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        dataStatus.postValue(Constants.Status.SUCCESS);
        return dataStatus;
    }


    public LiveData<List<AttendenceData>> getAttendenceData(){
        return mAttendenceDataDao.AttendanceDataObserver();
    }
}
