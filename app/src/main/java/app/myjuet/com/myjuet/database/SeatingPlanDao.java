package app.myjuet.com.myjuet.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import app.myjuet.com.myjuet.data.DateSheet;
import app.myjuet.com.myjuet.data.SeatingPlan;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface SeatingPlanDao {
    @Insert(onConflict = REPLACE)
    void insert(SeatingPlan data);

    @Query("SELECT * from seatingplan")
    LiveData<List<SeatingPlan>> seatingPlan();
}
