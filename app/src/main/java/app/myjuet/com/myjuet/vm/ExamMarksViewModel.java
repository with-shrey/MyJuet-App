package app.myjuet.com.myjuet.vm;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import app.myjuet.com.myjuet.data.Exam;
import app.myjuet.com.myjuet.data.ExamMarks;
import app.myjuet.com.myjuet.database.AppDatabase;
import app.myjuet.com.myjuet.database.ExamMarksDao;
import app.myjuet.com.myjuet.repository.AuthRepository;
import app.myjuet.com.myjuet.utilities.AppExecutors;
import app.myjuet.com.myjuet.utilities.Constants;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class ExamMarksViewModel extends AndroidViewModel{
    private AuthRepository mAuthRepository;
    private AppExecutors mAppExecutors;
    private ExamMarksDao mExamMarksDao;
    private Context context;
    private  MutableLiveData<Constants.Status> dataStatus = new MutableLiveData<Constants.Status>();


    public ExamMarksViewModel(@NonNull Application application) {
        super(application);
        mAuthRepository = AuthRepository.getInstance();
        mAppExecutors = AppExecutors.newInstance();
        mExamMarksDao = AppDatabase.newInstance(application).ExamMarksDao();
        context = application;
    }

    public LiveData<Constants.Status> loadExamMarks() {
        mAuthRepository.loginUser(context).observeForever((status) -> {
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
                    mAuthRepository.setLoginCookies(null);
                    dataStatus.postValue(Constants.Status.FAILED);
                    break;
            }
        });
        return dataStatus;
    }

    private void callRequest(boolean getExamCode,String semCode) {
        dataStatus.postValue(Constants.Status.LOADING);
            Document doc =null;
            try {
                String url = "https://webkiosk.juet.ac.in/StudentFiles/Exam/StudentEventMarksView.jsp";
                if (!getExamCode){
                    url = url+"?x=&Inst=JUET&exam="+semCode;
                }
                doc = Jsoup.connect(url)
                        .timeout(Constants.JSOUP_TIMEOUT)
                        .cookies(mAuthRepository.getLoginCookies())
                        .get();
                if (getExamCode){
                    callRequest(false,extractSemCode(doc));
                }else {
                    parseExamMarks(doc);
                }

            } catch (Exception e) {
                e.printStackTrace();
                dataStatus.postValue(Constants.Status.FAILED);
            }
    }
    private String extractSemCode(Document doc){
        Element select = doc.getElementById("exam");
        if (select == null){
            dataStatus.postValue(Constants.Status.SUCCESS);
            return "";
        }else {
            Elements options = select.getElementsByTag("option");
            if (options != null && options.size() >=2) {
                return options.get(1).attr("value");
            }
        }
        return "";
    }
    private SparseArray<Exam> map = new SparseArray<>();
    private void parseExamMarks(Document doc) {
        mAppExecutors.diskIO().execute(() -> {
            Element table = doc.getElementById("table-1");
            if (table == null) {
                mExamMarksDao.deleteAll();
                dataStatus.postValue(Constants.Status.SUCCESS);
            }else {
                Elements tbodies = table.getElementsByTag("tbody");
                if (tbodies != null && tbodies.size() > 0) {
                    Elements thead = table.getElementsByTag("thead");
                    map = getMapfromHeader(
                            thead.get(0).getElementsByTag("td")
                            );
                    Element tbody = tbodies.get(0);
                    Elements subjects = tbody.children();
                    for (Element subject : subjects) {
                            Elements columns = subject.children();
                            ExamMarks examMarks = new ExamMarks(columns, map);
                            mExamMarksDao.insert(examMarks);
                    }
                }
                dataStatus.postValue(Constants.Status.SUCCESS);
            }
        });
    }

    private SparseArray<Exam> getMapfromHeader(Elements children){
        SparseArray<Exam> map = new SparseArray<Exam>();
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i).html().contains("P-1")){
                map.put(i,Exam.P1);
            }
            else if(children.get(i).html().contains("P-2")){
                map.put(i,Exam.P2);
            }else if(children.get(i).html().contains("TEST-1")){
                map.put(i,Exam.TEST1);
            }else if(children.get(i).html().contains("TEST-2")){
                map.put(i,Exam.TEST2);
            }else if(children.get(i).html().contains("TEST-3")){
                map.put(i,Exam.TEST3);
            }
        }
        Log.v("Header",map.toString());
        return  map;
    }
}
