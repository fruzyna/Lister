package com.liamfruzyna.android.lister.Data;

import com.liamfruzyna.android.lister.Data.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mail929 on 11/6/14.
 */
public class WishList
{
    public List<Item> items = new ArrayList<>();
    public List<String> tags = new ArrayList<>();
    public String name;
    public boolean archived = false;

    public WishList(String name, List<Item> items, List<String> tags, boolean archived)
    {
        this.name = name;
        this.items = items;
        this.tags = tags;
        this.archived = archived;
    }

    public WishList(String name, List<String> tags)
    {
        this.name = name;
        this.tags = tags;
    }

}
