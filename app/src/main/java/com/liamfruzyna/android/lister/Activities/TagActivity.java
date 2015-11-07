package com.liamfruzyna.android.lister.Activities;

import android.app.DialogFragment;
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
import com.liamfruzyna.android.lister.Data.Util;
import com.liamfruzyna.android.lister.Data.WishList;
import com.liamfruzyna.android.lister.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by mail929 on 11/6/15.
 */
public class TagActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener
{
        List<WishList> lists;
        LinearLayout list;
        List<Item> items;
        Spinner spin;
        int current;

        //finds all the different people in unarchived lists
        public List<String> getTags()
        {
            List<String> people = new ArrayList<>();
            return people;
        }

    //Gets all the items in unarchived lists containing a name
    public List<Item> getTagItems(String tag)
    {
        List<Item> items = new ArrayList<Item>();
        return items;
    }

    //updates the list on screen
    public void updateList()
    {
            items = Util.sortByDone(Util.sortByDate(getTagItems(getTags().get(current))));
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

    //update the screen when a new name is selected
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        current = spin.getSelectedItemPosition();
        updateList();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent){}
}
