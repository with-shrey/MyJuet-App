package app.myjuet.com.myjuet.data;

import java.io.Serializable;

/**
 * Created by Shrey on 20-Jul-17.
 */

public class SgpaData implements java.io.Serializable {
    int mSem, mcoursecredits, mearned, pointssecuredsgpa, mPointssecuredcgpa, mGradePoints;
    float mCgpa, mSgpa;

    public int getmSem() {
        return mSem;
    }

    public void setmSem(int mSem) {
        this.mSem = mSem;
    }

    public int getmGradePoints() {
        return mGradePoints;
    }

    public void setmGradePoints(int mGradePoints) {
        this.mGradePoints = mGradePoints;
    }

    public int getMcoursecredits() {
        return mcoursecredits;
    }

    public void setMcoursecredits(int mcoursecredits) {
        this.mcoursecredits = mcoursecredits;
    }

    public int getMearned() {
        return mearned;
    }

    public void setMearned(int mearned) {
        this.mearned = mearned;
    }

    public int getPointssecuredsgpa() {
        return pointssecuredsgpa;
    }

    public void setPointssecuredsgpa(int pointssecuredsgpa) {
        this.pointssecuredsgpa = pointssecuredsgpa;
    }

    public int getmPointssecuredcgpa() {
        return mPointssecuredcgpa;
    }

    public void setmPointssecuredcgpa(int mPointssecuredcgpa) {
        this.mPointssecuredcgpa = mPointssecuredcgpa;
    }

    public float getmCgpa() {
        return mCgpa;
    }

    public void setmCgpa(float mCgpa) {
        this.mCgpa = mCgpa;
    }

    public float getmSgpa() {
        return mSgpa;
    }

    public void setmSgpa(float mSgpa) {
        this.mSgpa = mSgpa;
    }
}
