package app.myjuet.com.myjuet;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import app.myjuet.com.myjuet.adapters.AttendenceAdapter;
import app.myjuet.com.myjuet.data.AttendenceData;
import app.myjuet.com.myjuet.web.webUtilities;

import static app.myjuet.com.myjuet.web.webUtilities.AttendenceCrawler;

public class AttendenceActivity extends AppCompatActivity {

    public static AttendenceAdapter adapter;
    public static AttendenceData tempData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
        ListView list = (ListView) findViewById(R.id.list_view);
        adapter = new AttendenceAdapter(AttendenceActivity.this, new ArrayList<AttendenceData>());
        list.setAdapter(adapter);
        CookieHandler.setDefault(new CookieManager());
        extractor con = new extractor();
        con.execute();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tempData = adapter.getItem(i);
                Intent intent = new Intent(AttendenceActivity.this, AttendenceDetailsActivity.class);
                startActivity(intent);
            }
        });
    }

    private class extractor extends AsyncTask<Void, Void, ArrayList<AttendenceData>> {

        @Override
        protected ArrayList<AttendenceData> doInBackground(Void... voids) {
            ArrayList<AttendenceData> DataAttendence;
            String Url="https://webkiosk.juet.ac.in/CommonFiles/UserAction.jsp";
            String PostParam="txtInst=Institute&InstCode=JUET&txtUType=Member+Type&UserType=S&txtCode=Enrollment No&MemberCode=161B222&txtPIN=Password%2FPin&Password=jaishriram&BTNSubmit=Submit";
        String Attendence="https://webkiosk.juet.ac.in/StudentFiles/Academic/StudentAttendanceList.jsp";
            String Content="";
            try {
                webUtilities.sendPost(Url, PostParam);
                Content = webUtilities.GetPageContent(Attendence);


            } catch (Exception e) {
                e.printStackTrace();
            }
            DataAttendence = AttendenceCrawler(Content);
            return DataAttendence;

        }

        @Override
        protected void onPostExecute(ArrayList<AttendenceData> s) {
            adapter.clear();
            if (adapter != null)
                adapter.addAll(s);
            super.onPostExecute(s);
        }


    }
}
