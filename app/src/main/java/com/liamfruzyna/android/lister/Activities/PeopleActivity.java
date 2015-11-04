package com.liamfruzyna.android.lister.Activities;

import android.app.DialogFragment;
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
import com.liamfruzyna.android.lister.DialogFragments.SuggestionDialog;
import com.liamfruzyna.android.lister.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mail929 on 5/23/15.
 */
public class PeopleActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener
{
    List<WishList> lists;
    LinearLayout list;
    List<Item> items;
    Spinner spin;
    int current;

    //finds all the different people there are
    public List<String> getPeople()
    {
        List<String> people = new ArrayList<>();
        for(int i = 0; i < lists.size(); i++)
        {
            for(int l = 0; l < lists.get(i).items.size(); l++)
            {
                for(int k = 0; k < lists.get(i).items.get(l).people.size(); k++)
                {
                    boolean found = false;
                    for(int j = 0; j < people.size(); j++)
                    {
                        if(lists.get(i).items.get(l).people.get(k).equals(people.get(j)))
                        {
                            found = true;
                        }
                    }
                    if(!found)
                    {
                        people.add(lists.get(i).items.get(l).people.get(k));
                    }
                }
            }
        }
        return people;
    }

    public List<Item> getPeopleItems(String person)
    {
        List<Item> items = new ArrayList<Item>();
        for(int i = 0; i < lists.size(); i++)
        {
            for(int l = 0; l < lists.get(i).items.size(); l++)
            {
                for(int k = 0; k < lists.get(i).items.get(l).people.size(); k++)
                {
                    if(lists.get(i).items.get(l).people.get(k).equals(person))
                    {
                        items.add(lists.get(i).items.get(l));
                    }
                }
            }
        }
        return items;
    }

    public void updateList()
    {
        if(getPeople().size() > 0)
        {
            List<Item> temp = getPeopleItems(getPeople().get(current));
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
                        IO.save(WLActivity.getLists());
                    }
                });
                list.addView(view);
            }
            IO.save(lists);
        }
        else
        {
            DialogFragment dialog = new SuggestionDialog();
            Bundle args = new Bundle();
            args.putString("title", "No People Tagged");
            args.putString("message", "You haven't tagged any people in your lists. To do that just add @name to an item (without spaces).");
            dialog.setArguments(args);
            dialog.show(getFragmentManager(), "");
        }
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
        ArrayAdapter<String> sadapter = new ArrayAdapter<String>(this, R.layout.spinner_item, getPeople());
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
