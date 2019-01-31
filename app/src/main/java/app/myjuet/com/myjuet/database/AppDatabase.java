package app.myjuet.com.myjuet.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import app.myjuet.com.myjuet.data.AttendenceData;
import app.myjuet.com.myjuet.data.AttendenceDetails;

/**
 * Upgrade version in case of schema change
 */
@Database(entities = {AttendenceData.class,AttendenceDetails.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private  static  AppDatabase mAppDatabase;

    public static AppDatabase newInstance(Context context) {
        if (mAppDatabase == null)
        mAppDatabase = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "myjuet.db").allowMainThreadQueries().build();
    return mAppDatabase;
    }

    public abstract AttendenceDataDao AttendenceDao();
    public abstract AttendenceDetailsDao AttendenceDetailsDao();

}
