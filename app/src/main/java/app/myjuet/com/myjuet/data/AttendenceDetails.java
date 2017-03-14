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
        this.mDate = mDate;
        this.mStatus = mStatus;
        this.mType = mType;
        mTime = mDate.substring(mDate.indexOf(" "));
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public String getmStatus() {
        return mStatus;
    }

    public void setmStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }
}
