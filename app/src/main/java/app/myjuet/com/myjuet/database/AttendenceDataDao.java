package app.myjuet.com.myjuet.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import app.myjuet.com.myjuet.data.AttendenceData;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface AttendenceDataDao {
    @Insert(onConflict = REPLACE)
    void insert(AttendenceData data);

    @Query("SELECT * from attendencedata")
    LiveData<List<AttendenceData>> AttendanceDataObserver();

    @Query("SELECT * from attendencedata")
    List<AttendenceData> AttendanceData();

    @Query("SELECT * from attendencedata WHERE id = :id")
    LiveData<AttendenceData> getAttendenceById(String id);

    @Query("UPDATE  attendencedata SET mCountPresent = :p , mCountAbsent = :a, mOnNext=:attendNext, mOnLeaving = :leaveNext WHERE id = :id ")
    void updatePresentAbsent(String id,int p, int a, int attendNext,int leaveNext);

    @Query("UPDATE  attendencedata SET loading = :loading WHERE id = :id")
    void updateLoading(String id,boolean loading);

    @Query("UPDATE  attendencedata SET loading = :loading")
    void updateLoading(boolean loading);
}
