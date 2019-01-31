package app.myjuet.com.myjuet.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;


/**
 * Created by Shrey on 10-Mar-17.
 */

@Entity
public class AttendenceDetails implements java.io.Serializable {
    private String mDate;
    private String mStatus;
    private String mType;
    private String mTime;
    private String mSubjectId;
    @NonNull
    @PrimaryKey
    private String id;


    public AttendenceDetails(String mDate, String mStatus, String mType) {
        if (mDate.contains("N/A")) {
            this.mStatus = "Present";
            this.mType = mType;
            mTime = "N/A";
            this.mDate = "N/A";
        } else {
            this.mStatus = mStatus;
            this.mType = mType;
            mTime = mDate.substring(mDate.indexOf(" "));
            this.mDate = mDate.replace(mTime, "");
        }
    }


    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public AttendenceDetails() {
        mSubjectId = "";
        mType = "Lab";
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    @NonNull
    public String getSubjectId() {
        return mSubjectId;
    }

    public void setSubjectId(@NonNull String subjectId) {
        mSubjectId = subjectId;
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
