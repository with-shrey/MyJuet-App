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
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import app.myjuet.com.myjuet.data.AttendenceData;

import static android.R.attr.data;
import static android.R.id.list;


public class webUtilities extends AppCompatActivity {
    private static final String USER_AGENT = "Mozilla/5.0";
    private static HttpURLConnection conn = null;
    private static int[] count = new int[2];
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
        return response.toString();

    }

    public static ArrayList<AttendenceData> AttendenceCrawler(String Result) {
        String[] datas = new String[5];
        String subPart[] = new String[5];
        ArrayList<AttendenceData> list = new ArrayList<>();
        Result = Result.trim();

        //get the table body of atendence
        subPart[0] = Result.substring(Result.indexOf("<tbody>"), Result.indexOf("</tbody>"));
        //rows looping
        for (int j = 0; j < 15; j++) {
            if (subPart[0].contains("<tr") & subPart[0].contains("</tr>")) {

                subPart[1] = subPart[0].substring(subPart[0].indexOf("<tr"), subPart[0].indexOf("</tr>") + 5);

            } else
                break;
            String temp = subPart[1];

            //loop for columns
            for (int i = 0; i < 6; i++) {

                if (subPart[1].contains("<td") & subPart[1].contains("</td>")) {

                    subPart[2] = subPart[1].substring(subPart[1].indexOf("<td"), subPart[1].indexOf("</td>") + 5);

                    String tempData = dataExtractor(subPart[2], i);
                    if (tempData.equals("N/A"))
                        tempData = "--";
                    if (i == 1)  //name
                        datas[0] = tempData;
                    else if (i == 2 || (i == 5 && datas[1].equals("--")))  //lec+tut
                        datas[1] = tempData;
                    else if (i == 3)  //lec
                        datas[2] = tempData;
                    else if (i == 4)  //tut
                        datas[3] = tempData;

                    if (i == 5)
                        list.add(new AttendenceData(datas[0], count[1], count[0], datas[1], datas[2], datas[3]));
                    subPart[1] = subPart[1].substring(subPart[1].indexOf("</td>") + 5);

                } else
                    break;
            }
            subPart[0] = subPart[0].replace(temp, "");
        }
        return list;
    }

    //method to extract data from the html tag as argument @AttendenceCrawler
    private static String dataExtractor(String data, int num) {
        String extracted = "";
        String Url;
        switch (num) {
            case 1:
                extracted = data.substring(data.indexOf("<td") + 4, data.indexOf("-"));
                break;
            case 2:
                if (data.contains("href")) {
                    Url = "https://webkiosk.juet.ac.in/StudentFiles/Academic/" + data.substring(data.indexOf("href='") + 6, data.indexOf("'>"));
                    Url = Url.replace("amp;", "");
                    CountofAttendence(Url);
                    Log.v(String.valueOf(count[0]), String.valueOf(count[1]));

                }
            case 3:
            case 4:
            case 5:
            case 6:

                if (data.contains("&nbsp;"))
                    extracted = "N/A";
                else if (data.contains("align")) {
                    if (data.contains("<font"))
                        extracted = data.substring(data.indexOf("</font></a></td>") - 3, data.indexOf("</font></a></td>"));
                    else
                        extracted = data.substring(data.indexOf("</a></td>") - 3, data.indexOf("</a></td>"));

                    if (extracted.contains(">"))
                        extracted = extracted.replace(">", "");
                } else if (data.contains("<td>")) {

                    extracted = data.substring(data.indexOf("<td>") + 4, data.indexOf("</td>"));
                }
                break;
            default:

                extracted = "N/A";

        }

        return extracted;

    }

    private static void CountofAttendence(String link) {

        String Content;
        try {
            Content = GetPageContent(link);
            count[0] = Content.split("Present").length - 1;
            count[1] = Content.split("Absent").length - 2;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
