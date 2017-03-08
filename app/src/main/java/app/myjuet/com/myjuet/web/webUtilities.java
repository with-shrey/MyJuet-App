package app.myjuet.com.myjuet.web;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import app.myjuet.com.myjuet.AttendenceActivity;

import static android.R.id.input;

/**
 * Created by Shrey on 08-Mar-17.
 */

public class webUtilities extends AppCompatActivity {
    private static final String USER_AGENT = "Mozilla/5.0";
    private static HttpURLConnection conn = null;

    // Creating url and catching exception
    private static URL UrlCreator(String url) {
        URL link = null;
        try {
            link = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return link;
    }

    //sending Post to a link with paramaters
    public static void sendPost(String url, String postParams) throws Exception {
        URL obj = UrlCreator(url);

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


    public static String GetPageContent(String url) throws Exception {
        URL obj = UrlCreator(url);
        String Result = null;
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
        AttendenceCrawler(response.toString());
        return response.toString();

    }

    public static void AttendenceCrawler(String Result) {
        String subPart[] = new String[5];
        boolean login = true;
        Result = Result.trim();

        //get the table body of atendence
        subPart[0] = Result.substring(Result.indexOf("<tbody>"), Result.indexOf("</tbody>"));
        for (int j = 0; j < 8; j++) {
            subPart[1] = subPart[0].substring(subPart[0].indexOf("<tr>"), subPart[0].indexOf("</tr>") + 5);
            String temp = subPart[1];
            for (int i = 0; i < 7 & (subPart[1] != "" || subPart[1] != null); i++) {
                if (subPart[1].contains("<td>") & subPart[1].contains("</td>")) {
                    subPart[2] = subPart[1].substring(subPart[1].indexOf("<td"), subPart[1].indexOf("</td") + 5);
                    subPart[1] = subPart[1].replace(subPart[2], "");
                    Log.v("String", subPart[2]);
                } else break;
            }
            subPart[0] = subPart[0].replace(temp, "");
        }
    }
}
