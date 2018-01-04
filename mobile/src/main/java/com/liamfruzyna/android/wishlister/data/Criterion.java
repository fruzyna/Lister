package com.liamfruzyna.android.wishlister.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.liamfruzyna.android.wishlister.data.CriteriaTypes.PASS_ALL;
import static com.liamfruzyna.android.wishlister.data.CriteriaTypes.PASS_ONE;

/**
 * Created by liam on 4/7/17.
 */

public class Criterion
{
    boolean not;
    int group;
    String data;
    CriteriaTypes type;
    List<Criterion> criteria;

    public Criterion(CriteriaTypes type, int group, boolean not, String data, List<Criterion> criteria)
    {
        this.type = type;
        this.group = group;
        this.criteria = criteria;
        this.data = data;
        this.not = not;

        System.out.println(toString());
    }
/*
    public String toString()
    {
        String output = "";
        String should = "";
        if(not)
        {
            should = "not ";
        }

        if(type == PASS_ALL || type == PASS_ONE)
        {
            output = "Parent of group " + group + " must " + should + type.name() + " in group\n";
        }
        else
        {
            output = "Child of group " + group + " must " + should + type.name() + " be " + data + "\n";
        }

        for(Criterion c : criteria)
        {
            output += c.toString();
        }

        return output;
    }

    public boolean passesCriterion(Item item, ListObj list)
    {
        boolean state = false;
        switch(type)
        {
            case PASS_ALL:
                state = true;
                for(Criterion c : criteria)
                {
                    if(!c.passesCriterion(item, list))
                    {
                        state = false;
                    }
                }
                break;
            case PASS_ONE:
                for(Criterion c : criteria)
                {
                    if(c.passesCriterion(item, list))
                    {
                        state = true;
                        break;
                    }
                }
                break;
            case TAG:
                if(item.getItem().contains("#" + data) || list.tags.contains(data))
                {
                    state = true;
                }
                break;
            case PERSON:
                if(item.getItem().contains("@" + data) || list.tags.contains(data))
                {
                    state = true;
                }
                break;
            case DATE:
                if(item.getItem().contains(data) || list.tags.contains(data))
                {
                    state = true;
                }
                break;
            case DATE_RANGE:
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
                String[] dates = data.split(",");
                try
                {
                    Calendar toCal = Calendar.getInstance();
                    toCal.setTime(sdf.parse(dates[1]));
                    toCal.add(Calendar.DAY_OF_YEAR, 1);
                    toCal.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
                    Date to = toCal.getTime();
                    Calendar fromCal = Calendar.getInstance();
                    fromCal.setTime(sdf.parse(dates[0]));
                    fromCal.add(Calendar.DAY_OF_YEAR, -1);
                    fromCal.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
                    Date from = fromCal.getTime();

                    if((item.date.after(from) && item.date.before(to)) || (list.date.after(from) && list.date.before(to)))
                    {
                        state = true;
                    }
                } catch (ParseException e)
                {
                    e.printStackTrace();
                }
                break;
            case DAY_OF_WEEK:
                String day = data;
                Calendar nowCal = Calendar.getInstance();
                int dayNum = nowCal.get(Calendar.DAY_OF_WEEK);
                int goal = 0;
                Calendar byCal = Calendar.getInstance();
                if(day.equalsIgnoreCase("Monday"))
                {
                    goal = Calendar.MONDAY;
                }
                else if(day.equalsIgnoreCase("Tuesday"))
                {
                    goal = Calendar.TUESDAY;
                }
                else if(day.equalsIgnoreCase("Wednesday"))
                {
                    goal = Calendar.WEDNESDAY;
                }
                else if(day.equalsIgnoreCase("Thursday"))
                {
                    goal = Calendar.THURSDAY;
                }
                else if(day.equalsIgnoreCase("Friday"))
                {
                    goal = Calendar.FRIDAY;
                }
                else if(day.equalsIgnoreCase("Saturday"))
                {
                    goal = Calendar.SATURDAY;
                }
                else if(day.equalsIgnoreCase("Sunday"))
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
                    state = true;
                }
                break;
            case WITHIN_DAYS:
                int days = Integer.parseInt(data);
                Calendar bCal = Calendar.getInstance();
                bCal.add(Calendar.DAY_OF_YEAR, days);
                Calendar nCal = Calendar.getInstance();
                nCal.add(Calendar.DAY_OF_YEAR, -1);
                Date b = bCal.getTime();
                Date n = nCal.getTime();
                if((item.date.before(b) && item.date.after(n)) || (list.date.before(b) && list.date.after(n)))
                {
                    state = true;
                }
                break;
            default:
                System.out.println("Invalid type");
        }

        if(not)
        {
            state = !state;
        }
        return state;
    }

    public List<Criterion> getCriteria()
    {
        return criteria;
    }*/
}
