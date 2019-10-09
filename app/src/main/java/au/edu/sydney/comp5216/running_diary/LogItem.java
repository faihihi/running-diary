package au.edu.sydney.comp5216.running_diary;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * LogItem class for running log item
 */
public class LogItem {

    // Set fields
    private Double distance; // km

    private String time; // [hr:min:sec]
    private String pace; // [hr:min:sec] time per km

    private Double speed; // km per hour
    private String date; // [yyyy-mm-dd at hh:mm]

    private Date d_format;
    private String title;

    /**
     * Set void constructor
     */
    public LogItem(){}

    /**
     * Set constructor initializing item when first logged
     * @param distance
     * @param time
     * @param title
     */
    public LogItem(Double distance, String time, String title){
        this.distance = Math.round(distance * 1000.0) / 1000.0;
        this.time = time;
        setPace();
        setSpeed();
        setDate();
        this.title = title;
    }

    /**
     * Set constructor re-initializing item from database
     * @param distance
     * @param time
     * @param pace
     * @param speed
     * @param date
     * @param title
     */
    public LogItem(Double distance, String time, String pace, Double speed, String date, String title){
        this.distance = Math.round(distance * 1000.0) / 1000.0;
        this.time = time;
        this.pace = pace;
        this.speed = speed;
        this.date = date;
        setDFormat();
        this.title = title;
    }

    /**
     * Set pace
     */
    private void setPace(){
        // If distance is 0, set pace as 0
        if(distance == 0){
            String temp = "00:00:00";
            this.pace = temp;
            return;
        }

        // Calculate pace, convert format and set
        Double sec_result = getTimeInSecond(this.time) / distance;
        this.pace = convertSecToHr(sec_result);
    }

    /**
     * Calculate speed and set speed
     */
    private void setSpeed(){
        Double temp = this.distance/(getTimeInSecond(this.time)/3600);
        this.speed = Math.round(temp * 1000.0) / 1000.0;
    }

    /**
     * Get current time and set date in String and Date format
     */
    private void setDate(){
        Date d = new Date(System.currentTimeMillis());
        this.d_format = d;
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm");
        String dstr = formatter.format(d);
        this.date = dstr;
    }

    /**
     * Set Date format from date String
     */
    private void setDFormat(){
        try {
            String[] split = this.date.split("at");
            String temp = split[0];
            Log.d("Check Date temp", temp);
            Date d = new SimpleDateFormat("yyyy-MM-dd").parse(temp);
            this.d_format = d;
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get time in second
     * @param t time in string format hh:mm:ss
     * @return time in second format
     */
    public Double getTimeInSecond(String t){
        Double hr, min, sec;

        // Split string
        String[] time_split = t.split(":");

        // Get hr, min, and sec
        if(time_split.length == 3){
            hr = Double.parseDouble(time_split[0]);
            min = Double.parseDouble(time_split[1]);
            sec = Double.parseDouble(time_split[2]);
        } else{
            hr = 0.0;
            min = Double.parseDouble(time_split[0]);
            sec = Double.parseDouble(time_split[1]);
        }

        // Calculate seconds and return result
        Double sec_result = (Double) ((((hr * 60) + min) * 60) + sec);
        return sec_result;
    }

    /**
     * Convert seconds to format of hh:mm:ss in String
     * @param sec_result
     * @return
     */
    public String convertSecToHr(Double sec_result){
        Double hr_ans, min_ans, sec_ans;

        if(sec_result / 60 >= 60){
            hr_ans = sec_result / 3600;
            min_ans = (hr_ans - getInteger(hr_ans)) * 60;
            sec_ans = (min_ans - getInteger(min_ans)) * 60;
        } else{
            hr_ans = 0.0;
            min_ans = sec_result / 60;
            sec_ans = (min_ans - getInteger(min_ans)) * 60;
        }

        String hr_str = String.valueOf((int) Math.floor(hr_ans));
        String min_str = String.valueOf((int) Math.floor(min_ans));
        String sec_str = String.valueOf((int) Math.round(sec_ans));

        if(hr_str.length() == 1){hr_str = "0" + hr_str;}
        if(min_str.length() == 1){min_str = "0" + min_str;}
        if(sec_str.length() == 1){sec_str = "0" + sec_str;}

        return hr_str + ":" + min_str + ":" + sec_str;
    }

    /**
     * Get distance
     * @return distance in Double
     */
    public Double getDistance() {return distance; }

    /**
     * Get Speed
     * @return speed in Double
     */
    public Double getSpeed() {return speed; }

    /**
     * Get Pace
     * @return pace in String
     */
    public String getPace() {return pace; }

    /**
     * Get time
     * @return time in String hh:mm:ss
     */
    public String getTime() {return time; }

    /**
     * Get date
     * @return date in String yyyy-mm-dd at hh:mm
     */
    public String getDate() {return date; }

    /**
     * Get date format
     * @return Date
     */
    public Date getD_format() {return d_format;}

    /**
     * Get title
     * @return title in String
     */
    public String getTitle() {return title; }

    /**
     * Get first digit of number from decimal number (ex. "b" from "ab.cd")
     * @param num
     * @return first digit of input number in Double
     */
    protected Double getInteger(Double num){
        String doubleAsString = String.valueOf(num);
        int indexOfDecimal = doubleAsString.indexOf(".");
        doubleAsString = doubleAsString.substring(0, indexOfDecimal);
        return Double.parseDouble(doubleAsString);
    }
}
