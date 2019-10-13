package app.myjuet.com.myjuet.vm;

import android.app.Application;
import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import app.myjuet.com.myjuet.data.SeatingPlan;
import app.myjuet.com.myjuet.database.AppDatabase;
import app.myjuet.com.myjuet.database.SeatingPlanDao;
import app.myjuet.com.myjuet.repository.AuthRepository;
import app.myjuet.com.myjuet.utilities.AppExecutors;
import app.myjuet.com.myjuet.utilities.Constants;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class SeatingPlanViewModel extends AndroidViewModel{
    private AuthRepository mAuthRepository;
    private AppExecutors mAppExecutors;
    private SeatingPlanDao mSeatingPlanDao;
    private Context context;
    private  MutableLiveData<Constants.Status> dataStatus = new MutableLiveData<Constants.Status>();


    public SeatingPlanViewModel(@NonNull Application application) {
        super(application);
        mAuthRepository = AuthRepository.getInstance();
        mAppExecutors = AppExecutors.newInstance();
        mSeatingPlanDao = AppDatabase.newInstance(application).SeatingPlanDao();
        context = application;
    }

    public LiveData<Constants.Status> loadSeatingPlan() {
        mAuthRepository.loginUser(context).observeForever((status) -> {
            switch (status) {


                case LOADING:
                    dataStatus.setValue(Constants.Status.LOADING);
                    break;
                case SUCCESS:
                    mAppExecutors.networkIO().execute(() -> {
                        callRequest();
                    });
                    break;
                case WRONG_PASSWORD:
                case NO_INTERNET:
                case WEBKIOSK_DOWN:
                case FAILED:
                    mAuthRepository.setLoginCookies(null);
                    dataStatus.postValue(Constants.Status.FAILED);
                    break;
            }
        });
        return dataStatus;
    }

    private void callRequest() {
        dataStatus.postValue(Constants.Status.LOADING);
            Document doc =null;
            try {
                String url = "https://webkiosk.juet.ac.in/StudentFiles/Exam/StudViewSeatPlan.jsp";
                doc = Jsoup.connect(url)
                        .timeout(Constants.JSOUP_TIMEOUT)
                        .cookies(mAuthRepository.getLoginCookies())
                        .get();
                    parseSeatingPlan(doc);
            } catch (Exception e) {
                e.printStackTrace();
                dataStatus.postValue(Constants.Status.FAILED);
            }
    }

    private void parseSeatingPlan(Document doc) {
        mAppExecutors.diskIO().execute(() -> {
            Element table = doc.getElementById("table-1");
            mSeatingPlanDao.deleteAll();
            if (table == null) {
                dataStatus.postValue(Constants.Status.SUCCESS);
            }else {
                Elements tbodies = table.getElementsByTag("tbody");
                if (tbodies != null && tbodies.size() > 0) {
                    Element tbody = tbodies.get(0);
                    Elements subjects = tbody.children();
                    for (int i = 0; i < subjects.size(); i++) {
                        Elements dateColumns = subjects.get(i).children();
                        Elements seatColumns = subjects.get(i+2).children();
                        SeatingPlan seatingPlan =new  SeatingPlan(dateColumns,seatColumns);
                        mSeatingPlanDao.insert(seatingPlan);
                        i=i+2;
                    }
                }
                dataStatus.postValue(Constants.Status.SUCCESS);
            }
        });
    }


}
