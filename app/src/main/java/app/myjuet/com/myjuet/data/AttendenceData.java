package app.myjuet.com.myjuet.data;


public class AttendenceData {
    private String mLecTut, mTut, mLec, mName;
    private int mCountPresent, mCountAbsent;
    private int mOnNext, mOnLeaving;
/*
public constructor
 */

    public AttendenceData(String Name, int CountAbsent, int CountPresent, String LecTut, String Lec, String Tut) {
        this.mName = Name;
        this.mCountAbsent = CountAbsent;
        this.mCountPresent = CountPresent;
        this.mOnNext = (int) ((float) ((CountPresent + 1) * 100) / (float) (CountPresent + CountAbsent + 1));
        this.mOnLeaving = (int) ((float) (CountPresent * 100) / (float) (CountPresent + CountAbsent + 1));
        this.mLec = Lec;
        this.mLecTut = LecTut;
        this.mTut = Tut;
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
