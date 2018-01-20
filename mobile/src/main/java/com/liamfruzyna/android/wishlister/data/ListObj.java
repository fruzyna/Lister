package com.liamfruzyna.android.wishlister.data;

import java.util.ArrayList;

/**
 * Created by mail929 on 2/24/17.
 */

public class ListObj
{
    private String name;
    private boolean archived;
    private char perm;
    private int id;

    private int daysToDel;
    private boolean showDone;
    private boolean sortDate;
    private boolean sortDone;

    private ArrayList<Item> items;

    public ListObj(String name, int id, char perm, boolean archived, int daysToDel, boolean showDone, boolean sortDate, boolean sortDone)
    {
        this.name = name;
        this.id = id;
        this.perm = perm;
        this.archived = archived;

        this.daysToDel = daysToDel;
        this.showDone = showDone;
        this.sortDate = sortDate;
        this.sortDone = sortDone;

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

    public void setShowDone(boolean showDone)
    {
        this.showDone = showDone;
    }

    public void setSortDate(boolean sortDate)
    {
        this.sortDate = sortDate;
    }

    public void setSortDone(boolean sortDone)
    {
        this.sortDone = sortDone;
    }

    public void setDaysToDel(int daysToDel)
    {
        this.daysToDel = daysToDel;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getDaysToDel()
    {
        return daysToDel;
    }

    public boolean isShowDone()
    {
        return showDone;
    }

    public boolean isSortDate()
    {
        return sortDate;
    }

    public boolean isSortDone()
    {
        return sortDone;
    }

    public boolean isAuto()
    {
        return this instanceof AutoList;
    }
}
