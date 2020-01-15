package app.myjuet.com.myjuet.utilities;

import android.content.Context;

public class Constants {
    public String HOST_URL;
    public String BASE_URL;
    public String LOGIN_URL;
    public String ATTENDENCE_LIST;
    public String SEATING_PLAN;
    public String EXAM_MARKS;
    public String DATE_SHEET;
    public String INST_CODE;
    public Constants(Context context){
        HOST_URL = SharedPreferencesUtil.getInstance(context).getPreferences(INSTITUTE_URL, "webkiosk.juet.ac.in");
        BASE_URL = SharedPreferencesUtil.getInstance(context).getPreferences(INSTITUTE_PROTOCOL, "https://")+ HOST_URL;
        INST_CODE = SharedPreferencesUtil.getInstance(context).getPreferences(INSTITUTE_CODE, "JUET");
        LOGIN_URL = BASE_URL+"/CommonFiles/UserAction.jsp";
        ATTENDENCE_LIST = BASE_URL+"/StudentFiles/Academic/StudentAttendanceList.jsp";
        SEATING_PLAN = BASE_URL+"/StudentFiles/Exam/StudViewSeatPlan.jsp";
        EXAM_MARKS = BASE_URL+"/StudentFiles/Exam/StudentEventMarksView.jsp";
        DATE_SHEET = BASE_URL+"/StudentFiles/Exam/StudViewDateSheet.jsp";
    }
    public static final String DATE="date";
    public static final String INSTITUTE_URL="institute_url";
    public static final String INSTITUTE_CODE="institute_code";
    public static final String INSTITUTE_PROTOCOL="institute_protocol";
    public static final int JSOUP_TIMEOUT = 50*1000;

    public static enum Status{LOADING,SUCCESS ,WRONG_PASSWORD, NO_INTERNET,WEBKIOSK_DOWN ,FAILED,LOGGED_IN;}

    public static final String ENROLLMENT = "enrollment";
    public static final String PASSWORD = "password";
    public static final String DOB = "DOB";
    public static final String CURRENT_SEMESTER = "CURRENT_SEMESTER";
    public static final String CURRENT_EXAM = "CURRENT_EXAM";
    
   
}
