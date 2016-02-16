package com.liamfruzyna.android.lister.Data;

import android.widget.EditText;

import com.liamfruzyna.android.lister.Activities.WLActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by mail929 on 2/11/16.
 */
public class AutoList extends WishList
{
    List<String> criteria = new ArrayList<>();

    public AutoList(String name, List<String> tags, boolean archived, int order, List<String> criteria, boolean showDone)
    {
        super(name, tags, archived, order, showDone);
        auto = true;
        this.criteria = criteria;
        this.showDone = showDone;
        items = findItems();
    }

    public AutoList(String name, List<String> tags, List<String> criteria, boolean showDone)
    {
        super(name, tags, showDone);
        auto = true;
        this.criteria = criteria;
        this.showDone = showDone;
        items = findItems();
    }

    public List<Item> findItems()
    {
        List<Item> found = new ArrayList<>();
        for(WishList list : WLActivity.getUnArchived())
        {
            if(!list.auto)
            {
                for(Item item : list.items)
                {
                    boolean add = false;
                    for(String c : criteria)
                    {
                        String[] parts = c.split(" ");
                        String type = parts[0];
                        String required = parts[1];
                        System.out.println("Checking " + c);
                        if(required.equals("mandatory") || !add)
                        {
                            if(type.equals("tag"))
                            {
                                for(String tag : list.tags)
                                {
                                    if(tag.equals(parts[2]))
                                    {
                                        add = true;
                                    }
                                }
                                for(String tag : item.tags)
                                {
                                    if(tag.equals(parts[2]))
                                    {
                                        add = true;
                                    }
                                }
                            }
                            else if(type.equals("person"))
                            {
                                for(String tag : list.people)
                                {
                                    if(tag.equals(parts[2]))
                                    {
                                        add = true;
                                    }
                                }
                                for(String tag : item.people)
                                {
                                    if(tag.equals(parts[2]))
                                    {
                                        add = true;
                                    }
                                }
                            }
                            else if(type.equals("date_range"))
                            {
                                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
                                try
                                {
                                    Date from = sdf.parse(parts[2]);
                                    Date to = sdf.parse(parts[3]);
                                    if(item.date.after(from) && item.date.before(to))
                                    {
                                        add = true;
                                    }
                                } catch (ParseException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                            else if(type.equals("time"))
                            {
                                int days = Integer.parseInt(parts[2]);
                                Calendar byCal = Calendar.getInstance();
                                byCal.add(Calendar.DAY_OF_YEAR, days);
                                Calendar nowCal = Calendar.getInstance();
                                Date by = byCal.getTime();
                                Date now = nowCal.getTime();
                                if(item.date.before(by) && item.date.after(now))
                                {
                                    add = true;
                                }
                            }
                            else if(type.equals("day"))
                            {
                                String day = parts[2];
                                Calendar nowCal = Calendar.getInstance();
                                int dayNum = nowCal.get(Calendar.DAY_OF_WEEK);
                                int goal = 0;
                                Calendar byCal = Calendar.getInstance();
                                if(day.equals("Monday"))
                                {
                                    goal = Calendar.MONDAY;
                                }
                                else if(day.equals("Tuesday"))
                                {
                                    goal = Calendar.TUESDAY;
                                }
                                else if(day.equals("Wednesday"))
                                {
                                    goal = Calendar.WEDNESDAY;
                                }
                                else if(day.equals("Thursday"))
                                {
                                    goal = Calendar.THURSDAY;
                                }
                                else if(day.equals("Friday"))
                                {
                                    goal = Calendar.FRIDAY;
                                }
                                else if(day.equals("Saturday"))
                                {
                                    goal = Calendar.SATURDAY;
                                }
                                else if(day.equals("Sunday"))
                                {
                                    goal = Calendar.SUNDAY;
                                }
                                int remaining = goal - dayNum;
                                if(remaining <= 0)
                                {
                                    remaining += 7;
                                }
                                byCal.add(Calendar.DAY_OF_YEAR, remaining);
                                Date by = byCal.getTime();
                                Date now = nowCal.getTime();
                                if(item.date.before(by) && item.date.after(now))
                                {
                                    add = true;
                                }
                            }
                            else
                            {
                                IO.log("AutoList:findItems", "Type " + type + " not valid");
                            }
                            if(!add && required.equals("mandatory"))
                            {
                                break;
                            }
                        }
                    }
                    if(add)
                    {
                        found.add(item);
                    }
                }
            }
        }
        return found;
    }

    public List<String> getCriteria()
    {
        return criteria;
    }
}
