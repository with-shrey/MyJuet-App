package app.myjuet.com.myjuet.data;

/**
 * Created by Shrey on 10-Mar-17.
 */

public class AttendenceDetails {
    private String mDate;
    private String mStatus;
    private String mType;


    public AttendenceDetails(String mDate, String mStatus, String mType) {
        this.mDate = mDate;
        this.mStatus = mStatus;
        this.mType = mType;
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
