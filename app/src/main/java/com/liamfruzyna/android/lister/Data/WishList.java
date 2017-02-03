package com.liamfruzyna.android.lister.Data;

import com.liamfruzyna.android.lister.Fragments.WLFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by mail929 on 11/6/14.
 */
public class WishList
{
    public List<Item> items = new ArrayList<>();
    public List<String> tags = new ArrayList<>();
    public List<String> people = new ArrayList<>();
    public Date date = new Date(1097, 3, 24);
    public String formattedDate = "NONE";
    public String name;
    public boolean auto = false;
    public int order = 0;
    public int daysToDelete = 0;
    public boolean archived = false;
    public boolean showDone;

    public WishList(String name, List<Item> items, List<String> tags, boolean archived, int order, boolean showDone, int daysToDelete)
    {
        this.name = name;
        this.items = items;
        this.tags = tags;
        this.archived = archived;
        this.order = order;
        this.showDone = showDone;
        this.daysToDelete = daysToDelete;
        findPeople();
        findDate();
        deleteItems();
    }

    public WishList(String name, List<String> tags, boolean archived, int order, boolean showDone, int daysToDelete)
    {
        this.name = name;
        this.tags = tags;
        this.archived = archived;
        this.order = order;
        this.showDone = showDone;
        this.daysToDelete = daysToDelete;
        findPeople();
        findDate();
        deleteItems();
    }

    public WishList(String name, List<String> tags, boolean showDone, int daysToDelete)
    {
        this.name = name;
        this.tags = tags;
        this.showDone = showDone;
        this.daysToDelete = daysToDelete;
        order = 0;
        for(WishList list : Data.getLists())
        {
            if(list.order > order)
            {
                order = list.order;
            }
        }
        order++;
        findPeople();
        findDate();
        deleteItems();
    }

    public void deleteItems()
    {
        List<Item> toRemove = new ArrayList<>();
        for(Item item : items)
        {
            if(item.deleteItem(daysToDelete))
            {
                toRemove.add(item);
            }
        }
        for(Item item : toRemove)
        {
            items.remove(item);
        }
    }

    public void findPeople()
    {
        for(String tag : tags)
        {
            if(tag.length() > 0)
            {
                if(tag.charAt(0) == '@')
                {
                    people.add(tag.replace("@", ""));
                }
            }
        }
    }

    public void findDate()
    {
        for(String tag : tags)
        {
            if(tag.contains("/"))
            {
                //date
                String[] date = tag.split("/");

                if (date.length == 3)
                {
                    //if the date has month day and year
                    int day = Integer.parseInt(date[1]);
                    int month;
                    int year;
                    if (date[0].contains(" "))
                    {
                        String[] start = date[0].split(" ");
                        month = Integer.parseInt(start[start.length - 1]);
                    }
                    else
                    {
                        month = Integer.parseInt(date[0]);
                    }
                    month -= 1;
                    if (date[2].contains(" "))
                    {
                        String[] end = date[2].split(" ");
                        year = Integer.parseInt(end[0]);
                    }
                    else
                    {
                        year = Integer.parseInt(date[2]);
                    }
                    formattedDate = (month + 1) + "/" + day + "/" + year;
                    if (year < 2000)
                    {
                        year += 2000;
                    }
                    IO.log("Item:parseItem", "Found date: " + month + "/" + day + "/" + year);
                    this.date = new Date(year, month, day);
                }
                else if (date.length == 2)
                {
                    //if the date just has month and day
                    int day;
                    int month;
                    int year;
                    if (date[0].contains(" "))
                    {
                        String[] start = date[0].split(" ");
                        month = Integer.parseInt(start[start.length - 1]);
                    }
                    else
                    {
                        month = Integer.parseInt(date[0]);
                    }
                    month -= 1;
                    if (date[1].contains(" "))
                    {
                        String[] end = date[1].split(" ");
                        day = Integer.parseInt(end[0]);
                    }
                    else
                    {
                        day = Integer.parseInt(date[1]);
                    }
                    year = Calendar.getInstance().getTime().getYear();
                    IO.log("Item:parseItem", "Found date: " + month + "/" + day + "/" + year);
                    this.date = new Date(year, month, day);
                    formattedDate = (1 + month) + "/" + day;
                }
            }
        }
    }

    public boolean equals(WishList list)
    {
        boolean allMatch = true;
        if(list.items.size() != items.size())
        {
            return false;
        }
        for(Item item : items)
        {
            boolean match = false;
            for(Item newItem : list.items)
            {
                if(newItem.equals(item))
                {
                    match = true;
                }
            }
            if(!match)
            {
                return false;
            }
        }

        return true;
    }
}
