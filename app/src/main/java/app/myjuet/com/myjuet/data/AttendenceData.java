package app.myjuet.com.myjuet.data;


import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity
public class AttendenceData implements java.io.Serializable {
    @PrimaryKey
    @NonNull
    private String id;
    private String mSubjectCode;
    private String mSubjectUrl;
    private String mLecTut;
    private String mTut;
    private String mLec;
    private String mName;
    private int mCountPresent;
    private int mCountAbsent;
    private int mOnNext;
    private int mOnLeaving;
    private boolean loading;

/*
public constructor
 */

    public boolean equals(AttendenceData that) {
        return  getOnNext() == that.getOnNext() &&
                getOnLeaving() == that.getOnLeaving() &&
                isLoading() == that.isLoading() &&
                id.equals(that.getId()) &&
                getSubjectCode().equals(that.getSubjectCode()) &&
                getName().equals(that.getName());
    }



    public AttendenceData(String Name, int CountAbsent, int CountPresent, String LecTut, String Lec, String Tut) {
        this.mName = Name;
        this.mCountAbsent = CountAbsent;
        this.mCountPresent = CountPresent;
        if (CountPresent == 0 && CountAbsent == 0) {
            this.mOnNext = 100;
            this.mOnLeaving = 0;
        } else {
        this.mOnNext = (int) ((float) ((CountPresent + 1) * 100) / (float) (CountPresent + CountAbsent + 1));
        this.mOnLeaving = (int) ((float) (CountPresent * 100) / (float) (CountPresent + CountAbsent + 1));
        }
        this.mLec = Lec;
        this.mLecTut = LecTut;
        this.mTut = Tut;

    }
    public AttendenceData() {
        loading = true;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubjectCode() {
        return mSubjectCode;
    }

    public String getLecTut() {
        return mLecTut;
    }

    public String getTut() {
        return mTut;
    }

    public String getLec() {
        return mLec;
    }

    public String getName() {
        return mName;
    }

    public int getCountPresent() {
        return mCountPresent;
    }

    public int getCountAbsent() {
        return mCountAbsent;
    }

    public int getOnNext() {
        return mOnNext;
    }

    public int getOnLeaving() {
        return mOnLeaving;
    }



    public String getSubjectUrl() {
        return mSubjectUrl;
    }

    public void setSubjectUrl(String subjectUrl) {
        mSubjectUrl = subjectUrl;
    }

    public void setSubjectCode(String subjectCode) {
        mSubjectCode = subjectCode;
    }

    public void setLecTut(String lecTut) {
        mLecTut = lecTut;
    }

    public void setTut(String tut) {
        mTut = tut;
    }

    public void setLec(String lec) {
        mLec = lec;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setCountPresent(int countPresent) {
        mCountPresent = countPresent;
    }

    public void setCountAbsent(int countAbsent) {
        mCountAbsent = countAbsent;
    }

    public void setOnNext(int onNext) {
        mOnNext = onNext;
    }

    public void setOnLeaving(int onLeaving) {
        mOnLeaving = onLeaving;
    }

    public String getmName() {
        return mName;
    }

    public String getmCountAbsent() {
        return String.valueOf(mCountAbsent);
    }

    public String getmCountPresent() {
        return String.valueOf(mCountPresent);
    }

    public String getmLec() {
        return mLec;
    }

    public String getmLecTut() {
        return mLecTut;
    }

    public String getmOnLeaving() {
        return String.valueOf(mOnLeaving);
    }

    public String getmOnNext() {
        return String.valueOf(mOnNext);
    }

    public String getmTut() {
        return mTut;
    }
}
