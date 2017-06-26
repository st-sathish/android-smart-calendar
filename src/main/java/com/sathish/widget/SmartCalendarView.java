package com.sathish.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 * Created by CS39 on 6/25/2017.
 */

public class SmartCalendarView extends LinearLayout implements CalendarAdapter.OnInitListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener  {

    // for logging
    private static final String LOGTAG = "Calendar View";

    // how many days to show, defaults to six weeks, 42 days
    private static final int DAYS_COUNT = 42;

    // default date format
    private static final String DATE_FORMAT = "MMM yyyy";

    // date format
    private String dateFormat = DATE_FORMAT;

    private static final String DISPLAY_MONTH_DEFAULT_FORMAT = "MMM";

    // current displayed month
    private Calendar currentDate = Calendar.getInstance();

    //event handling
    private EventHandler eventHandler = null;

    // internal components
    private LinearLayout header;
    private ImageView btnPrev;
    private ImageView btnNext;
    private TextView txtDate;
    private GridView grid;
    private TextView tvDispDate;
    private TextView tvDispMonth;

    private Context context;

    public SmartCalendarView(Context context) {
        super(context);
        this.context = context;
        initialize();
    }

    public SmartCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initialize();
    }

    public SmartCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initialize();
    }

    private void initialize() {
        initializeUI();
        updateCalendar();
    }

    private void initializeUI() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.smart_calendar, this);
        // layout is inflated, assign local variables to components
        header = (LinearLayout)findViewById(R.id.calendar_header);
        btnPrev = (ImageView)findViewById(R.id.calendar_prev_button);
        btnNext = (ImageView)findViewById(R.id.calendar_next_button);
        txtDate = (TextView)findViewById(R.id.calendar_date_display);
        grid = (GridView)findViewById(R.id.calendar_grid);
        tvDispDate = (TextView) findViewById(R.id.tv_date);
        tvDispMonth = (TextView) findViewById(R.id.tv_month);
    }

    public void updateCalendar() {
        updateCalendar(null);
    }

    /**
     * Display dates correctly in grid
     */
    public void updateCalendar(HashSet<Date> events) {
        List<Date> cells = new ArrayList<>();
        Calendar calendar = (Calendar)currentDate.clone();

        // determine the cell for current month's beginning
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        // move calendar backwards to the beginning of the week
        calendar.add(Calendar.DAY_OF_MONTH, - monthBeginningCell);

        // fill cells
        while (cells.size() < DAYS_COUNT) {
            cells.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // update grid
        CalendarAdapter calendarAdapter = new CalendarAdapter(getContext(), cells, events);
        calendarAdapter.setOnInitListener(this);
        grid.setAdapter(calendarAdapter);

        // update title
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
        txtDate.setText(sdf.format(currentDate.getTime()));

        // set header color according to current season
        int month = currentDate.get(Calendar.MONTH);
        //header.setBackgroundColor(AppC);

        grid.setOnItemClickListener(this);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        Date date = (Date)adapterView.getItemAtPosition(position);
        updateDisplayScreen(date);
        if(eventHandler != null) {
            eventHandler.onDayLongClick(date);
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Date date = (Date)adapterView.getItemAtPosition(position);
        updateDisplayScreen(date);
        if(eventHandler != null) {
            eventHandler.onDayClick(date);
        }
    }

    private void updateDisplayScreen(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        tvDispDate.setText(String.valueOf(c.get(Calendar.DATE)));

        //display month
        SimpleDateFormat sdf = new SimpleDateFormat(DISPLAY_MONTH_DEFAULT_FORMAT, Locale.ENGLISH);
        tvDispMonth.setText(sdf.format(date));
    }

    /**
     * Assign event handler to be passed needed events
     */
    public void setOnDateClickListener(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    /**
     * Assign event handler to be passed needed events
     */
    public void setOnDateLongClickListener(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    @Override
    public void onDisplayScreenUpdate(Date date) {
        updateDisplayScreen(date);
    }

    /**
     * This interface defines what events to be reported to
     * the outside world
     */
    public interface EventHandler {

        void onDayLongClick(Date date);

        void onDayClick(Date date);
    }
}
