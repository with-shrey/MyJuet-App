package app.myjuet.com.myjuet;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;

import app.myjuet.com.myjuet.web.webUtilities;

public class AttendenceActivity extends AppCompatActivity {

    TextView text ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendence);
        text=(TextView)findViewById(R.id.text);
        CookieHandler.setDefault(new CookieManager());
        extractor con = new extractor();
        con.execute();
    }

    private class extractor extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... voids) {

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

            return Content;

        }

        @Override
        protected void onPostExecute(String s) {
            Log.v("Activity Shrey",s);
            text.setText(s);
            super.onPostExecute(s);
        }

    }
}
