package app.myjuet.com.myjuet.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import app.myjuet.com.myjuet.data.AttendenceData;
import app.myjuet.com.myjuet.data.AttendenceDetails;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface AttendenceDetailsDao {
    @Insert(onConflict = REPLACE)
    void insert(AttendenceDetails data);

    @Query("SELECT * from attendencedetails WHERE mSubjectId = :id")
    LiveData<List<AttendenceDetails>> AttendenceDetails(String id);
}
