package com.liamfruzyna.android.lister.Data;

import com.liamfruzyna.android.lister.Activities.WLActivity;
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
    public List<String> people = new ArrayList<>();
    public String name;
    public int order = 0;
    public boolean archived = false;

    public WishList(String name, List<Item> items, List<String> tags, boolean archived, int order)
    {
        this.name = name;
        this.items = items;
        this.tags = tags;
        this.archived = archived;
        this.order = order;
        findPeople();
    }

    public WishList(String name, List<String> tags)
    {
        this.name = name;
        this.tags = tags;
        order = WLActivity.getLists().size();
        findPeople();
    }

    public void findPeople()
    {
        for(int i = 0; i < tags.size(); i++)
        {
            if(tags.get(i).length() > 0)
            {
                if(tags.get(i).charAt(0) == '@')
                {
                    people.add(tags.get(i).replace("@", ""));

                }
            }
        }
    }

}
