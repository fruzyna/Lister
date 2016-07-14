package com.liamfruzyna.android.lister.Data;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
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
        if(items == null)
        {
            items = new ArrayList<>();
        }
        List<Item> copy = new ArrayList<>();
        for(Item item : items)
        {
            copy.add(item);
        }
        return copy;
    }

    //takes an item and colors its tags
    public static SpannableStringBuilder colorTags(String item, int color)
    {
        final SpannableStringBuilder sb = new SpannableStringBuilder();
        int alpha = Color.argb(128, Color.red(color), Color.green(color), Color.blue(color));
        String[] words = item.split(" ");
        for(int i = 0; i < words.length; i++)
        {
            if(words[i].length() > 0)
            {
                if(words[i].charAt(0) == '#')
                {
                    SpannableString s = new SpannableString(words[i]);
                    s.setSpan(new ForegroundColorSpan(alpha), 0, words[i].length(), 0);
                    sb.append(s);
                }
                else
                {
                    sb.append(words[i]);
                }
            }
            if(i < words.length - 1)
            {
                sb.append(" ");
            }
        }
        return sb;
    }

    //finds all the unAchived lists and groups them
    public static List<WishList> populateUnArchived()
    {
        List<WishList> unArchived = new ArrayList<>();
        for (WishList list : Data.getLists())
        {
            if (!list.archived)
            {
                unArchived.add(list);
            }
        }
        System.out.println("Unarchived Lists: " + Data.getUnArchived().size());
        return unArchived;
    }

    //Takes a list of lists and reorganizes it based off the order variable
    public static List<String> sortLists(List<WishList> lists)
    {
        List<WishList> copy = new ArrayList<>(lists);
        List<String> names = new ArrayList<>();
        List<String> extra = new ArrayList<>();
        while (copy.size() > 0)
        {
            int lowest = Integer.MAX_VALUE;
            int count = 0;
            for (int j = 0; j < copy.size(); j++)
            {
                if (copy.get(j).order != 0)
                {
                    if (copy.get(j).order < lowest)
                    {
                        IO.log("WLActivity:sortLists", "New Lowest " + copy.get(j).name + " with order of " + copy.get(j).order);
                        lowest = copy.get(j).order;
                        count = j;
                    }
                } else
                {
                    extra.add(copy.get(j).name);
                }
            }
            IO.log("WLActivity:sortLists", "Adding " + copy.get(count).name + " with order of " + copy.get(count).order);
            names.add(copy.get(count).name);
            copy.remove(count);
        }
        for (String xtra : extra)
        {
            if (!names.contains(xtra))
            {
                names.add(xtra);
            }
        }

        return names;
    }
}
