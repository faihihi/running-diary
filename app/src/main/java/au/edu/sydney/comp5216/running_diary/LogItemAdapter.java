package au.edu.sydney.comp5216.running_diary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.ArrayList;

/**
 * Custom adapter for using with LogItem object model
 */
public class LogItemAdapter extends ArrayAdapter<LogItem> {
    public LogItemAdapter(Context context, ArrayList<LogItem> items) {
        super(context, 0, items);
    }

    /**
     * Get view using Item object
     * @param convertView
     * @param parent
     * @param position
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        LogItem item = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.logitem_list, parent, false);
        }

        // Lookup view for data population
        TextView logDate = (TextView) convertView.findViewById(R.id.logDate);
        TextView logDistance = (TextView) convertView.findViewById(R.id.logDistance);
        TextView logTime = (TextView) convertView.findViewById(R.id.logTime);
        TextView logPace = (TextView) convertView.findViewById(R.id.logPace);
        TextView logSpeed = (TextView) convertView.findViewById(R.id.logSpeed);

        // Populate the data into the template view using the data object
        logDate.setText(item.getDate());
        logDistance.setText(item.getDistance().toString());
        logTime.setText(item.getTime());
        logPace.setText(item.getPace());
        logSpeed.setText(item.getSpeed().toString());

        // Return the completed view to render on screen
        return convertView;
    }
}
