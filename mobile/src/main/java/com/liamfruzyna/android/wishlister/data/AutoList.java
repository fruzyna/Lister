package com.liamfruzyna.android.wishlister.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by mail929 on 2/24/17.
 */

public class AutoList extends ListObj
{
    java.util.List<String> criteria = new ArrayList<>();

    public AutoList(String name, java.util.List<String> tags, boolean archived, java.util.List<String> criteria, boolean showDone, int daysToDelete)
    {
        super(name, tags, archived, showDone, daysToDelete);
        auto = true;
        this.criteria = criteria;
        this.showDone = showDone;
    }

    public AutoList(String name, java.util.List<String> tags, java.util.List<String> criteria, boolean showDone, int daysToDelete)
    {
        super(name, tags, showDone, daysToDelete);
        auto = true;
        this.criteria = criteria;
        this.showDone = showDone;
    }

    public void findItems()
    {
        System.out.println("Finding items for " + name);
        java.util.List<Item> found = new ArrayList<>();
        for(ListObj list : Data.getUnArchived())
        {
            if(!list.auto)
            {
                for(Item item : list.items)
                {
                    boolean add = false;
                    for(String c : criteria)
                    {
                        String[] parts = c.split(" ");
                        if(!c.contains("include") && !c.contains("exclude"))
                        {
                            StringBuilder sb = new StringBuilder();
                            for(int i = 0; i < parts.length; i++)
                            {
                                sb.append(parts[i] + " ");
                                if(i == 1)
                                {
                                    sb.append("include ");
                                }
                            }
                            c = sb.toString();
                            parts = c.split(" ");
                        }
                        boolean make = true;
                        String type = parts[2];
                        String required = parts[0];
                        String exclude = parts[1];

                        if(exclude.equals("exclude"))
                        {
                            make = false;
                        }
                        if(required.equals("mandatory") && make)
                        {
                            add = false;
                        }
                        if(!add || (!make && add))
                        {
                            if(type.equals("tag"))
                            {
                                for(String tag : list.tags)
                                {
                                    if(tag.toLowerCase().equals(parts[3].toLowerCase()))
                                    {
                                        add = make;
                                    }
                                }
                                for(String tag : item.tags)
                                {
                                    if(tag.toLowerCase().equals(parts[3].toLowerCase()))
                                    {
                                        add = make;
                                    }
                                }
                            }
                            else if(type.equals("person"))
                            {
                                for(String tag : list.people)
                                {
                                    if(tag.toLowerCase().equals(parts[3].toLowerCase()))
                                    {
                                        add = make;
                                    }
                                }
                                for(String tag : item.people)
                                {
                                    if(tag.toLowerCase().equals(parts[3].toLowerCase()))
                                    {
                                        add = make;
                                    }
                                }
                            }
                            else if(type.equals("date_range"))
                            {
                                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
                                try
                                {
                                    Calendar toCal = Calendar.getInstance();
                                    toCal.setTime(sdf.parse(parts[4]));
                                    toCal.add(Calendar.DAY_OF_YEAR, 1);
                                    Date to = toCal.getTime();
                                    Date from = sdf.parse(parts[3]);
                                    if((item.date.after(from) && item.date.before(to)) || (list.date.after(from) && list.date.before(to)))
                                    {
                                        add = make;
                                    }
                                } catch (ParseException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                            else if(type.equals("time"))
                            {
                                int days = Integer.parseInt(parts[3]);
                                Calendar byCal = Calendar.getInstance();
                                byCal.add(Calendar.DAY_OF_YEAR, days);
                                Calendar nowCal = Calendar.getInstance();
                                nowCal.add(Calendar.DAY_OF_YEAR, -1);
                                Date by = byCal.getTime();
                                Date now = nowCal.getTime();
                                if((item.date.before(by) && item.date.after(now)) || (list.date.before(by) && list.date.after(now)))
                                {
                                    add = make;
                                }
                            }
                            else if(type.equals("day"))
                            {
                                String day = parts[3];
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
                                if((item.date.before(by) && item.date.after(now)) || (list.date.before(by) && list.date.after(now)))
                                {
                                    add = make;
                                }
                            }
                            else
                            {
                                System.out.println("Type " + type + " not valid");
                            }
                            if(required.equals("mandatory") && !add)
                            {
                                break;
                            }
                        }
                    }
                    if(add)
                    {
                        System.out.println("Adding " + item.item);
                        found.add(item);
                    }
                }
            }
        }
        items = found;
    }

    public java.util.List<String> getCriteria()
    {
        return criteria;
    }

    public void setCriteria(java.util.List<String> criteria)
    {
        this.criteria = criteria;
    }
}
