package com.mail929.android.lister.data;

/**
 * Created by mail929 on 2/24/17.
 */

public class Item
{
    private boolean done;
    private String item;
    private int id;
    private int lid;

    public Item(String item, int id, int lid, boolean done)
    {
        this.item = item;
        this.id = id;
        this.lid = lid;
        this.done = done;
    }

    public String getItem()
    {
        return item;
    }

    public Boolean isDone()
    {
        return done;
    }

    public int getId()
    {
        return id;
    }

    public int getParent()
    {
        return lid;
    }

    public void setDone(boolean done)
    {
        this.done = done;
    }

    public void setItem(String item)
    {
        this.item = item;
    }
}