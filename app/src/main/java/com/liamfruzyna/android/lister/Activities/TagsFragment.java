package com.liamfruzyna.android.lister.Activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liamfruzyna.android.lister.Data.Item;
import com.liamfruzyna.android.lister.Data.WishList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mail929 on 12/20/14.
 */
public class TagsFragment extends TagFragment
{
    //finds all the different tags there are
    @Override
    public List<String> getTags()
    {
        List<String> tags = new ArrayList<String>();
        for(WishList list : lists)
        {
            if(!list.archived && !list.auto)
            {
                for(String tag : list.tags)
                {
                    boolean found = false;
                    for(String tagg : tags)
                    {
                        if(tagg.toLowerCase().equals(tag.toLowerCase()))
                        {
                            found = true;
                        }
                    }
                    if(!found && !tag.contains("@"))
                    {
                        tags.add(tag);
                    }
                }
                for(Item item : list.items)
                {
                    for(String tag : item.tags)
                    {
                        boolean found = false;
                        for(String tagg : tags)
                        {
                            if(tag.toLowerCase().equals(tagg.toLowerCase()))
                            {
                                found = true;
                            }
                        }
                        if(!found)
                        {
                            tags.add(tag);
                        }
                    }
                }
            }
        }
        return tags;
    }

    @Override
    public View onCreateView(LayoutInflater infl, ViewGroup parent, Bundle savedInstanceState)
    {
        View v = super.onCreateView(infl, parent, savedInstanceState);
        getActivity().setTitle("Tag Viewer");
        return v;
    }

    @Override
    public List<Item> getTagItems(String tag)
    {
        List<Item> items = new ArrayList<>();
        for(WishList list : lists)
        {
            if(!list.archived && !list.auto)
            {
                boolean found = false;
                for(String tagg : list.tags)
                {
                    if(tagg.toLowerCase().equals(tag.toLowerCase()))
                    {
                        found = true;
                        for(Item item : list.items)
                        {
                            items.add(item);
                        }
                    }
                }
                if(!found)
                {
                    for(Item item : list.items)
                    {
                        for(String tagg : item.tags)
                        {
                            if(tagg.toLowerCase().equals(tag.toLowerCase()))
                            {
                                items.add(item);
                            }
                        }
                    }
                }
            }
        }
        return items;
    }
}
