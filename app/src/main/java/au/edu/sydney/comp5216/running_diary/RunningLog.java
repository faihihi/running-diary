package au.edu.sydney.comp5216.running_diary;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "runninglog")
public class RunningLog {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "runningLogID")
    private int runningLogID;

    @ColumnInfo(name = "runningLogDistance")
    private Double distance;
    @ColumnInfo(name = "runningLogTime")
    private String time;
    @ColumnInfo(name = "runningLogPace")
    private String pace;
    @ColumnInfo(name = "runningLogDate")
    private String date;
    @ColumnInfo(name = "runningLogSpeed")
    private Double speed;

    public RunningLog(Double distance, String time, String pace, String date, Double speed){
        this.distance = distance;
        this.time = time;
        this.pace = pace;
        this.date = date;
        this.speed = speed;
    }

    public LogItem getRunningLogItem(){
        LogItem newItem = new LogItem(this.distance,this.time);
        return newItem;
    }

    public int getRunningLogID() {
        return runningLogID;
    }

    public Double getDistance() {
        return distance;
    }

    public String getTime() {
        return time;
    }

    public String getPace() {
        return pace;
    }

    public String getDate() {
        return date;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setRunningLogID(int runningLogID) {
        this.runningLogID = runningLogID;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public void setPace(String pace) {
        this.pace = pace;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
