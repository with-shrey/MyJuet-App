package app.myjuet.com.myjuet;

import android.content.Context;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
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

    public static boolean pingHost(String host, int port, int timeout) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), timeout);
            socket.close();
            return true;
        } catch (IOException e) {
            return false; // Either timeout or unreachable or failed DNS lookup.
        }
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<AttendenceData> loadInBackground() {
        ArrayList<AttendenceData> DataAttendence;
        String Content = " ";
        try {
            if (pingHost("webkiosk.juet.ac.in", 80, 5000)) {
                webUtilities.sendPost(mUrl, mPostParam);
                Content = webUtilities.GetPageContent(mAttendence);
                webUtilities.conn.disconnect();
            } else AttendenceActivity.Error = AttendenceActivity.HOST_DOWN;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!Content.equals(" ")) {
            DataAttendence = AttendenceCrawler(Content);

            AttendenceActivity.listdata.clear();
            return DataAttendence;
        }
        return AttendenceActivity.listdata;
    }
}

