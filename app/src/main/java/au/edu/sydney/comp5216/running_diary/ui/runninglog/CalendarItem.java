package au.edu.sydney.comp5216.running_diary.ui.runninglog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * CalendarItem class for creating Calendar object
 */
public class CalendarItem {

    // Set fields
    private Calendar calendarItem;

    // Constructor
    public CalendarItem(){}

    /**
     * Get string of days of current week
     * @return array of string of days
     */
    public String[] getCurrentWeek() {
        this.calendarItem = Calendar.getInstance();
        this.calendarItem.setFirstDayOfWeek(Calendar.SUNDAY);
        this.calendarItem.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return getNextWeek();
    }

    /**
     * Get string of days of next week
     * @return array of strings of days
     */
    public String[] getNextWeek() {
        DateFormat format = new SimpleDateFormat("M-dd");
        String[] days = new String[7];
        for (int i = 0; i < 7; i++) {
            days[i] = format.format(this.calendarItem.getTime());
            this.calendarItem.add(Calendar.DATE, 1);
        }
        return days;
    }

    /**
     * Get string of days of previous week
     * @return array of strings of days
     */
    public String[] getPreviousWeek() {
        this.calendarItem.add(Calendar.DATE, -14);
        return getNextWeek();
    }

    /**
     * Get strings of days of last 2 week
     * @return array of strings of days
     */
    public String[] getPreviousTwoWeek() {
        this.calendarItem.add(Calendar.DATE, -21);
        return getNextWeek();
    }
}
