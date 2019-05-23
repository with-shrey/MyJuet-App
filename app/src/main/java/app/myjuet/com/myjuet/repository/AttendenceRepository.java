package app.myjuet.com.myjuet.repository;

import android.content.Context;
import android.webkit.URLUtil;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import app.myjuet.com.myjuet.data.AttendenceData;
import app.myjuet.com.myjuet.database.AttendenceDataDao;
import app.myjuet.com.myjuet.database.AttendenceDetailsDao;
import app.myjuet.com.myjuet.utilities.AppExecutors;
import app.myjuet.com.myjuet.utilities.Constants;
import app.myjuet.com.myjuet.utilities.webUtilities;
import timber.log.Timber;

@Singleton
public class AttendenceRepository {
    private final AppExecutors mExecutors;
    private final AttendenceDataDao mAttendenceDataDao;
    private final AttendenceDetailsDao mAttendenceDetailsDao;
    private final AuthRepository mAuthRepository;
    private final Context mContext;
    
    @Inject
    public AttendenceRepository(AppExecutors executors, AttendenceDetailsDao attendenceDetailsDao, AttendenceDataDao attendenceDataDao, AuthRepository authRepository, Context context) {
        this.mExecutors = executors;
        this.mAttendenceDataDao = attendenceDataDao;
        this.mAuthRepository = authRepository;
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
            statusMutableLiveData.setValue(status!= Constants.Status.SUCCESS ? status: Constants.Status.LOGGED_IN);
            if (status == Constants.Status.SUCCESS){
                this.startLoading().observeForever(status1 -> {
                    statusMutableLiveData.setValue(status1!= Constants.Status.SUCCESS ? status1: Constants.Status.LOADING);
                    if (status1 == Constants.Status.SUCCESS){
                        this.loadDetails().observeForever(statusMutableLiveData::setValue);
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
                    doc = Jsoup.connect(Constants.ATTENDENCE_LIST)
                            .timeout(Constants.JSOUP_TIMEOUT)
                            .cookies(mAuthRepository.getLoginCookies())
                            .get();
                    webUtilities.parseAttendencePage(mAttendenceDataDao, doc);


                        data.postValue(Constants.Status.SUCCESS);

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
            for (AttendenceData datum : data) {
                mExecutors.networkIO().execute(() -> {
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
                });
            }
        return dataStatus;
    }


    public LiveData<List<AttendenceData>> getAttendenceData(){
        return mAttendenceDataDao.AttendanceDataObserver();
    }
}
