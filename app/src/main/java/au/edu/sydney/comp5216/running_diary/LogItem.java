package au.edu.sydney.comp5216.running_diary;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogItem {
    private Double distance;

    private String time; //[hr:min:sec]
    private String pace; //[hr:min:sec] time per km

    private Double speed; //km per hour
    private String date; //[yyyy-mm-dd]

    public LogItem(Double distance, String time){
        this.distance = distance;
        this.time = time;
        setPace();
        setSpeed();
        setDate();
    }

    private void setPace(){
        if(distance == 0){
            String temp = "00:00:00";
            this.pace = temp;
            return;
        }

        Double hr_ans, min_ans, sec_ans;
        Double sec_result = getTimeInSecond();

        if(sec_result / 60 >= 60){
            hr_ans = sec_result / 3600;
            min_ans = (hr_ans - getInteger(hr_ans)) * 60;
            sec_ans = (min_ans - getInteger(min_ans)) * 60;
        } else{
            hr_ans = 0.0;
            min_ans = sec_result / 60;
            sec_ans = (min_ans - getInteger(min_ans)) * 60;
        }

        this.pace = String.valueOf((int) Math.floor(hr_ans)) + ":" + String.valueOf((int) Math.floor(min_ans)) + ":" + String.valueOf((int) Math.round(sec_ans));
    }

    private void setSpeed(){
        this.speed = this.distance/(getTimeInSecond()/3600);
    }

    private void setDate(){
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date(System.currentTimeMillis());
        String dstr = formatter.format(d);
        this.date = dstr;
    }

    public Double getTimeInSecond(){
        Double hr, min, sec;

        String[] time_split = time.split(":");

        if(time_split.length == 3){
            hr = Double.parseDouble(time_split[0]);
            min = Double.parseDouble(time_split[1]);
            sec = Double.parseDouble(time_split[2]);
        } else{
            hr = 1.0;
            min = Double.parseDouble(time_split[0]);
            sec = Double.parseDouble(time_split[1]);
        }

        Double sec_result = (Double) ((((hr * 60) + min) * 60) + sec) / distance;

        return sec_result;
    }

    public Double getDistance() {return distance; }
    public Double getSpeed() {return speed; }
    public String getPace() {return pace; }
    public String getTime() {return time; }
    public String getDate() {return date; }

    protected Double getInteger(Double num){
        String doubleAsString = String.valueOf(num);
        int indexOfDecimal = doubleAsString.indexOf(".");
        doubleAsString = doubleAsString.substring(0, indexOfDecimal);
        return Double.parseDouble(doubleAsString);
    }

}