package app.myjuet.com.myjuet.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;


import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import app.myjuet.com.myjuet.data.AttendenceData;
import app.myjuet.com.myjuet.data.AttendenceDetails;
import app.myjuet.com.myjuet.data.SgpaData;
import app.myjuet.com.myjuet.database.AppDatabase;
import app.myjuet.com.myjuet.database.AttendenceDataDao;
import app.myjuet.com.myjuet.database.AttendenceDetailsDao;


@SuppressWarnings("StringBufferMayBeStringBuilder")
public class webUtilities extends AppCompatActivity {
    private static final String USER_AGENT = "Mozilla/5.0";
    public static ArrayList<AttendenceData> list = new ArrayList<>();
    public static HttpURLConnection conn = null;
    private static ArrayList<ArrayList<AttendenceDetails>> detailsmain = new ArrayList<>();
    private static ArrayList<AttendenceDetails> listDetails = new ArrayList<>();
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

    public static boolean isConnected(Context context){
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return  activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    //sending Post to a link with paramaters
    public static String sendPost(String url, String postParams) throws Exception {
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
        conn.setRequestProperty("Result-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Result-Length", Integer.toString(postParams.length()));
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
        return response.toString();
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
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();

    }

    public static ArrayList<SgpaData> crawlCGPA(String Result) {
        ArrayList<SgpaData> datasg = new ArrayList<>();
        if (Result.contains("<tbody>")) {
            String p1 = Result.substring(Result.indexOf("<tbody>", Result.indexOf("<tbody>") + 1), Result.indexOf("</tbody>", Result.indexOf("</tbody>") + 1));
            for (int i = 1; i <= 8 && p1.contains("<tr>"); i++) {
                String p2 = p1.substring(p1.indexOf("<tr>"), p1.indexOf("</tr>"));
                p1 = p1.substring(p1.indexOf("</tr>") + 5);
                SgpaData data = new SgpaData();
                for (int j = 1; j <= 8; j++) {
                    switch (j) {
                        case 1:
                            // data.setmSem(Integer.valueOf(p2.substring(p2.indexOf("</a>")-5,p2.indexOf("</a>")-4)));
                            String temp1 = p2.substring(p2.indexOf("<td"), p2.indexOf("</td>") + 5);
                            data.setmSem(Integer.valueOf(temp1.substring(temp1.indexOf("</a>") - 1, temp1.indexOf("</a>"))));
                            p2 = p2.substring(p2.indexOf("</td>") + 5);
                            break;

                        case 2:
                            temp1 = p2.substring(p2.indexOf("<td>"), p2.indexOf("</td>") + 5);
                            data.setmGradePoints(Math.round(Float.valueOf(temp1.substring(temp1.indexOf("<td>") + 4, temp1.indexOf("</td>")))));
                            p2 = p2.substring(p2.indexOf("</td>") + 5);
                            break;
                        case 3:
                            temp1 = p2.substring(p2.indexOf("<td>"), p2.indexOf("</td>") + 5);
                            data.setMcoursecredits(Math.round(Float.valueOf(temp1.substring(temp1.indexOf("<td>") + 4, temp1.indexOf("</td>")))));
                            p2 = p2.substring(p2.indexOf("</td>") + 5);
                            break;
                        case 4:
                            temp1 = p2.substring(p2.indexOf("<td>"), p2.indexOf("</td>") + 5);
                            data.setMearned(Math.round(Float.valueOf(temp1.substring(temp1.indexOf("<td>") + 4, temp1.indexOf("</td>")))));
                            p2 = p2.substring(p2.indexOf("</td>") + 5);
                            break;
                        case 5:
                            temp1 = p2.substring(p2.indexOf("<td>"), p2.indexOf("</td>") + 5);
                            data.setmPointssecuredcgpa(Math.round(Float.valueOf(temp1.substring(temp1.indexOf("<td>") + 4, temp1.indexOf("</td>")))));
                            p2 = p2.substring(p2.indexOf("</td>") + 5);
                            p2 = p2.substring(p2.indexOf("</td>") + 5);
                            break;
                        case 6:
                            break;

                        case 7:
                            temp1 = p2.substring(p2.indexOf("<td"), p2.indexOf("</td>") + 5);
                            data.setmSgpa(Float.valueOf(temp1.substring(temp1.indexOf(">") + 1, temp1.indexOf("</td>"))));
                            p2 = p2.substring(p2.indexOf("</td>") + 5);
                            break;
                        case 8:
                            temp1 = p2.substring(p2.indexOf("<td"), p2.indexOf("</td>") + 5);
                            data.setmCgpa(Float.valueOf(temp1.substring(temp1.indexOf(">") + 1, temp1.indexOf("</td>"))));
                            p2 = p2.substring(p2.indexOf("</td>") + 5);
                            datasg.add(data);
                            data = new SgpaData();
                            break;

                    }
                }
            }
        }
        return datasg;
    }

    public static void parseAttendencePage(AttendenceDataDao attendenceDataDao, Document doc){
        Element table = doc.getElementById("table-1");
        if (table != null) {
            Elements tbodies = table.getElementsByTag("tbody");
            if (tbodies.size() > 0) {
                Element tbody = tbodies.get(0);
                Elements subjects = tbody.children();
                for (Element subject : subjects) {
                    AttendenceData attendenceData = new AttendenceData();
                    Elements columns = subject.children();
                    for (int i = 0; i < columns.size(); i++) {
                        switch (i) {
                            case 0:
                                attendenceData.setId(columns.get(i).text());
                                break;
                            case 1:
                                try {
                                    attendenceData.setName(TextUtils.split(columns.get(i).text(), " - ")[0]);
                                } catch (Exception e) {
                                    attendenceData.setName("--");
                                }
                                try {
                                    attendenceData.setSubjectCode(TextUtils.split(columns.get(i).text(), " - ")[1]);
                                } catch (Exception e) {
                                    attendenceData.setSubjectCode("--");
                                }
                                break;
                            case 2:
                                if (!columns.get(i).text().equals("&nbsp"))
                                    if (columns.get(i).children().size() > 0) {
                                        attendenceData.setSubjectUrl("https://webkiosk.juet.ac.in/StudentFiles/Academic/" + columns.get(i).children().get(0).attr("href"));
                                        attendenceData.setLecTut(columns.get(i).text().replace("&nbsp", "--"));
                                    } else {
                                        attendenceData.setLecTut("--");
                                    }

                                break;
                            case 3:
                                if (!columns.get(i).text().equals("&nbsp"))

                                    attendenceData.setLec(columns.get(i).text().replace("&nbsp", "--"));
                                else {
                                    attendenceData.setLec("--");
                                }
                                break;
                            case 4:
                                if (!columns.get(i).text().equals("&nbsp"))
                                    if (columns.get(i).children().size() > 0) {
                                        attendenceData.setTut(columns.get(i).text().replace("&nbsp", "--"));
                                    } else {
                                        attendenceData.setTut("--");
                                    }
                                break;
                            case 5:
                                if (!columns.get(i).text().equals("&nbsp"))
                                    if (columns.get(i).children().size() > 0) {
                                        attendenceData.setSubjectUrl("https://webkiosk.juet.ac.in/StudentFiles/Academic/" + columns.get(i).children().get(0).attr("href"));
                                        attendenceData.setLecTut(columns.get(i).text().replace("&nbsp", "--"));
                                    }
                                break;
                        }
                    }
                    attendenceDataDao.insert(attendenceData);
                }

            }
        }
    }

    public static void parseAttendenceDetails(AttendenceData datum, AttendenceDataDao dataDao,AttendenceDetailsDao detailsDao, Document doc){
        Element table = doc.getElementById("table-1");
        if (table != null) {
            Elements tbodies = table.getElementsByTag("tbody");
            if (tbodies.size() > 0) {
                Element tbody = tbodies.get(0);
                int CountPresent = tbody.html().split("Present").length - 1;
                int CountAbsent = tbody.html().split("Absent").length - 1;
                int mOnNext, mOnLeaving;
                if (CountPresent == 0 && CountAbsent == 0) {
                    mOnNext = 100;
                    mOnLeaving = 0;
                } else {
                    mOnNext = (int) ((float) ((CountPresent + 1) * 100) / (float) (CountPresent + CountAbsent + 1));
                    mOnLeaving = (int) ((float) (CountPresent * 100) / (float) (CountPresent + CountAbsent + 1));
                }
                dataDao.updatePresentAbsent(datum.getId(), CountPresent, CountAbsent, mOnNext, mOnLeaving);
                Elements classes = tbody.children();
                for (Element lecture : classes) {
                    AttendenceDetails details = new AttendenceDetails();
                    Elements columns = lecture.children();
                    details.setSubjectId(datum.getId());
                    for (int i = 0; i < columns.size(); i++) {
                        switch (i) {
                            case 0:
                                details.setId(datum.getId() + "_" + columns.get(i).text());
                                break;
                            case 1:
                                try {
                                    details.setDate(TextUtils.split(columns.get(i).text(), " ")[0]);
                                } catch (Exception e) {
                                    details.setDate("--");
                                }
                                try {
                                    details.setTime(TextUtils.split(columns.get(i).text(), " ")[1] + " " + TextUtils.split(columns.get(i).text(), " ")[2]);
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
                    detailsDao.insert(details);

                }

            }
        }
    }

    public static Pair<Connection.Response,Connection.Response> login(String user, String dob, String pass) throws IOException {
        Connection.Response res1 = null;
        Connection.Response res = null;
        res1 = Jsoup
                .connect("https://webkiosk.juet.ac.in/")
                .method(Connection.Method.GET)
                .execute();
        Document doc = res1.parse();
        String captcha = doc.select(".noselect").first().text();
        res = Jsoup
                .connect("https://webkiosk.juet.ac.in/CommonFiles/UserAction.jsp")
                .cookies(res1.cookies())
                .data("txtInst", "Institute"
                        , "InstCode", "JUET"
                        , "x", ""
                        , "txtuType", "Member+Type"
                        , "UserType", "S"
                        , "txtCode", "Enrollment+No"
                        , "MemberCode", user
                        , "DOB", "DOB"
                        , "DATE1", dob
                        , "txtPin", "Password%2FPin"
                        , "Password", pass
                        , "txtCodecaptcha", "Enter Captcha"
                        , "txtcap", captcha
                        , "BTNSubmit", "Submit"
                )
                .method(Connection.Method.POST)
                .execute();
        return new Pair<>(res1, res);
    }

}

