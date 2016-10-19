package com.liamfruzyna.android.lister.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liamfruzyna.android.lister.Data.Item;
import com.liamfruzyna.android.lister.Data.WishList;
import com.liamfruzyna.android.lister.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mail929 on 11/6/15.
 */
public class DatesFragment extends TagFragment
{

    //finds all the different people in unarchived lists
    @Override
    public List<String> getTags()
    {
        List<String> dates = new ArrayList<>();
        for(WishList list : lists)
        {
            if (!list.archived)
            {
                for (Item item : list.items)
                {
                    if(!dates.contains(getDate(item.date)) && !getDate(item.date).contains("Apr 24 2997"))
                    {
                        dates.add(getDate(item.date));
                    }
                }
                String date = getDate(list.date);
                if(!dates.contains(date) && !date.contains("Apr 24 2997"))
                {
                    dates.add(date);
                }
            }
        }
        return dates;
    }

    @Override
    public View onCreateView(LayoutInflater infl, ViewGroup parent, Bundle savedInstanceState)
    {
        View v = super.onCreateView(infl, parent, savedInstanceState);
        getActivity().setTitle("Date Viewer");
        return v;
    }

    //Gets all the items in unarchived lists containing a name
    @Override
    public List<Item> getTagItems(String person)
    {
        List<Item> items = new ArrayList<>();
        for(WishList list : lists)
        {
            if(!list.archived && !list.auto)
            {
                for(Item item : list.items)
                {
                    if((getDate(item.date).equals(person) && !items.contains(item)) || getDate(list.date).equals(person))
                    {
                        items.add(item);
                    }
                }
            }
        }
        return items;
    }

    public static String getDate(Date date)
    {
        String[] array = date.toString().split(" ");
        return array[1] + " " + array[2] + " " + array[5];
    }
}
