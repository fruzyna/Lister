package com.liamfruzyna.android.wishlister.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by mail929 on 2/24/17.
 */

public class ListObj
{
    private String name;
    private boolean archived;
    private char perm;
    private int id;

    private ArrayList<Item> items;

    public ListObj(String name, int id, char perm, boolean archived)
    {
        this.name = name;
        this.id = id;
        this.perm = perm;
        this.archived = archived;

        items = new ArrayList<>();
    }

    public void addItem(Item item)
    {
        items.add(item);
    }

    public ArrayList<Item> getItems()
    {
        return items;
    }

    public String getName()
    {
        return name;
    }

    public boolean isArchived()
    {
        return archived;
    }

    public char getPerm()
    {
        return perm;
    }

    public int getId()
    {
        return id;
    }

    public void resetList()
    {
        items = new ArrayList<>();
    }

    public Item getItem(int id)
    {
        for(Item item : items)
        {
            if(item.getId() == id)
            {
                return item;
            }
        }
        return null;
    }

    public void setArchived(boolean archived)
    {
        this.archived = archived;
    }
}
