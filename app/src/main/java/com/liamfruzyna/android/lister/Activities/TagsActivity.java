package com.liamfruzyna.android.lister.Activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.liamfruzyna.android.lister.Data.IO;
import com.liamfruzyna.android.lister.Data.Item;
import com.liamfruzyna.android.lister.Data.WishList;
import com.liamfruzyna.android.lister.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by mail929 on 12/20/14.
 */
public class TagsActivity extends TagActivity
{
    //finds all the different tags there are
    @Override
    public List<String> getTags()
    {
        List<String> tags = new ArrayList<String>();
        for(WishList list : lists)
        {
            if(!list.archived)
            {
                for(String tag : list.tags)
                {
                    boolean found = false;
                    for(String tagg : tags)
                    {
                        if(tagg.equals(tag))
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
                            if(tag.equals(tagg))
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
    public List<Item> getTagItems(String tag)
    {
        List<Item> items = new ArrayList<Item>();
        for(WishList list : lists)
        {
            if(!list.archived)
            {
                boolean found = false;
                for(String tagg : list.tags)
                {
                    if(tagg.equals(tag))
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
                            if(tagg.equals(tag))
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
