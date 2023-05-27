package app.myjuet.com.myjuet.vm;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import org.intellij.lang.annotations.RegExp;
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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
                String url = new Constants(context).EXAM_MARKS;
                if (!getExamCode){
                    url = url+"?x=&Inst="+new Constants(context).INST_CODE+"&exam="+semCode;
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
    private Map<Exam, Integer> _totalMarks = new HashMap<>();
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
                    _totalMarks = getTotalMarks(thead.get(0).getElementsByTag("td"));
                    Element tbody = tbodies.get(0);
                    Elements subjects = tbody.children();
                    for (Element subject : subjects) {
                        Elements columns = subject.children();
                        ExamMarks examMarks = new ExamMarks(columns, map, _totalMarks);
                        Log.v("marks", examMarks.toString());
                        mExamMarksDao.insert(examMarks);
                    }
                }
                dataStatus.postValue(Constants.Status.SUCCESS);
            }
        });
    }

    private SparseArray<Exam> getMapfromHeader(Elements children){
        SparseArray<Exam> map = new SparseArray<>();
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i).html().contains("P-1")){
                map.put(i,Exam.P1);
            }
            else if(children.get(i).html().contains("P-2")){
                map.put(i,Exam.P2);
            }else if(children.get(i).html().contains("TEST-1")){
                map.put(i,Exam.TEST1);
            }else if(children.get(i).html().contains("T-1")){
                map.put(i,Exam.T1);
            }else if(children.get(i).html().contains("TEST-2")){
                map.put(i,Exam.TEST2);
            } else if (children.get(i).html().contains("T-2")) {
                map.put(i, Exam.T2);
            } else if (children.get(i).html().contains("TEST-3")) {
                map.put(i, Exam.TEST3);
            } else if (children.get(i).html().contains("T-3")) {
                map.put(i, Exam.T3);
            }
        }
        return map;
    }

    private int extractTotal(String text) {
        Matcher m = Pattern.compile("\\((.*)\\)").matcher(text);
        Log.v("extractTotal", text);
        if (m.find()) {
            int total = (int) Double.parseDouble(m.group(1) == null ? "0" : m.group(1));
            Log.v("total " + text, "" + total);
            return total;
        }
        return 0;
    }

    private HashMap<Exam, Integer> getTotalMarks(Elements children) {
        HashMap<Exam, Integer> totalMarks = new HashMap<>();
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i).html().contains("P-1")) {
                totalMarks.put(Exam.P1, extractTotal(children.get(i).html()));
            } else if (children.get(i).html().contains("P-2")) {
                totalMarks.put(Exam.P2, extractTotal(children.get(i).html()));
            } else if (children.get(i).html().contains("TEST-1")) {
                totalMarks.put(Exam.TEST1, extractTotal(children.get(i).html()));
            } else if (children.get(i).html().contains("T-1")) {
                totalMarks.put(Exam.T1, extractTotal(children.get(i).html()));
            } else if (children.get(i).html().contains("TEST-2")) {
                totalMarks.put(Exam.TEST2, extractTotal(children.get(i).html()));
            } else if (children.get(i).html().contains("T-2")) {
                totalMarks.put(Exam.T2, extractTotal(children.get(i).html()));
            } else if (children.get(i).html().contains("TEST-3")) {
                totalMarks.put(Exam.TEST3, extractTotal(children.get(i).html()));
            } else if (children.get(i).html().contains("T-3")) {
                totalMarks.put(Exam.T3, extractTotal(children.get(i).html()));
            }
        }
        return totalMarks;
    }
}
