package com.liamfruzyna.android.wishlister;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mail929 on 11/6/14.
 */
public class WishList
{
    public List<Item> items = new ArrayList<Item>();
    public List<String> tags = new ArrayList<String>();
    public String name;

    public WishList(String name, List<Item> items, List<String> tags)
    {
        this.name = name;
        this.items = items;
        this.tags = tags;
    }

    public WishList(String name, List<String> tags)
    {
        this.name = name;
        this.tags = tags;
    }
}
