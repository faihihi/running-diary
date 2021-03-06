package au.edu.sydney.comp5216.running_diary;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

/**
 * RunningLogDB database class for connecting with device database
 */
@Database(entities = {RunningLog.class}, version = 1, exportSchema = false)
public abstract class RunningLogDB extends RoomDatabase{
    private static final String DATABASE_NAME = "runninglog_db";
    private static RunningLogDB DBINSTANCE;

    /**
     * RunningLog Data Access Object
     * @return Dao object
     */
    public abstract RunningLogDao RunningLogDao();

    /**
     * Get database of "runninglog_db"
     * @param context
     * @return database instance
     */
    public static RunningLogDB getDatabase(Context context) {
        if (DBINSTANCE == null) {
            synchronized (RunningLogDB.class) {
                DBINSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        RunningLogDB.class, DATABASE_NAME).build();
            }
        }
        return DBINSTANCE;
    }

    /**
     * Destroy database instance
     */
    public static void destroyInstance() {
        DBINSTANCE = null;
    }
}
