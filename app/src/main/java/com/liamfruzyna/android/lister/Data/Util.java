package com.liamfruzyna.android.lister.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mail929 on 11/6/15.
 */
public class Util
{
    //Takes a list of items and returns the earliest dated item
    public static Item findEarliest(List<Item> items)
    {
        Item earliest = items.get(0);
        if(items.size() > 1)
        {
            for(int i = 1; i < items.size(); i++)
            {
                if(items.get(i).date.before(earliest.date))
                {
                    earliest = items.get(i);
                }
            }
        }
        return earliest;
    }

    //Takes a list of items and reorganized it based off date
    public static List<Item> sortByDate(List<Item> todo)
    {
        List<Item> build = new ArrayList<>();
        while(todo.size() > 0)
        {
            Item item = findEarliest(todo);
            build.add(item);
            todo.remove(item);
        }
        return build;
    }

    //Takes a list of items and reorganizes it based off if they are done
    public static List<Item> sortByDone(List<Item> temp)
    {
        List<Item> items = new ArrayList<>();

        for (int i = 0; i < temp.size(); i++)
        {
            if(!temp.get(i).done)
            {
                items.add(temp.get(i));
            }
        }
        for (int i = 0; i < temp.size(); i++)
        {
            if(temp.get(i).done)
            {
                items.add(temp.get(i));
            }
        }
        return items;
    }

    //Takes a list of items and reorganizes it based off which have priority
    public static List<Item> sortByPriority(List<Item> temp)
    {
        List<Item> items = new ArrayList<>();

        for (int i = 0; i < temp.size(); i++)
        {
            if(temp.get(i).priority)
            {
                items.add(temp.get(i));
            }
        }
        for (int i = 0; i < temp.size(); i++)
        {
            if(!temp.get(i).priority)
            {
                items.add(temp.get(i));
            }
        }
        return items;
    }

    //Takes a list of items and reorganizes it based off if they are done
    public static List<Item> newList(List<Item> items)
    {
        List<Item> copy = new ArrayList<>();
        for(int i = 0; i < items.size(); i++)
        {
            copy.add(items.get(i));
        }
        return copy;
    }
}
