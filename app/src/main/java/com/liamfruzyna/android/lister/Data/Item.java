package com.liamfruzyna.android.lister.Data;

import android.provider.ContactsContract;

import com.liamfruzyna.android.lister.Activities.WLActivity;
import com.liamfruzyna.android.lister.Notifications.AlarmTask;

import java.util.Calendar;

/*this is the object for items that are added into lists
    Boolean done - whether the item is checked off or not
    Boolean archived - whether the item has been hidden
            this will be ignored if the setting is checked
            but set grey
    String item - the text in the item
*/
public class Item
{
    public Boolean done;
    public Boolean archived;
    public String item;

    //the constructor if you don't know what this is don't ask me
    public Item(String item, Boolean done, Boolean archived)
    {
        this.item = item;
        if(DataContainer.showNotifications && !archived)
        {
            findDate();
        }
        this.done = done;
        this.archived = archived;
    }

    //looks for dates in the list items and creates a notification if there is
    public void findDate()
    {
        String s = item;
        boolean found = false;
        int hour = 12;
        int min = 0;
        if(s.indexOf(":") != -1)
        {
            found = true;
            s = s.substring(0, s.indexOf(":"));
            s = s.substring(1 + s.lastIndexOf(" "));
            if(isInt(s))
            {
                hour = Integer.parseInt(s);
            }
            else
            {
                found = false;
            }
            s = item;
            s = s.substring(1 + s.indexOf(":"));
            s = s.substring(0, 2);
            if(isInt(s))
            {
                min = Integer.parseInt(s);
            }
            else
            {
                found = false;
            }
        }
        s = item;
        int month = 0;
        int day = 0;
        int year = 0;
        if(s.indexOf("/") != -1)
        {
            found = true;
            s = s.substring(0, s.indexOf("/"));
            s = s.substring(1 + s.lastIndexOf(" "));
            if(isInt(s))
            {
                month = Integer.parseInt(s) - 1;
            }
            else
            {
                found = false;
            }
            s = item;
            s = s.substring(1 + s.indexOf("/"));
            s = s.substring(0, s.indexOf("/"));
            if(isInt(s))
            {
                day = Integer.parseInt(s);
            }
            else
            {
                found = false;
            }
            s = item;
            s = s.substring(1 + s.indexOf("/"));
            s = s.substring(1 + s.indexOf("/"));
            s = s.substring(0, 4);
            if(isInt(s))
            {
                year = Integer.parseInt(s);
            }
            else if(isInt(s.substring(0, 2)))
            {
                year = Integer.parseInt(s.substring(0, 2));
            }
            else
            {
                found = false;
            }
            if(year < 2000)
            {
                year += 2000;
            }
        }
        if(found)
        {
            Calendar calendar =  Calendar.getInstance();
            calendar.set(year, month, day, hour, min, 0);
            new AlarmTask(WLActivity.c, calendar, (long)(Math.random()*100), item);
        }
    }

    //checks if the string found that could be a date is a number or not
    public static boolean isInt(String s)
    {
        try{
            Integer.parseInt(s);
        }catch(NumberFormatException e){
            return false;
        }
        return true;
    }
}