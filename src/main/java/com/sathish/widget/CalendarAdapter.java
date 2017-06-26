package com.sathish.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Sathish on 6/26/2017.
 */

public class CalendarAdapter extends ArrayAdapter<Date> {

    // days with events
    private HashSet<Date> eventDays;

    // for view inflation
    private LayoutInflater inflater;

    private Context context;
    private OnInitListener onInitListener;

    public CalendarAdapter(Context context, List<Date> days, HashSet<Date> eventDays) {
        super(context, R.layout.control_calendar_day, days);
        this.eventDays = eventDays;
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    @NonNull
    public View getView(int position, View view, ViewGroup parent) {
        // day in question
        Calendar date = Calendar.getInstance();
        date.setTime(getItem(position));
        int day = date.get(Calendar.DATE);
        int month = date.get(Calendar.MONTH);
        int year = date.get(Calendar.YEAR);

        // today
        Calendar today = Calendar.getInstance();
        today.setTime(new Date());
        int today_date = today.get(Calendar.DATE);
        int today_month = today.get(Calendar.MONTH);
        int today_year = today.get(Calendar.YEAR);

        // inflate item if it does not exist yet
        if (view == null)
            view = inflater.inflate(R.layout.control_calendar_day, parent, false);

        // if this day has an event, specify event image
        view.setBackgroundResource(0);
        if (eventDays != null) {
            for (Date eventDate : eventDays) {
                // today
                Calendar event = Calendar.getInstance();
                event.setTime(eventDate);
                int e_date = event.get(Calendar.DATE);
                int e_month = event.get(Calendar.MONTH);
                int e_year = event.get(Calendar.YEAR);
                if (e_date == day && e_month == month && e_year == year) {
                    // mark this day for event
                    view.setBackgroundResource(R.drawable.reminder);
                    break;
                }
            }
        }

        // clear styling
        ((TextView)view).setTypeface(null, Typeface.NORMAL);
        ((TextView)view).setTextColor(Color.WHITE);

        if (month != today_month || year != today_year) {
            // if this day is outside current month, grey it out
            ((TextView)view).setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.greyed_out, null));
        }
        else if (day == today_date) {
            setDefaultValue(today.getTime());
            // if it is today, set it to blue/bold
            ((TextView)view).setTypeface(null, Typeface.BOLD);
            ((TextView)view).setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.today, null));
        }
        // set text
        ((TextView)view).setText(String.valueOf(day));
        return view;
    }

    public void setDefaultValue(Date date) {
        if(onInitListener != null) {
            onInitListener.onDisplayScreenUpdate(date);
        }
    }
    public void setOnInitListener(OnInitListener onInitListener) {
        this.onInitListener = onInitListener;
    }

    public interface OnInitListener {

        void onDisplayScreenUpdate(Date date);
    }
}
