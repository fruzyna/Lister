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
        for(int i = 0; i < lists.size(); i++)
        {
            if(!lists.get(i).archived)
            {
                for(int j = 0; j < lists.get(i).tags.size(); j++)
                {
                    boolean found = false;
                    for(int l = 0; l < tags.size(); l++)
                    {
                        if(tags.get(l).equals(lists.get(i).tags.get(j)))
                        {
                            found = true;
                        }
                    }
                    if(!found && !lists.get(i).tags.get(j).contains("@"))
                    {
                        tags.add(lists.get(i).tags.get(j));
                    }
                }
                for(int l = 0; l < lists.get(i).items.size(); l++)
                {
                    for(int k = 0; k < lists.get(i).items.get(l).tags.size(); k++)
                    {
                        boolean found = false;
                        for(int j = 0; j < tags.size(); j++)
                        {
                            if(lists.get(i).items.get(l).tags.get(k).equals(tags.get(j)))
                            {
                                found = true;
                            }
                        }
                        if(!found)
                        {
                            tags.add(lists.get(i).items.get(l).tags.get(k));
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
        for(int i = 0; i < lists.size(); i++)
        {
            if(!lists.get(i).archived)
            {
                boolean found = false;
                for(int j = 0; j < lists.get(i).tags.size(); j++)
                {
                    if(lists.get(i).tags.get(j).equals(tag))
                    {
                        found = true;
                        for(int l = 0; l < lists.get(i).items.size(); l++)
                        {
                            items.add(lists.get(i).items.get(l));
                        }
                    }
                }
                if(!found)
                {
                    for(int l = 0; l < lists.get(i).items.size(); l++)
                    {
                        for(int k = 0; k < lists.get(i).items.get(l).tags.size(); k++)
                        {
                            if(lists.get(i).items.get(l).tags.get(k).equals(tag))
                            {
                                items.add(lists.get(i).items.get(l));
                            }
                        }
                    }
                }
            }
        }
        return items;
    }
}
