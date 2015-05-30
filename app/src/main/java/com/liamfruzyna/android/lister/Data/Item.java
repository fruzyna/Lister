package com.liamfruzyna.android.lister.Data;

import android.provider.ContactsContract;

import com.liamfruzyna.android.lister.Activities.WLActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

    public String color = "#000000";
    public List<String> people = new ArrayList<>();
    public List<String> tags = new ArrayList<>();

    //the constructor if you don't know what this is don't ask me
    public Item(String item, Boolean done, Boolean archived)
    {
        this.item = item;
        /*if(DataContainer.showNotifications && !archived)
        {
            findDate();
        }*/
        parseItem();
        this.done = done;
        this.archived = archived;
    }

    public void parseItem()
    {
        if(item.contains("@"))
        {
            //person tag
            String[] people = item.split("@");
            findPeople(people);
        }
        if(item.contains("*"))
        {
            //tag
            String[] tags = item.split("\\*");
            findTags(tags);
        }
        if(item.contains("#"))
        {
            //color
            String color = item.split("#")[1];
            if(color.contains(" "))
            {
                color = color.split(" ")[0];
            }
            this.color = "#" + color;
            System.out.println("Found Color: " + this.color);
        }
    }

    public void findPeople(String[] strings)
    {
        for(int i = 1; i < strings.length; i++)
        {
            String person = strings[i];
            if (person.contains(" "))
            {
                person = person.split(" ")[0];
            }
            System.out.println("Found Person: " + person);
            people.add(person);
        }
    }

    public void findTags(String[] strings)
    {
        for(int i = 1; i < strings.length; i++)
        {
            String tag = strings[i];
            if (tag.contains(" "))
            {
                tag = tag.split(" ")[0];
            }
            System.out.println("Found Tag: " + tag);
            tags.add(tag);
        }
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