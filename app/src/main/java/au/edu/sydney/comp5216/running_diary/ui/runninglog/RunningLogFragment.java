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

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

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

public class RunningLogFragment extends Fragment {

    private ArrayList<LogItem> RunningLogArray;
    ArrayAdapter<LogItem> itemsAdapter;

    TextView dv, tv, pv, sv;

    RunningLogDB db;
    RunningLogDao runningLogDao;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_runninglog, container, false);

        db = RunningLogDB.getDatabase(getActivity().getApplicationContext());
        runningLogDao = db.RunningLogDao();
        readItemsFromDatabase();

        /**
         * Initialize the custom adapter and connect listView with adapter
         */
        itemsAdapter = new LogItemAdapter(getContext(), RunningLogArray);
        ListView listView = (ListView) root.findViewById(R.id.listView);
        listView.setAdapter(itemsAdapter);

        dv = (TextView) root.findViewById(R.id.logDistanceAvg);
        tv = (TextView) root.findViewById(R.id.logTimeAvg);
        pv = (TextView) root.findViewById(R.id.logPaceAvg);
        sv = (TextView) root.findViewById(R.id.logSpeedAvg);

        getWeeklyAvg();

        return root;
    }

    private void getWeeklyAvg(){
        Double distance_sum = 0.0;
        Double time_sum = 0.0;
        Double pace_sum = 0.0;
        Double speed_sum = 0.0;

        int count = 0;

        for (LogItem item : RunningLogArray) {
            if(isDateInCurrentWeek(item.getD_format())){
                Log.d("Check if any date is in this week", "YESSS");
                distance_sum = distance_sum + item.getDistance();
                time_sum = time_sum + item.getSecond(item.getTime());
                pace_sum = pace_sum + item.getSecond(item.getPace());
                speed_sum = speed_sum + item.getSpeed();
                count++;
            }
        }

        if(count == 0){ count = 1;}

        Double distance_avg = distance_sum/count;
        Double time_avg = time_sum/count;
        Double pace_avg = pace_sum/count;
        Double speed_avg = speed_sum/count;

        dv.setText(reduceDecimal(distance_avg).toString());
        tv.setText(reduceDecimal(time_avg).toString());
        pv.setText(reduceDecimal(pace_avg).toString());
        sv.setText(reduceDecimal(speed_avg).toString());
    }

    private Double reduceDecimal(Double d){
        return Math.round(d * 1000.0) / 1000.0;
    }

    public static boolean isDateInCurrentWeek(Date date) {
        Calendar currentCalendar = Calendar.getInstance();
        int week = currentCalendar.get(Calendar.WEEK_OF_YEAR);
        int year = currentCalendar.get(Calendar.YEAR);

        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTime(date);
        int targetWeek = targetCalendar.get(Calendar.WEEK_OF_YEAR);
        int targetYear = targetCalendar.get(Calendar.YEAR);

        return week == targetWeek && year == targetYear;
    }

    private void readItemsFromDatabase(){
        try {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    List<RunningLog> itemsFromDB = runningLogDao.listAll();
                    RunningLogArray = new ArrayList<LogItem>();
                    if (itemsFromDB != null & itemsFromDB.size() > 0) {
                        for (RunningLog item : itemsFromDB) {
                            RunningLogArray.add(item.getRunningLogItem());

                            Log.i("SQLite read item", "Distance: " + item.getDistance() + "Time: " + item.getTime());
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