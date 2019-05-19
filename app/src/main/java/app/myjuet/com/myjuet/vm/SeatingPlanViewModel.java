package app.myjuet.com.myjuet.vm;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import app.myjuet.com.myjuet.data.SeatingPlan;
import app.myjuet.com.myjuet.database.AppDatabase;
import app.myjuet.com.myjuet.database.SeatingPlanDao;
import app.myjuet.com.myjuet.repository.MasterRepo;
import app.myjuet.com.myjuet.utilities.AppExecutors;
import app.myjuet.com.myjuet.utilities.Constants;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class SeatingPlanViewModel extends AndroidViewModel{
    private MasterRepo mMasterRepo;
    private AppExecutors mAppExecutors;
    private SeatingPlanDao mSeatingPlanDao;
    private Context context;
    private  MutableLiveData<Constants.Status> dataStatus = new MutableLiveData<Constants.Status>();


    public SeatingPlanViewModel(@NonNull Application application) {
        super(application);
        mMasterRepo = MasterRepo.getInstance();
        mAppExecutors = AppExecutors.newInstance();
        mSeatingPlanDao = AppDatabase.newInstance(application).SeatingPlanDao();
        context = application;
    }

    public LiveData<Constants.Status> loadSeatingPlan() {
        mMasterRepo.loginUser(context).observeForever((status) -> {
            switch (status) {


                case LOADING:
                    dataStatus.setValue(Constants.Status.LOADING);
                    break;
                case SUCCESS:
                    mAppExecutors.networkIO().execute(() -> {
                        callRequest(true, null);
                    });
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

    private void callRequest(boolean getSemesterCode,String semCode) {
        dataStatus.setValue(Constants.Status.LOADING);
            Document doc =null;
            try {
                String url = "https://webkiosk.juet.ac.in/StudentFiles/Exam/StudViewSeatingPlan.jsp";
                if (!getSemesterCode){
                    url = url+"?x=&Inst=JUET&DScode="+semCode;
                }
                doc = Jsoup.connect(url)
                        .timeout(Constants.JSOUP_TIMEOUT)
                        .cookies(mMasterRepo.getLoginCookies())
                        .get();
                if (getSemesterCode){
                    callRequest(false,extractSemCode(doc));
                }else {
                    parseSeatingPlan(doc);
                }

            } catch (Exception e) {
                e.printStackTrace();
                dataStatus.postValue(Constants.Status.FAILED);
            }
    }
    private String extractSemCode(Document doc){
            Element select = doc.getElementById("DScode");
            if (select == null){
                dataStatus.postValue(Constants.Status.SUCCESS);
                return "";
            }else {
                Elements options = select.getElementsByTag("option");
                if (options != null && options.size() >0) {
                    Log.v("SEM CODE",options.get(0).attr("value"));
                    return options.get(0).attr("value");
                }
            }
        return "";
    }

    private void parseSeatingPlan(Document doc) {
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
                        SeatingPlan seatingPlan =new  SeatingPlan(columns);
                        mSeatingPlanDao.insert(seatingPlan);
                    }
                }
                dataStatus.postValue(Constants.Status.SUCCESS);
            }
        });
    }


}
