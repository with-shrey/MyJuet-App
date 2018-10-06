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

import static app.myjuet.com.myjuet.services.RefreshService.pingHost;

public class AttendenceViewModel extends AndroidViewModel {
    AppExecutors mAppExecutors;
    AppDatabase mAppDatabase;
    Application context;
    Map<String, String> loginCookies;
    public static enum Status{LOADING,SUCCESS ,WRONG_PASSWORD, NO_INTERNET ,FAILED}
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
            if (!pingHost("webkiosk.juet.ac.in", 80, 5000)) {
                mAppExecutors.mainThread().execute(()-> {
                    mLoginStatus.setValue(Status.NO_INTERNET);
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
            Document doc = null;
            try {
                doc = Jsoup.connect("https://webkiosk.juet.ac.in/StudentFiles/Academic/StudentAttendanceList.jsp")
                        .cookies(loginCookies)
                        .get();
               Elements tbodies = doc.getElementById("table-1").getElementsByTag("tbody");
               if (tbodies.size() > 0){
                  Element tbody = tbodies.get(0);
                  Elements subjects = tbody.children();
                   for (Element subject : subjects) {
                       AttendenceData attendenceData = new AttendenceData();
                       Elements columns = subject.children();
                       for (int i = 0; i < columns.size(); i++) {
                           switch(i){
                               case 0:
                                   attendenceData.setId(columns.get(i).text());
                                   break;
                               case 1:
                                   try {
                                       attendenceData.setName(TextUtils.split(columns.get(i).text(), " - ")[0]);
                                   }catch (Exception e){
                                       attendenceData.setName("--");
                                   }
                                   try {
                                       attendenceData.setSubjectCode(TextUtils.split(columns.get(i).text(), " - ")[1]);
                                   }catch (Exception e){
                                       attendenceData.setSubjectCode("--");
                                   }
                                   break;
                               case 2:
                                   if (!columns.get(i).text().equals("&nbsp"))
                                       if (columns.get(i).children().size() > 0){
                                           attendenceData.setSubjectUrl("https://webkiosk.juet.ac.in/StudentFiles/Academic/" +columns.get(i).children().get(0).attr("href"));
                                           attendenceData.setLecTut(columns.get(i).text().replace("&nbsp","--"));
                                       }
                                    else{
                                           attendenceData.setLecTut("--");
                                       }

                                   break;
                               case 3:
                                   if (!columns.get(i).text().equals("&nbsp"))

                                           attendenceData.setLec(columns.get(i).text().replace("&nbsp","--"));
                                       else{
                                           attendenceData.setLec("--");
                                       }
                                   break;
                               case 4:
                                   if (!columns.get(i).text().equals("&nbsp"))
                                       if (columns.get(i).children().size() > 0){
                                           attendenceData.setTut(columns.get(i).text().replace("&nbsp","--"));
                                       }
                                       else{
                                           attendenceData.setTut("--");
                                       }
                                   break;
                               case 5:
                                   if (!columns.get(i).text().equals("&nbsp"))
                                       if (columns.get(i).children().size() > 0){
                                           attendenceData.setSubjectUrl("https://webkiosk.juet.ac.in/StudentFiles/Academic/" +columns.get(i).children().get(0).attr("href"));
                                           attendenceData.setLecTut(columns.get(i).text().replace("&nbsp","--"));
                                       }
                                   break;
                           }
                       }
                       Log.v("JSOUP",attendenceData.getId());
                       mAppDatabase.AttendenceDao().insert(attendenceData);
                   }

               }
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
        mAppDatabase.AttendenceDao().updateLoading(true);
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

                        Elements tbodies = doc.getElementById("table-1").getElementsByTag("tbody");
                        if (tbodies.size() > 0) {
                            Element tbody = tbodies.get(0);
                            int CountPresent = tbody.html().split("Present").length - 1;
                            int CountAbsent = tbody.html().split("Absent").length - 2;
                            int mOnNext,mOnLeaving;
                            if (CountPresent == 0 && CountAbsent == 0) {
                                mOnNext = 100;
                                mOnLeaving = 0;
                            } else {
                                mOnNext = (int) ((float) ((CountPresent + 1) * 100) / (float) (CountPresent + CountAbsent + 1));
                                mOnLeaving = (int) ((float) (CountPresent * 100) / (float) (CountPresent + CountAbsent + 1));
                            }
                            mAppDatabase.AttendenceDao().updatePresentAbsent(datum.getId(), CountPresent, CountAbsent,mOnNext,mOnLeaving);
                            Elements classes = tbody.children();
                            for (Element lecture : classes) {
                                Log.v("JSOUPDET", lecture.html());
                                AttendenceDetails details = new AttendenceDetails();
                                Elements columns = lecture.children();
                                details.setSubjectId(datum.getId());
                                for (int i = 0; i < columns.size(); i++) {
                                    switch (i) {
                                        case 0:
                                            details.setId(datum.getId()+"_"+columns.get(i).text());
                                            break;
                                        case 1:
                                            try {
                                                details.setDate(TextUtils.split(columns.get(i).text(), " ")[0]);
                                            } catch (Exception e) {
                                                details.setDate("--");
                                            }
                                            try {
                                                details.setTime(TextUtils.split(columns.get(i).text(), " ")[1]+" "+TextUtils.split(columns.get(i).text(), " ")[2]);
                                            } catch (Exception e) {
                                                details.setTime("--");
                                            }
                                            break;
                                        case 2:
                                            break;
                                        case 3:
                                            details.setStatus(columns.get(i).text());
                                            break;
                                        case 4:

                                            break;
                                        case 5:
                                            details.setType(columns.get(i).text());
                                            break;
                                    }
                                }
                                mAppDatabase.AttendenceDetailsDao().insert(details);

                            }

                        }
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
