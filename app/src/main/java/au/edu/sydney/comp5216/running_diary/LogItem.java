package au.edu.sydney.comp5216.running_diary;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogItem {
    private Double distance;

    private String time; //[hr:min:sec]
    private String pace; //[hr:min:sec] time per km

    private Double speed; //km per hour
    private String date; //[yyyy-mm-dd]

    private Date d_format;

    public LogItem(){}

    public LogItem(Double distance, String time){
        this.distance = Math.round(distance * 1000.0) / 1000.0;
        this.time = time;
        setPace();
        setSpeed();
        setDate();
    }

    public LogItem(Double distance, String time, String pace, Double speed, String date){
        this.distance = Math.round(distance * 1000.0) / 1000.0;
        this.time = time;
        this.pace = pace;
        this.speed = speed;
        this.date = date;
        setDFormat();
    }

    private void setPace(){
        if(distance == 0){
            String temp = "00:00:00";
            this.pace = temp;
            return;
        }
        Double sec_result = getTimeInSecond(this.time) / distance;
        this.pace = convertSecToHr(sec_result);
    }

    private void setSpeed(){
        Double temp = this.distance/(getTimeInSecond(this.time)/3600);
        this.speed = Math.round(temp * 1000.0) / 1000.0;
    }

    private void setDate(){
        Date d = new Date(System.currentTimeMillis());
        this.d_format = d;
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        String dstr = formatter.format(d);
        this.date = dstr;
    }

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

    public Double getTimeInSecond(String t){
        Double hr, min, sec;

        String[] time_split = t.split(":");

        if(time_split.length == 3){
            hr = Double.parseDouble(time_split[0]);
            min = Double.parseDouble(time_split[1]);
            sec = Double.parseDouble(time_split[2]);
        } else{
            hr = 0.0;
            min = Double.parseDouble(time_split[0]);
            sec = Double.parseDouble(time_split[1]);
        }

        Double sec_result = (Double) ((((hr * 60) + min) * 60) + sec);

        return sec_result;
    }

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

    public Double getDistance() {return distance; }
    public Double getSpeed() {return speed; }
    public String getPace() {return pace; }
    public String getTime() {return time; }
    public String getDate() {return date; }
    public Date getD_format() {return d_format;}

    protected Double getInteger(Double num){
        String doubleAsString = String.valueOf(num);
        int indexOfDecimal = doubleAsString.indexOf(".");
        doubleAsString = doubleAsString.substring(0, indexOfDecimal);
        return Double.parseDouble(doubleAsString);
    }
}
