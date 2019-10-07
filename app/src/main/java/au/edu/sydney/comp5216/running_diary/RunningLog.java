package au.edu.sydney.comp5216.running_diary;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

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
    @ColumnInfo(name = "runningLogTitle")
    private String title;

    public RunningLog(Double distance, String time, String pace, String date, Double speed, String title){
        this.distance = distance;
        this.time = time;
        this.pace = pace;
        this.date = date;
        this.speed = speed;
        this.title = title;
    }

    public LogItem getRunningLogItem(){
        LogItem newItem = new LogItem(this.distance, this.time, this.pace, this.speed, this.date, this.title);
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

    public String getTitle() {
        return title;
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

    public void setTitle(String title) {
        this.title = title;
    }
}
