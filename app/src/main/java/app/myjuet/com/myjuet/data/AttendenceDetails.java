package app.myjuet.com.myjuet.data;

import android.util.Log;

/**
 * Created by Shrey on 10-Mar-17.
 */

public class AttendenceDetails implements java.io.Serializable {
    private String mDate;
    private String mStatus;
    private String mType;
    private String mTime;


    public AttendenceDetails(String mDate, String mStatus, String mType) {
        this.mStatus = mStatus;
        this.mType = mType;
        mTime = mDate.substring(mDate.indexOf(" "));
        this.mDate = mDate.replace(mTime, "");
    }


    public String getmTime() {
        return mTime;
    }

    public String getmDate() {
        return mDate;
    }

    public String getmStatus() {
        return mStatus;
    }


    public String getmType() {
        return mType;
    }

}
