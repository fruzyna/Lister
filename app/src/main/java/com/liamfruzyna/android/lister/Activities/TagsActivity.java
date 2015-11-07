package com.liamfruzyna.android.lister.Activities;

import android.content.SharedPreferences;
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
public class TagsActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener
{
    List<WishList> lists;
    LinearLayout list;
    List<Item> items;
    Spinner spin;
    int current;


    //Takes a list of items and returns the earliest dated item
    public Item findEarliest(List<Item> items)
    {
        Item earliest = items.get(0);
        if(items.size() > 1)
        {
            for(int i = 1; i < items.size(); i++)
            {
                if(items.get(i).date.before(earliest.date))
                {
                    earliest = items.get(i);
                }
            }
        }
        return earliest;
    }

    //Takes a list of items and reorganized it based off date
    public List<Item> sortByDate(List<Item> todo)
    {
        List<Item> build = new ArrayList<>();
        while(todo.size() > 0)
        {
            Item item = findEarliest(todo);
            build.add(item);
            todo.remove(item);
        }
        return build;
    }

    //Takes a list of items and reorganizes it based off if they are done
    public List<Item> sortByDone(List<Item> temp)
    {
        List<Item> items = new ArrayList<>();

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
        return items;
    }

    //finds all the different tags there are
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
        }
        return tags;
    }

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

    public void updateList()
    {
        //reorganizes all the items by date then doneness
        items = sortByDone(sortByDate(getTagItems(getTags().get(current))));
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
                        IO.save(WLActivity.getLists());
                }
            });

            //color item text based off date (late is red, day of it orange)
            SharedPreferences settings = getSharedPreferences(IO.PREFS, 0);
            boolean highlight = settings.getBoolean(IO.HIGHLIGHT_DATE_PREF, true);
            if(highlight)
            {
                Date date = items.get(i).date;
                Date today = Calendar.getInstance().getTime();
                int compare = date.compareTo(today);
                if(date.getYear() == today.getYear() && date.getMonth() == today.getMonth() && date.getDate() == today.getDate() && !items.get(i).done)
                {
                    cb.setTextColor(Color.parseColor("#FFA500"));
                }
                else if(date.compareTo(today) < 0 && !items.get(i).done)
                {
                    cb.setTextColor(Color.RED);
                }
            }

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

        lists = WLActivity.getLists();

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
