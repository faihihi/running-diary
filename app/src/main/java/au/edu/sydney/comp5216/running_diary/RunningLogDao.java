package au.edu.sydney.comp5216.running_diary;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * RunningLog Data Access Object interface
 */
@Dao
public interface RunningLogDao {
    /**
     * Query to select all items from running log
     * @return List of running logs
     */
    @Query("SELECT * FROM runninglog")
    List<RunningLog> listAll();

    /**
     * Insert new log item
     * @param runningLog
     */
    @Insert
    void insert(RunningLog runningLog);

    /**
     * Insert all running logs
     * @param runningLogs
     */
    @Insert
    void insertAll(RunningLog... runningLogs);

    /**
     * Delete all items from runninglog db
     */
    @Query("DELETE FROM runninglog")
    void deleteAll();
}
