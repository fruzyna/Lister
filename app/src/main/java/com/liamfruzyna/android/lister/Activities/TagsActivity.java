package com.liamfruzyna.android.lister.Activities;

import android.graphics.Color;
import android.graphics.Paint;
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

import com.liamfruzyna.android.lister.Data.DataContainer;
import com.liamfruzyna.android.lister.Data.IO;
import com.liamfruzyna.android.lister.Data.Item;
import com.liamfruzyna.android.lister.Data.WishList;
import com.liamfruzyna.android.lister.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mail929 on 12/20/14.
 */
public class TagsActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener
{
    List<WishList> lists;
    LinearLayout list;
    List<Item> items;
    Spinner spin;
    int current;

    //finds all the different tags there are
    public List<String> getTags()
    {
        List<String> tags = new ArrayList<String>();
        for(int i = 0; i < lists.size(); i++)
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
                if(!found)
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
        return tags;
    }

    public List<Item> getTagItems(String tag)
    {
        List<Item> items = new ArrayList<Item>();
        for(int i = 0; i < lists.size(); i++)
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
        return items;
    }

    public void updateList()
    {
        List<Item> temp = getTagItems(getTags().get(current));
        items = new ArrayList<Item>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < temp.size(); i++)
        {
            if(!temp.get(i).done)
            {
                items.add(temp.get(i));
            }
        }
        for (int i = 0; i < temp.size(); i++)
        {
            if(temp.get(i).done)
            {
                items.add(temp.get(i));
            }
        }
        list.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < items.size(); i++)
        {
            final int j = i;
            View view = inflater.inflate(R.layout.item, list, false);
            //init checkbox and set text
            final CheckBox cb = (CheckBox) view.findViewById(R.id.checkbox);
            cb.setText(items.get(i).item);
            cb.setTextColor(Color.parseColor(items.get(i).color));
            cb.setChecked(items.get(i).done);
            if(items.get(i).done)
            {
                cb.setPaintFlags(cb.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else
            {
                cb.setPaintFlags(0);
            }

            cb.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                        items.get(j).done = cb.isChecked();
                        if (cb.isChecked())
                        {
                            cb.setPaintFlags(cb.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        } else
                        {
                            cb.setPaintFlags(0);
                        }
                        IO.save(WLActivity.lists);
                }
            });
            list.addView(view);
        }
        IO.save(lists);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        list = (LinearLayout) findViewById(R.id.list);

        lists = DataContainer.lists;

        System.out.println(lists.size());

        spin = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> sadapter = new ArrayAdapter<String>(this, R.layout.spinner_item, getTags());
        sadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(sadapter);
        current = spin.getSelectedItemPosition();
        updateList();

        spin.setOnItemSelectedListener(this);


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        current = spin.getSelectedItemPosition();
        updateList();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent){}
}
