package app.myjuet.com.myjuet.utilities;

import android.support.v7.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import app.myjuet.com.myjuet.AttendenceFragment;
import app.myjuet.com.myjuet.data.AttendenceData;
import app.myjuet.com.myjuet.data.AttendenceDetails;
import app.myjuet.com.myjuet.data.ListsReturner;


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
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();

    }


    public static ListsReturner AttendenceCrawler(String Result) {
        String[] datas = new String[5];
        String subPart[] = new String[5];
        Result = Result.trim();
        if (Result.contains("Login</a>")) {
            AttendenceFragment.Error = AttendenceFragment.WRONG_CREDENTIALS;
        }      //get the table body of atendence

        else if (Result.contains("<tbody") && Result.contains("</tbody>") && !Result.equals(null)) {
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

                        if (i == 5) {
                            if (!listDetails.isEmpty()) {
                                list.add(new AttendenceData(datas[0], count[1], count[0], datas[1], datas[2], datas[3]));
                                detailsmain.add(listDetails);
                                listDetails = new ArrayList<>();
                            } else {
                                list.clear();
                            }
                        }
                        subPart[1] = subPart[1].substring(subPart[1].indexOf("</td>") + 5);

                    } else
                        break;
                }
                subPart[0] = subPart[0].replace(temp, "");
            }
            ListsReturner returner = new ListsReturner(list, detailsmain);
            conn.disconnect();
            return returner;
        }
        list.clear();
        detailsmain.clear();
        ListsReturner returner = new ListsReturner(list, detailsmain);
        conn.disconnect();
        return returner;
    }

    //method to extract data from the html tag as argument @AttendenceCrawler
    private static String dataExtractor(String data, int num) {
        String extracted = "";
        String Url;
        switch (num) {
            case 1:
                extracted = data.substring(data.indexOf("<td") + 4, data.indexOf(" - "));
                break;
            case 5:
            case 2:
                if (data.contains("href")) {
                    Url = "https://webkiosk.juet.ac.in/StudentFiles/Academic/" + data.substring(data.indexOf("href='") + 6, data.indexOf("'>"));
                    Url = Url.replace("amp;", "");
                    listDetails = AttendenceDetailsFinder(Url);
                }
            case 3:
            case 4:

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

    private static ArrayList<AttendenceDetails> AttendenceDetailsFinder(String link) {

        String Content = "";
        try {
            Content = GetPageContent(link);
            count[0] = Content.split("Present").length - 1;
            count[1] = Content.split("Absent").length - 2;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return DetailsCrawler(Content);
    }

    private static ArrayList<AttendenceDetails> DetailsCrawler(String Result) {
        String subPart[] = new String[5];
        ArrayList<AttendenceDetails> listDetails = new ArrayList<>();
        String[] data = new String[3];
        //get the table body of atendence
        if (Result.contains("</thead><tbody>") && Result.contains("</tbody>")) {
            subPart[0] = Result.substring(Result.indexOf("</thead><tbody>") + 8, Result.indexOf("</tbody>"));
            //rows looping
            while (subPart[0].contains("<tr")) {
                if (subPart[0].contains("<tr") & subPart[0].contains("</tr>")) {

                    subPart[1] = subPart[0].substring(subPart[0].indexOf("<tr"), subPart[0].indexOf("</tr>") + 5);

                } else
                    break;
                String temp = subPart[1];

                //loop for columns
                for (int i = 0; i < 6; i++) {

                    if (subPart[1].contains("<td") & subPart[1].contains("</td>")) {
                        if (i < 3)
                            subPart[2] = subPart[1].substring(subPart[1].indexOf("<td") + 24, subPart[1].indexOf("</td>"));
                        else if (i == 3) {
                            subPart[2] = subPart[1].substring(subPart[1].indexOf("<td") - 1, subPart[1].indexOf("</td>"));
                            if (subPart[2].contains("Present"))
                                subPart[2] = "Present";
                            else
                                subPart[2] = "Absent";
                        } else if (i == 4)
                            subPart[2] = subPart[1].substring(subPart[1].indexOf("<td>") + 4, subPart[1].indexOf("</td>"));
                        else {
                            subPart[2] = subPart[1].substring(subPart[1].indexOf("color=\"\">") + 9, subPart[1].indexOf("</font></b></td>"));

                        }
                        subPart[1] = subPart[1].substring(subPart[1].indexOf("</td>") + 5);

                        switch (i) {
                            case 1:
                                data[0] = subPart[2];
                                break;
                            case 3:
                                data[1] = subPart[2];
                                break;
                            case 5:
                                data[2] = subPart[2];
                                AttendenceDetails dataDetailed = new AttendenceDetails(data[0], data[1], data[2]);
                                listDetails.add(dataDetailed);
                                break;
                            default:
                        }
                    } else if (i == 5) {
                        data[2] = "Lab";
                        AttendenceDetails dataDetailed = new AttendenceDetails(data[0], data[1], data[2]);
                        listDetails.add(dataDetailed);
                    } else
                        break;
                }
                subPart[0] = subPart[0].replace(temp, "");
            }
        }
        return listDetails;
    }


}
