package au.edu.sydney.comp5216.running_diary.ui.runninglog;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import au.edu.sydney.comp5216.running_diary.LogItem;
import au.edu.sydney.comp5216.running_diary.LogItemAdapter;
import au.edu.sydney.comp5216.running_diary.R;
import au.edu.sydney.comp5216.running_diary.RunningLog;
import au.edu.sydney.comp5216.running_diary.RunningLogDB;
import au.edu.sydney.comp5216.running_diary.RunningLogDao;

/**
 * RunningLogFragment starts when Running Log navigation is clicked
 */
public class RunningLogFragment extends Fragment implements View.OnClickListener {

    // Set variables
    private ArrayList<LogItem> RunningLogArray;
    ArrayAdapter<LogItem> itemsAdapter;

    TextView dv, tv, pv, sv, avg_title;
    Button prev_week, next_week;

    private boolean prev, cur, prev2;

    RunningLogDB db;
    RunningLogDao runningLogDao;

    /**
     * Create Running Log view
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_runninglog, container, false);

        // Get items from database and add to RunningLogArray
        db = RunningLogDB.getDatabase(getActivity().getApplicationContext());
        runningLogDao = db.RunningLogDao();
        readItemsFromDatabase();

        // Initialize the custom adapter and connect listView with adapter
        itemsAdapter = new LogItemAdapter(getContext(), RunningLogArray);
        ListView listView = (ListView) root.findViewById(R.id.listView);
        listView.setAdapter(itemsAdapter);

        dv = (TextView) root.findViewById(R.id.logDistanceAvg);
        tv = (TextView) root.findViewById(R.id.logTimeAvg);
        pv = (TextView) root.findViewById(R.id.logPaceAvg);
        sv = (TextView) root.findViewById(R.id.logSpeedAvg);
        avg_title = (TextView) root.findViewById(R.id.weeklyavg_text);

        prev_week = (Button) root.findViewById(R.id.prev_week_btn);
        next_week = (Button) root.findViewById(R.id.next_week_btn);

        // Set onClickListener
        prev_week.setOnClickListener(this);
        next_week.setOnClickListener(this);

        // Get weekly average and display
        getWeeklyAvg("current");
        prev = false;
        cur = true;
        prev2 = false;

        return root;
    }

    /**
     * Set onclick listener for previous and next button
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // When previous button is clicked
            case R.id.prev_week_btn:
                if(cur){ // If displaying current week average
                    // Get previous week average and set text
                    getWeeklyAvg("previous");
                    next_week.setVisibility(View.VISIBLE);
                    avg_title.setText("Last Week Average");

                    cur = false;
                    prev = true;
                } else if(prev){ // If displaying previous week average
                    // Get last two week average and set text
                    getWeeklyAvg("previous2");
                    prev_week.setVisibility(View.INVISIBLE);
                    avg_title.setText("Last 2 Week Average");

                    prev2 = true;
                    prev = false;
                }
                break;

            // When next button is clicked
            case R.id.next_week_btn:
                if(prev){ // If displaying previous week average
                    // Get current week average and set text
                    getWeeklyAvg("current");
                    next_week.setVisibility(View.INVISIBLE);
                    avg_title.setText("This Week Average");

                    prev = false;
                    cur = true;
                } else if(prev2){ // If displaying previous two week average
                    // Get previous week average and set text
                    getWeeklyAvg("previous");
                    prev_week.setVisibility(View.VISIBLE);
                    avg_title.setText("Last Week Average");

                    prev = true;
                    prev2 = false;
                }
                break;
            default:
                break;
        }
    }

    /**
     * Calculate weekly average from all running logs
     * Display results
     */
    private void getWeeklyAvg(String checkWeek){
        Double distance_sum = 0.0;
        Double time_sum = 0.0;
        Double pace_sum = 0.0;
        Double speed_sum = 0.0;

        int count = 0;

        // Loop through log list and check if any log is recorded in the current week
        for (LogItem item : RunningLogArray) {
            if(isDateInWeek(item.getD_format(), checkWeek)){
                // Sum up distance, time, pace, speed, of all logs recorded in current week
                distance_sum = distance_sum + item.getDistance();
                time_sum = time_sum + item.getTimeInSecond(item.getTime());
                pace_sum = pace_sum + item.getTimeInSecond(item.getPace());
                speed_sum = speed_sum + item.getSpeed();
                count++;
            }
        }

        if(count == 0){ count = 1;}

        // Calculate the average of distance, time, pace, speed
        Double distance_avg = distance_sum/count;
        LogItem i = new LogItem();
        String time_avg = i.convertSecToHr(time_sum/count);
        String pace_avg = i.convertSecToHr(pace_sum/count);
        Double speed_avg = speed_sum/count;

        // Display result of weekly average
        dv.setText(reduceDecimal(distance_avg).toString());
        tv.setText(time_avg);
        pv.setText(pace_avg);
        sv.setText(reduceDecimal(speed_avg).toString());
    }

    /**
     * Reduce decimal to 0.000 format
     * @param d original format
     * @return 0.000 decimal format to the thousandth place
     */
    private Double reduceDecimal(Double d){
        return Math.round(d * 1000.0) / 1000.0;
    }

    /**
     * Check if date is in the current week
     * @param date
     * @return boolean
     */
    public static boolean isDateInCurrentWeek(Date date) {
        CalendarItem calendarItem = new CalendarItem();
        String[] currentWeek = calendarItem.getCurrentWeek();

        SimpleDateFormat formatter= new SimpleDateFormat("M-dd");
        String targetDate = formatter.format(date);

        for(int i=0;i<currentWeek.length;i++){
            if(currentWeek[i].equals(targetDate)){
                return true;
            }
        }
        return false;
    }

    /**
     * Check if date is in the current/previous/next week
     * @param date
     * @return boolean
     */
    public static boolean isDateInWeek(Date date, String week) {
        CalendarItem calendarItem = new CalendarItem();
        String[] checkWeek = calendarItem.getCurrentWeek();
        if(week.equals("current")){
            checkWeek = calendarItem.getCurrentWeek();
        } else if(week.equals("previous")){
            checkWeek = calendarItem.getPreviousWeek();
        } else if(week.equals("previous2")){
            checkWeek = calendarItem.getPreviousTwoWeek();
        }

        Log.d("Check date", Arrays.toString(checkWeek));

        SimpleDateFormat formatter= new SimpleDateFormat("M-dd");
        String targetDate = formatter.format(date);

        for(int i=0;i<checkWeek.length;i++){
            if(checkWeek[i].equals(targetDate)){
                return true;
            }
        }
        return false;
    }

    /**
     * Read item from database
     */
    private void readItemsFromDatabase(){
        try {
            new AsyncTask<Void, Void, Void>() {
                /**
                 * Get running log items from database and add to RunningLogArray list
                 * @param voids
                 * @return void
                 */
                @Override
                protected Void doInBackground(Void... voids) {
                    List<RunningLog> itemsFromDB = runningLogDao.listAll();
                    RunningLogArray = new ArrayList<LogItem>();
                    if (itemsFromDB != null & itemsFromDB.size() > 0) {
                        for (RunningLog item : itemsFromDB) {
                            RunningLogArray.add(item.getRunningLogItem());
                        }
                    }
                    return null;
                }
            }.execute().get();
        }
        catch(Exception ex) {
            Log.e("readItemsFromDatabase", ex.getStackTrace().toString());
        }
    }
}