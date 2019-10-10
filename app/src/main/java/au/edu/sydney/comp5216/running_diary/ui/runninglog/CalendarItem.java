package au.edu.sydney.comp5216.running_diary.ui.runninglog;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

public class CalendarItem {
    private Calendar calendarItem;

    public String[] getCurrentWeek() {
        this.calendarItem = Calendar.getInstance();
        this.calendarItem.setFirstDayOfWeek(Calendar.SUNDAY);
        this.calendarItem.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return getNextWeek();
    }
    public String[] getNextWeek() {
        DateFormat format = new SimpleDateFormat("M-dd");
        String[] days = new String[7];
        for (int i = 0; i < 7; i++) {
            days[i] = format.format(this.calendarItem.getTime());
            this.calendarItem.add(Calendar.DATE, 1);
        }
        return days;
    }
    public String[] getPreviousWeek() {
        this.calendarItem.add(Calendar.DATE, -14);
        return getNextWeek();
    }

    public String[] getPreviousTwoWeek() {
        this.calendarItem.add(Calendar.DATE, -21);
        return getNextWeek();
    }
    public void checkCalendar() {
        CalendarItem t = new CalendarItem();
        System.out.println("Current : " + Arrays.toString(t.getCurrentWeek()));
        System.out.println("Previous: " + Arrays.toString(t.getPreviousWeek()));
        System.out.println("Previous: " + Arrays.toString(t.getPreviousWeek()));
        System.out.println("Next    : " + Arrays.toString(t.getNextWeek()));
        System.out.println("Next    : " + Arrays.toString(t.getNextWeek()));
    }
}
