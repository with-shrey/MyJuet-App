package app.myjuet.com.myjuet;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

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
        HttpURLConnection conn=null;
        private final String USER_AGENT = "Mozilla/5.0";
        @Override
        protected String doInBackground(Void... voids) {

            String Url="https://webkiosk.juet.ac.in/CommonFiles/UserAction.jsp";
            String PostParam="txtInst=Institute&InstCode=JUET&txtUType=Member+Type&UserType=S&txtCode=Enrollment No&MemberCode=161B222&txtPIN=Password%2FPin&Password=jaishriram&BTNSubmit=Submit";
        String Attendence="https://webkiosk.juet.ac.in/StudentFiles/Academic/StudentAttendanceList.jsp";
            String Content="";
            extractor http = new extractor();

            try {
                http.sendPost(Url,PostParam);
                Content=http.GetPageContent(Attendence);
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
        private void sendPost(String url, String postParams) throws Exception {
            URL obj = new URL(url);

            conn = (HttpsURLConnection) obj.openConnection();
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Host", "webkiosk.juet.ac.in");
            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setRequestProperty("Accept",
                    "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            conn.setRequestProperty("Connection", "close");
            conn.setRequestProperty("Referer", "webkiosk.juet.ac.in");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", Integer.toString(postParams.length()));
            conn.setDoOutput(true);
            conn.setDoInput(true);
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        }


        private String GetPageContent(String url) throws Exception {
            URL obj = new URL(url);
            conn = (HttpsURLConnection) obj.openConnection();
            conn.setRequestMethod("GET");
            conn.setUseCaches(false);
            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setRequestProperty("Accept",
                    "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            int responseCode = conn.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        }
    }
}
