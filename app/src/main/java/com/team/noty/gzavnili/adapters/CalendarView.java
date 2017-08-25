package com.team.noty.gzavnili.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.team.noty.gzavnili.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

import io.paperdb.Paper;

/**
 * Created by a7med on 28/06/2015.
 */
public class CalendarView extends LinearLayout
{

	// how many days to show, defaults to six weeks, 42 days
	private static final int DAYS_COUNT = 42;

	// default date format
	private static final String DATE_FORMAT = "MM";

	// date format
	private String dateFormat;

	// current displayed month
	private Calendar currentDate = Calendar.getInstance();

	//event handling
	private EventHandler eventHandler = null;

	private GridView grid;

	static final int PAGE_COUNT = 12;
	public HashSet<Date> eventDays;
	int nowPosition;

	ImageView btn_left, btn_right;
	TextView curent_month;

	// month-season association (northern hemisphere, sorry australia :)
	String[] month =  {"January", "February", "March", "April",  "May", "June", "Jule", "August",
			"September", "October", "November", "December"};

	public CalendarView(Context context)
	{
		super(context);
	}

	public CalendarView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initControl(context, attrs);
	}

	public CalendarView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		initControl(context, attrs);
	}

	/**
	 * Load control xml layout
	 */
	private void initControl(Context context, AttributeSet attrs)
	{
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.control_calendar, this);

		loadDateFormat(attrs);
		assignUiElements();

		assignClickHandlers();
		updateCalendar();

		Paper.init(context);
	}

	private void loadDateFormat(AttributeSet attrs)
	{
		TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CalendarView);

		try
		{
			// try to load provided date format, and fallback to default otherwise
			dateFormat = DATE_FORMAT;
		}
		finally
		{
			ta.recycle();
		}
	}
	private void assignUiElements()
	{

		curent_month = (TextView) findViewById(R.id.calendar_date_display);

		grid = (GridView) findViewById(R.id.calendar_grid);

		btn_left = (ImageView) findViewById(R.id.btn_left);
		btn_right = (ImageView) findViewById(R.id.btn_right);

		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
		nowPosition = Integer.parseInt(sdf.format(currentDate.getTime())) - 1;

		curent_month.setText(month[nowPosition]);

	}

	private void assignClickHandlers()
	{
		btn_right.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

				currentDate.add(Calendar.MONTH, 1);
				updateCalendar();
			}
		});

		btn_left.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				currentDate.add(Calendar.MONTH, -1);
				updateCalendar();
			}
		});

		grid.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				if (eventDays != null) {
					for (Date eventDate : eventDays) {
						if (eventDate.getMonth() == ((Date) parent.getItemAtPosition(position)).getMonth()) {
							if (eventDate.getDate() == ((Date) parent.getItemAtPosition(position)).getDate()) {
								if (eventDate.getYear() == ((Date) parent.getItemAtPosition(position)).getYear()) {
									if (eventHandler != null) {
										eventHandler.onDayClick((Date) parent.getItemAtPosition(position));
										int oldPosition = Paper.book().read("position", -1);
										if (oldPosition != -1){
											View text = parent.getChildAt(oldPosition);
											((TextView) text).setBackgroundColor(getResources().getColor(R.color.back_calendar));
											((TextView) text).setTextColor(getResources().getColor(R.color.colorPrimary));
										}
										((TextView) view).setBackgroundResource(R.drawable.select_day);
										((TextView) view).setTextColor(getResources().getColor(R.color.white));
										Paper.book().write("position", position);

									}
								}
							}
						}
					}
				}
			}

		});
	}

	/**
	 * Display dates correctly in grid
	 */
	public void updateCalendar()
	{
		updateCalendar(null, 0);
	}

	/**
	 * Display dates correctly in grid
	 */
	public void updateCalendar(HashSet<Date> events, int update)
	{
		ArrayList<Date> cells = new ArrayList<>();
		Calendar calendar = (Calendar)currentDate.clone();

		// determine the cell for current month's beginning
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1;

		// move calendar backwards to the beginning of the week
		calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

		// fill cells
		while (cells.size() < DAYS_COUNT)
		{
			cells.add(calendar.getTime());
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		Paper.book().delete("position");
		grid.setAdapter(new CalendarAdapter(getContext(), cells, events, update));

		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		curent_month.setText(month[Integer.parseInt(sdf.format(currentDate.getTime())) - 1]);

	}
	private class CalendarAdapter extends ArrayAdapter<Date>
	{
		// days with events

		// for view inflation
		private LayoutInflater inflater;

		public CalendarAdapter(Context context, ArrayList<Date> days, HashSet<Date> eventDay, int update)
		{
			super(context, R.layout.control_calendar_day, days);

			if (update == 1) {
				eventDays = eventDay;
			}
			inflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View view, ViewGroup parent)
		{
			// day in question
			Date date = getItem(position);
			int day = date.getDate();
			int month = date.getMonth();
			int year = date.getYear();

			// today
			Date today = new Date();

			// inflate item if it does not exist yet
			if (view == null)
				view = inflater.inflate(R.layout.control_calendar_day, parent, false);

			// if this day has an event, specify event image
			view.setBackgroundResource(0);

			// clear styling
			((TextView)view).setTypeface(null, Typeface.NORMAL);
			((TextView)view).setTextColor(getResources().getColor(R.color.color_text_day));

			if (month != today.getMonth() || year != today.getYear())
			{
				// if this day is outside current month, grey it out
				((TextView)view).setTextColor(getResources().getColor(R.color.color_text_out_day));
			}


			// set text
			((TextView)view).setText(String.valueOf(date.getDate()));
			if (eventDays != null)
			{
				for (Date eventDate : eventDays)
				{
					if (eventDate.getDate() == day &&
							eventDate.getMonth() == month &&
							eventDate.getYear() == year)
					{
						SpannableString content = new SpannableString(String.valueOf(date.getDate()));
						content.setSpan(new UnderlineSpan(), 0, String.valueOf(date.getDate()).length(), 0);
						((TextView)view).setText(content);
						((TextView)view).setTypeface(null, Typeface.BOLD);
						((TextView)view).setTextColor(getResources().getColor(R.color.colorPrimary));
						break;
					}
				}
			}


			return view;
		}
	}

	/**
	 * Assign event handler to be passed needed events
	 */
	public void setEventHandler(EventHandler eventHandler)
	{
		this.eventHandler = eventHandler;
	}

	/**
	 * This interface defines what events to be reported to
	 * the outside world
	 */
	public interface EventHandler
	{
		void onDayClick(Date date);
	}
}
