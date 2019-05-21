package app.myjuet.com.myjuet.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import app.myjuet.com.myjuet.data.DateSheet;
import app.myjuet.com.myjuet.data.ExamMarks;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ExamMarksDao {
    @Insert(onConflict = REPLACE)
    void insert(ExamMarks data);

    @Query("SELECT * from exammarks")
    LiveData<List<ExamMarks>> examMarks();

    @Query("DELETE FROM exammarks")
    void deleteAll();
}
