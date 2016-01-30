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
            for(Item item : items)
            {
                if(item.date.before(earliest.date))
                {
                    earliest = item;
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

        for (Item item : temp)
        {
            if(!item.done)
            {
                items.add(item);
            }
        }
        for (Item item : temp)
        {
            if(item.done)
            {
                items.add(item);
            }
        }
        return items;
    }

    //Takes a list of items and reorganizes it based off which have priority
    public static List<Item> sortByPriority(List<Item> temp)
    {
        List<Item> items = new ArrayList<>();

        for (Item item : temp)
        {
            if(!item.priority)
            {
                items.add(item);
            }
        }
        for (Item item : temp)
        {
            if(item.priority)
            {
                items.add(item);
            }
        }
        return items;
    }

    //Takes a list of items and reorganizes it based off if they are done
    public static List<Item> newList(List<Item> items)
    {
        List<Item> copy = new ArrayList<>();
        for(Item item : items)
        {
            copy.add(item);
        }
        return copy;
    }
}
