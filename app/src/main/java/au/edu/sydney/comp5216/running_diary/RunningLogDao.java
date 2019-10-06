package au.edu.sydney.comp5216.running_diary;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RunningLogDao {
    @Query("SELECT * FROM runninglog")
    List<RunningLog> listAll();

    @Insert
    void insert(RunningLog runningLog);

    @Insert
    void insertAll(RunningLog... runningLogs);

    @Query("DELETE FROM runninglog")
    void deleteAll();
}
