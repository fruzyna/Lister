package com.liamfruzyna.android.lister.Data;

import com.liamfruzyna.android.lister.Activities.WLActivity;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Item
{
    public Boolean done;
    public Boolean archived;
    public String item;

    public Item(String item, Boolean done, Boolean archived)
    {
        this.item = item;
        findDate();
        this.done = done;
        this.archived = archived;
    }

    public void findDate()
    {
        String s = item;
        boolean found = false;
        int hour = 0;
        int min = 0;
        if(s.indexOf(":") != -1)
        {
            found = true;
            s = s.substring(0, s.indexOf(":"));
            if(isInt(s))
            {
                hour = Integer.parseInt(s.substring(1 + s.lastIndexOf(" ")));
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
            if(isInt(s))
            {
                month = Integer.parseInt(s.substring(1 + s.lastIndexOf(" ")));
            }
            else
            {
                found = false;
            }
            s = item;
            s = s.substring(1 + s.indexOf("/"));
            if(isInt(s))
            {
                day = Integer.parseInt(s.substring(0, s.indexOf("/")));
            }
            else
            {
                found = false;
            }
            s = item;
            s = s.substring(1 + s.indexOf("/"));
            s = s.substring(1 + s.indexOf("/"));
            s = s.substring(0, 2);
            if(isInt(s))
            {
                year = Integer.parseInt(s);
            }
            else
            {
                found = false;
            }
            year += 100;
            if(year > 2000)
            {
                year -= 2000;
            }
            System.out.println(month + "/" + day + "/" + year + " " + hour + ":" + min);
        }
        if(found)
        {
            Calendar calendar =  Calendar.getInstance();
            calendar.set(year, month, day, hour, min);
            WLActivity.startAlarm(calendar);
        }
    }

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