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
    boolean showDone;

    public AutoList(String name, List<String> tags, boolean archived, int order, List<String> criteria, boolean showDone)
    {
        super(name, tags, archived, order);
        auto = true;
        this.criteria = criteria;
        this.showDone = showDone;
        items = findItems();
    }

    public AutoList(String name, List<String> tags, List<String> criteria, boolean showDone)
    {
        super(name, tags);
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

                            }
                            else if(type.equals("time"))
                            {
                            }
                            else if(type.equals("day"))
                            {
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
                    if(!showDone && item.done)
                    {
                        add = false;
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
