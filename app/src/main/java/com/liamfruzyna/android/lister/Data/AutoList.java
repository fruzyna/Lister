package com.liamfruzyna.android.lister.Data;

import com.liamfruzyna.android.lister.Activities.WLActivity;

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

    public AutoList(String name, List<String> tags, boolean archived, int order, List<String> criteria)
    {
        super(name, tags, archived, order);
        auto = true;
        this.criteria = criteria;
        items = findItems();
    }

    public AutoList(String name, List<String> tags, List<String> criteria)
    {
        super(name, tags);
        auto = true;
        this.criteria = criteria;
        items = findItems();
    }

    public ArrayList<Item> findItems()
    {
        for(WishList list : WLActivity.getUnArchived())
        {
            for(Item item: list.items)
            {
                boolean add = false;
                for(String c : criteria)
                {
                    String[] parts = c.split(" ");
                    String type = parts[0];
                    String required = parts[1];
                    if(required.equals("mandatory") || !add)
                    {
                        if(type.equals("tag"))
                        {
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

                        }
                        else if(type.equals("time"))
                        {
                            int days = Integer.parseInt(parts[3]);
                            Date date = item.date;
                            Calendar cal = Calendar.getInstance();
                            Date current = new Date(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
                            cal.add(Calendar.DATE, 7);
                            Date goal = new Date(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                            if(date.before(goal) && date.after(current))
                            {
                                add = true;
                            }
                        }
                        else if(type.equals("day"))
                        {

                        }
                        if(!add && required.equals("mandatory"))
                        {
                            break;
                        }
                    }
                }
            }
        }
        return null;
    }
}
