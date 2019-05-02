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

/**
 * Upgrade version in case of schema change
 */
@Database(entities = {AttendenceData.class,AttendenceDetails.class, DateSheet.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private  static  AppDatabase mAppDatabase;

    public static AppDatabase newInstance(Context context) {
        if (mAppDatabase == null)
        mAppDatabase = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "myjuet.db")
                .addMigrations(AppDatabase.MIGRATION_1_2)
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();
    return mAppDatabase;
    }

    public abstract AttendenceDataDao AttendenceDao();
    public abstract AttendenceDetailsDao AttendenceDetailsDao();
    public abstract DateSheetDao DateSheetDao();
    private static final Migration MIGRATION_1_2 =
            new Migration(1, 2) {
                @Override
                public void migrate(@NonNull final SupportSQLiteDatabase database) {
                    database.execSQL("CREATE TABLE IF NOT EXISTS `DateSheet` (`id` TEXT, `subjectCode` TEXT NOT NULL, `subjectName` TEXT, `date` TEXT, `time` TEXT, PRIMARY KEY(`subjectCode`))");
                }
            };
}
