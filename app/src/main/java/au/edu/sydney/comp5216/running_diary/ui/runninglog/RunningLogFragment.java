package au.edu.sydney.comp5216.running_diary.ui.runninglog;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Calendar;
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
public class RunningLogFragment extends Fragment {

    // Set variables
    private ArrayList<LogItem> RunningLogArray;
    ArrayAdapter<LogItem> itemsAdapter;

    TextView dv, tv, pv, sv;

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

        // Get weekly average and display
        getWeeklyAvg();

        return root;
    }

    /**
     * Calculate weekly average from all running logs
     * Display results
     */
    private void getWeeklyAvg(){
        Double distance_sum = 0.0;
        Double time_sum = 0.0;
        Double pace_sum = 0.0;
        Double speed_sum = 0.0;

        int count = 0;

        // Loop through log list and check if any log is recorded in the current week
        for (LogItem item : RunningLogArray) {
            if(isDateInCurrentWeek(item.getD_format())){
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
        // Get current week and year
        Calendar currentCalendar = Calendar.getInstance();
        int week = currentCalendar.get(Calendar.WEEK_OF_YEAR);
        int year = currentCalendar.get(Calendar.YEAR);

        // Set input week and year
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTime(date);
        int targetWeek = targetCalendar.get(Calendar.WEEK_OF_YEAR);
        int targetYear = targetCalendar.get(Calendar.YEAR);

        // Compare and return true if in the same week and year
        return week == targetWeek && year == targetYear;
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