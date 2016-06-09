package com.liamfruzyna.android.lister.Data;

import com.liamfruzyna.android.lister.Fragments.WLFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mail929 on 11/6/14.
 */
public class WishList
{
    public List<Item> items = new ArrayList<>();
    public List<String> tags = new ArrayList<>();
    public List<String> people = new ArrayList<>();
    public String name;
    public boolean auto = false;
    public int order = 0;
    public int daysToDelete = 0;
    public boolean archived = false;
    public boolean showDone;

    public WishList(String name, List<Item> items, List<String> tags, boolean archived, int order, boolean showDone, int daysToDelete)
    {
        this.name = name;
        this.items = items;
        this.tags = tags;
        this.archived = archived;
        this.order = order;
        this.showDone = showDone;
        this.daysToDelete = daysToDelete;
        findPeople();
        deleteItems();
    }

    public WishList(String name, List<String> tags, boolean archived, int order, boolean showDone, int daysToDelete)
    {
        this.name = name;
        this.tags = tags;
        this.archived = archived;
        this.order = order;
        this.showDone = showDone;
        this.daysToDelete = daysToDelete;
        findPeople();
        deleteItems();
    }

    public WishList(String name, List<String> tags, boolean showDone, int daysToDelete)
    {
        this.name = name;
        this.tags = tags;
        this.showDone = showDone;
        this.daysToDelete = daysToDelete;
        order = 0;
        for(WishList list : Data.getLists())
        {
            if(list.order > order)
            {
                order = list.order;
            }
        }
        order++;
        findPeople();
        deleteItems();
    }

    public void deleteItems()
    {
        List<Item> toRemove = new ArrayList<>();
        for(Item item : items)
        {
            if(item.deleteItem(daysToDelete))
            {
                toRemove.add(item);
            }
        }
        for(Item item : toRemove)
        {
            items.remove(item);
        }
    }

    public void findPeople()
    {
        for(String tag : tags)
        {
            if(tag.length() > 0)
            {
                if(tag.charAt(0) == '@')
                {
                    people.add(tag.replace("@", ""));

                }
            }
        }
    }
}
