package app.myjuet.com.myjuet.vm;

import android.app.Application;
import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import app.myjuet.com.myjuet.data.DateSheet;
import app.myjuet.com.myjuet.database.AppDatabase;
import app.myjuet.com.myjuet.database.DateSheetDao;
import app.myjuet.com.myjuet.repository.MasterRepo;
import app.myjuet.com.myjuet.utilities.AppExecutors;
import app.myjuet.com.myjuet.utilities.Constants;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class DateSheetViewModel extends AndroidViewModel{
    private MasterRepo mMasterRepo;
    private AppExecutors mAppExecutors;
    private DateSheetDao mDateSheetDao;
    private Context context;
    private  MutableLiveData<Constants.Status> dataStatus = new MutableLiveData<Constants.Status>();


    public DateSheetViewModel(@NonNull Application application) {
        super(application);
        mMasterRepo = MasterRepo.getInstance();
        mAppExecutors = AppExecutors.newInstance();
        mDateSheetDao = AppDatabase.newInstance(application).DateSheetDao();
        context = application;
    }

    public LiveData<Constants.Status> loadDateSheet() {
            mMasterRepo.loginUser(context).observeForever((status) -> {
                switch (status) {


                    case LOADING:
                        dataStatus.setValue(Constants.Status.LOADING);
                        break;
                    case SUCCESS:
                        callRequest();
                        break;
                    case WRONG_PASSWORD:
                    case NO_INTERNET:
                    case WEBKIOSK_DOWN:
                    case FAILED:
                        mMasterRepo.setLoginCookies(null);
                        dataStatus.postValue(Constants.Status.FAILED);
                        break;
                }
            });
        return dataStatus;
    }

    private void callRequest() {
        dataStatus.setValue(Constants.Status.LOADING);
        mAppExecutors.networkIO().execute(() ->{
            Document doc =null;
            try {
                doc = Jsoup.connect("https://webkiosk.juet.ac.in/StudentFiles/Exam/StudViewDateSheet.jsp")
                        .timeout(Constants.JSOUP_TIMEOUT)
                        .cookies(mMasterRepo.getLoginCookies())
                        .get();
                parseDateSheet(doc);


            } catch (Exception e) {
                e.printStackTrace();
                dataStatus.postValue(Constants.Status.FAILED);
            }


        });
    }

    private void parseDateSheet(Document doc) {
        mAppExecutors.diskIO().execute(() -> {
            Element table = doc.getElementById("table-1");
            if (table == null) {
                dataStatus.postValue(Constants.Status.SUCCESS);
            }else {
                Elements tbodies = table.getElementsByTag("tbody");
                if (tbodies != null && tbodies.size() > 0) {
                    Element tbody = tbodies.get(0);
                    Elements subjects = tbody.children();
                    subjects.remove(0);
                    for (Element subject : subjects) {
                        Elements columns = subject.children();
                        DateSheet dateSheet =new  DateSheet(columns);
                        mDateSheetDao.insert(dateSheet);
                    }
                }
                dataStatus.postValue(Constants.Status.SUCCESS);
            }
        });
    }


}
