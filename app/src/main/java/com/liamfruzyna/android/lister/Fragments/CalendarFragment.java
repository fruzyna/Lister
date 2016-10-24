package com.liamfruzyna.android.lister.Fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Spinner;

import com.liamfruzyna.android.lister.Data.Data;
import com.liamfruzyna.android.lister.Data.Item;
import com.liamfruzyna.android.lister.Data.Util;
import com.liamfruzyna.android.lister.Data.WishList;
import com.liamfruzyna.android.lister.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.liamfruzyna.android.lister.R.styleable.Spinner;

/**
 * Created by mail929 on 10/10/16.
 */

public class CalendarFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener
{
    View view;
    List<LinearLayout> weeks;
    int first;
    Calendar cal;
    LinearLayout calendar;
    Spinner monthSpinner;
    LayoutInflater infl;
    String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    @Override
    public View onCreateView(LayoutInflater infl, ViewGroup parent, Bundle savedInstanceState)
    {
        view = infl.inflate(R.layout.fragment_calendar, parent, false);

        getActivity().setTitle("Calendar View");

        monthSpinner = (Spinner) view.findViewById(R.id.month);
        ArrayAdapter<String> sadapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, months);
        sadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(sadapter);
        monthSpinner.setOnItemSelectedListener(this);

        this.infl = infl;

        weeks = new ArrayList<>();
        cal = Calendar.getInstance();
        calendar = (LinearLayout) view.findViewById(R.id.days);

        updateCalendar();

        return view;
    }

    public void updateCalendar()
    {
        calendar.removeAllViews();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        first = cal.get(Calendar.DAY_OF_WEEK);
        switch(first)
        {
            case Calendar.MONDAY:
                first = 1;
                break;
            case Calendar.TUESDAY:
                first = 0;
                break;
            case Calendar.WEDNESDAY:
                first = -1;
                break;
            case Calendar.THURSDAY:
                first = -2;
                break;
            case Calendar.FRIDAY:
                first = -3;
                break;
            case Calendar.SATURDAY:
                first = -4;
                break;
            case Calendar.SUNDAY:
                first = -5;
                break;
        }

        for(int i = 0; i < 6; i++)
        {
            LinearLayout week = (LinearLayout) infl.inflate(R.layout.view_week, calendar, false);
            if(i % 2 == 0)
            {
                week.setBackgroundColor(Color.WHITE);
            }
            LinearLayout monday = (LinearLayout) week.findViewById(R.id.monday);
            monday.addView(getDay());
            monday.addView(makeCount());
            monday.setOnClickListener(this);
            LinearLayout tuesday = (LinearLayout) week.findViewById(R.id.tuesday);
            tuesday.addView(getDay());
            tuesday.addView(makeCount());
            tuesday.setOnClickListener(this);
            LinearLayout wednesday = (LinearLayout) week.findViewById(R.id.wednesday);
            wednesday.addView(getDay());
            wednesday.addView(makeCount());
            wednesday.setOnClickListener(this);
            LinearLayout thursday = (LinearLayout) week.findViewById(R.id.thursday);
            thursday.addView(getDay());
            thursday.addView(makeCount());
            thursday.setOnClickListener(this);
            LinearLayout friday = (LinearLayout) week.findViewById(R.id.friday);
            friday.addView(getDay());
            friday.addView(makeCount());
            friday.setOnClickListener(this);
            LinearLayout saturday = (LinearLayout) week.findViewById(R.id.saturday);
            saturday.addView(getDay());
            saturday.addView(makeCount());
            saturday.setOnClickListener(this);
            LinearLayout sunday = (LinearLayout) week.findViewById(R.id.sunday);
            sunday.addView(getDay());
            sunday.addView(makeCount());
            sunday.setOnClickListener(this);
            calendar.addView(week);
        }
    }

    public TextView getDay()
    {
        TextView view = new TextView(getActivity());
        if(first > 0 && first <= cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        {
            view.setText(first + "");
        }
        first++;
        cal.set(Calendar.DAY_OF_MONTH, first - 1);
        return view;
    }

    public TextView makeCount()
    {
        TextView view = new TextView(getActivity());
        int count = 0;
        if(first > 1 && first <= cal.getActualMaximum(Calendar.DAY_OF_MONTH) + 1)
        {
            count = getInstances(Util.getDate(cal.getTime()));
            view.setText(count + "");
        }
        view.setTextSize(20);
        return view;
    }

    public int getInstances(String date)
    {
        List<Item> items = new ArrayList<>();
        for(WishList list : Data.getLists())
        {
            if(!list.archived && !list.auto)
            {
                for(Item item : list.items)
                {
                    if(Util.getDate(item.date).equals(date) && !items.contains(item))
                    {
                        items.add(item);
                    }
                }
            }
        }
        return items.size();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
        cal.set(Calendar.MONTH, i);
        updateCalendar();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView){}

    @Override
    public void onClick(View view)
    {
    }
}