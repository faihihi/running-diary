package au.edu.sydney.comp5216.running_diary;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Running Log Entity class
 */
@Entity(tableName = "runninglog")
public class RunningLog {
    // Set primary key variable
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "runningLogID")
    private int runningLogID;

    // Set columns variables
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

    /**
     * RunningLog Constructor
     * @param distance
     * @param time
     * @param pace
     * @param date
     * @param speed
     * @param title
     */
    public RunningLog(Double distance, String time, String pace, String date, Double speed, String title){
        this.distance = distance;
        this.time = time;
        this.pace = pace;
        this.date = date;
        this.speed = speed;
        this.title = title;
    }

    /**
     * Get running log item
     * @return LogItem
     */
    public LogItem getRunningLogItem(){
        LogItem newItem = new LogItem(this.distance, this.time, this.pace, this.speed, this.date, this.title);
        return newItem;
    }

    /**
     * Get running log id
     * @return id int
     */
    public int getRunningLogID() {
        return runningLogID;
    }

    /**
     * Get distance
     * @return distance
     */
    public Double getDistance() {
        return distance;
    }

    /**
     * Get time
     * @return time
     */
    public String getTime() {
        return time;
    }

    /**
     * Get pace
     * @return pace
     */
    public String getPace() {
        return pace;
    }

    /**
     * Get date
     * @return date in string
     */
    public String getDate() {
        return date;
    }

    /**
     * Get speed
     * @return speed
     */
    public Double getSpeed() {
        return speed;
    }

    /**
     * Get title
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set running log ID
     * @param runningLogID
     */
    public void setRunningLogID(int runningLogID) {
        this.runningLogID = runningLogID;
    }

    /**
     * Set date
     * @param date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Set distance
     * @param distance
     */
    public void setDistance(Double distance) {
        this.distance = distance;
    }

    /**
     * Set pace
     * @param pace
     */
    public void setPace(String pace) {
        this.pace = pace;
    }

    /**
     * Set speed
     * @param speed
     */
    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    /**
     * Set time
     * @param time
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * Set title
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }
}
