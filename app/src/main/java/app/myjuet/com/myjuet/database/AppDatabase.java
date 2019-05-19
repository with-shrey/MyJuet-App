package app.myjuet.com.myjuet.database;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import app.myjuet.com.myjuet.data.AttendenceData;
import app.myjuet.com.myjuet.data.AttendenceDetails;
import app.myjuet.com.myjuet.data.DateSheet;
import app.myjuet.com.myjuet.data.ExamMarks;
import app.myjuet.com.myjuet.data.SeatingPlan;

/**
 * Upgrade version in case of schema change
 */
@Database(entities = {AttendenceData.class,AttendenceDetails.class, DateSheet.class, SeatingPlan.class, ExamMarks.class}, version = 4, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private  static  AppDatabase mAppDatabase;

    public static AppDatabase newInstance(Context context) {
        if (mAppDatabase == null)
        mAppDatabase = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "myjuet.db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();
    return mAppDatabase;
    }

    public abstract AttendenceDataDao AttendenceDao();
    public abstract AttendenceDetailsDao AttendenceDetailsDao();
    public abstract DateSheetDao DateSheetDao();
    public abstract SeatingPlanDao SeatingPlanDao();
    public abstract ExamMarksDao ExamMarksDao();
}
