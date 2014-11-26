package com.liamfruzyna.android.lister;

import java.util.GregorianCalendar;

public class Item
{
    GregorianCalendar date;
    Boolean done;
    String item;

    public Item(String item, Boolean done)
    {
        this.item = item;
        findDate();
        this.done = done;
    }

    public void createNotification()
    {
        WLActivity.startAlarm(date);
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
            hour = Integer.parseInt(s.substring(1 + s.lastIndexOf(" ")));
            s = item;
            s = s.substring(1 + s.indexOf(":"));
            s = s.substring(0, 2);
            min = Integer.parseInt(s);
        }
        s = item;
        int month = 0;
        int day = 0;
        int year = 0;
        if(s.indexOf("/") != -1)
        {
            found = true;
            s = s.substring(0, s.indexOf("/"));
            month = Integer.parseInt(s.substring(1 + s.lastIndexOf(" ")));
            s = item;
            s = s.substring(1 + s.indexOf("/"));
            day = Integer.parseInt(s.substring(0, s.indexOf("/")));
            s = item;
            s = s.substring(1 + s.indexOf("/"));
            s = s.substring(1 + s.indexOf("/"));
            s = s.substring(0, 2);
            year = Integer.parseInt(s);
            if (year < 2000)
            {
                year += 2000;
            }
            System.out.println(month + "/" + day + "/" + year + " " + hour + ":" + min);
        }
        if(found)
        {
            date = new GregorianCalendar(year, month, day, hour, min);
            createNotification();
        }
    }
}