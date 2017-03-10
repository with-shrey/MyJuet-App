package app.myjuet.com.myjuet;

import android.content.Context;

import java.util.ArrayList;

import app.myjuet.com.myjuet.data.AttendenceData;
import app.myjuet.com.myjuet.web.webUtilities;

import android.content.AsyncTaskLoader;

import static app.myjuet.com.myjuet.R.id.Attendence;
import static app.myjuet.com.myjuet.web.webUtilities.AttendenceCrawler;


/**
 * Created by Shrey on 10-Mar-17.
 */

public class AttendenceLoader extends AsyncTaskLoader<ArrayList<AttendenceData>> {
    private String mUrl, mPostParam, mAttendence;

    public AttendenceLoader(Context context, String url, String PostParam) {
        super(context);
        mUrl = url;
        mPostParam = PostParam;
        mAttendence = "https://webkiosk.juet.ac.in/StudentFiles/Academic/StudentAttendanceList.jsp";

    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<AttendenceData> loadInBackground() {
        ArrayList<AttendenceData> DataAttendence;
        String Content = "";
        try {
            webUtilities.sendPost(mUrl, mPostParam);
            Content = webUtilities.GetPageContent(mAttendence);


        } catch (Exception e) {
            e.printStackTrace();
        }
        DataAttendence = AttendenceCrawler(Content);
        return DataAttendence;
    }
}

