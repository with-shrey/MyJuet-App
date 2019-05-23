package app.myjuet.com.myjuet.utilities;

public class Constants {
    public static final String DATE="date";
    public static final int JSOUP_TIMEOUT = 50*1000;
    public static final String PING_HOST = "webkiosk.juet.ac.in";
    public static final String BASE_URL = "https://webkiosk.juet.ac.in/";
    public static final String LOGIN_URL = BASE_URL+"CommonFiles/UserAction.jsp";
    public static final String ATTENDENCE_LIST = BASE_URL+"StudentFiles/Academic/StudentAttendanceList.jsp";


    public static enum Status{LOADING,SUCCESS ,WRONG_PASSWORD, NO_INTERNET,WEBKIOSK_DOWN ,FAILED,LOGGED_IN;}
    public static final String ENROLLMENT = "enrollment";
    public static final String PASSWORD = "password";
}
