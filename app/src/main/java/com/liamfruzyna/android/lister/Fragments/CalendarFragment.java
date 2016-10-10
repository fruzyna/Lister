package com.liamfruzyna.android.lister.Fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liamfruzyna.android.lister.Data.Data;
import com.liamfruzyna.android.lister.Data.Item;
import com.liamfruzyna.android.lister.Data.WishList;
import com.liamfruzyna.android.lister.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by mail929 on 10/10/16.
 */

public class CalendarFragment extends Fragment
{
    View view;
    List<LinearLayout> weeks;
    int first;
    Calendar cal;

    @Override
    public View onCreateView(LayoutInflater infl, ViewGroup parent, Bundle savedInstanceState)
    {
        view = infl.inflate(R.layout.fragment_calendar, parent, false);

        weeks = new ArrayList<>();
        cal = Calendar.getInstance();
        LinearLayout calendar = (LinearLayout) view.findViewById(R.id.calendar);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        first = -(cal.get(Calendar.DAY_OF_WEEK))+3;
        if(first == 0)
        {
            first = -7;
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
            LinearLayout tuesday = (LinearLayout) week.findViewById(R.id.tuesday);
            tuesday.addView(getDay());
            tuesday.addView(makeCount());
            LinearLayout wednesday = (LinearLayout) week.findViewById(R.id.wednesday);
            wednesday.addView(getDay());
            wednesday.addView(makeCount());
            LinearLayout thursday = (LinearLayout) week.findViewById(R.id.thursday);
            thursday.addView(getDay());
            thursday.addView(makeCount());
            LinearLayout friday = (LinearLayout) week.findViewById(R.id.friday);
            friday.addView(getDay());
            friday.addView(makeCount());
            LinearLayout saturday = (LinearLayout) week.findViewById(R.id.saturday);
            saturday.addView(getDay());
            saturday.addView(makeCount());
            LinearLayout sunday = (LinearLayout) week.findViewById(R.id.sunday);
            sunday.addView(getDay());
            sunday.addView(makeCount());
            calendar.addView(week);
        }
        return view;
    }

    public TextView getDay()
    {
        TextView view = new TextView(getActivity());
        if(first > 0 && first <= cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        {
            view.setText(first + "");
        }
        first++;
        cal.set(Calendar.DAY_OF_MONTH, first);
        return view;
    }

    public TextView makeCount()
    {
        TextView view = new TextView(getActivity());
        int count = 0;
        if(first > 0 && first <= cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        {
            count = getInstances(getDate(cal.getTime()));
            view.setText(count + "");
        }
        if(count > 5)
        {
            view.setTextColor(Color.RED);
        }
        else if(count < 3)
        {
            view.setTextColor(Color.GREEN);
        }
        else if(count == 0)
        {
            view.setText("q");
        }
        else
        {
            view.setTextColor(Color.CYAN);
        }
        return view;
    }

    public int getInstances(String string)
    {
        int count = 0;
        for(WishList list : Data.getLists())
        {
            if (!list.archived)
            {
                for (Item item : list.items)
                {
                    //System.out.println("Comparing " + string + " & " + getDate(item.date));
                    if(string.equals(getDate(item.date)))
                    {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public static String getDate(Date date)
    {
        String[] array = date.toString().split(" ");
        return array[1] + " " + array[2] + " " + array[5];
    }
}