package com.liamfruzyna.android.lister.Data;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

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
}
