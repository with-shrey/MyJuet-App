package app.myjuet.com.myjuet.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import app.myjuet.com.myjuet.data.AttendenceDetails;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface AttendenceDetailsDao {
    @Insert(onConflict = REPLACE)
    void insert(AttendenceDetails data);

    @Query("SELECT * from attendencedetails WHERE mSubjectId = :id")
    LiveData<List<AttendenceDetails>> AttendenceDetails(String id);

}
