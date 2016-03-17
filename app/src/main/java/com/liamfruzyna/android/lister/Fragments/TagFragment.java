package com.liamfruzyna.android.lister.Fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.liamfruzyna.android.lister.Data.Data;
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
public class TagFragment extends Fragment implements AdapterView.OnItemSelectedListener
{
        View view;
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
        List<Item> items = new ArrayList<>();
        return items;
    }

    //updates the list on screen
    public void updateList()
    {
            items = Util.sortByDone(Util.sortByPriority(Util.sortByDate(getTagItems(getTags().get(current)))));
            list.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            for (final Item item : items)
            {
                View view = inflater.inflate(R.layout.checkbox_list_item, list, false);
                //init checkbox and set text
                final CheckBox cb = (CheckBox) view.findViewById(R.id.checkbox);
                int color = Color.parseColor(item.color);

                //color item text based off date (late is red, day of is orange)
                SharedPreferences settings = getActivity().getSharedPreferences(IO.PREFS, 0);
                boolean highlight = settings.getBoolean(IO.HIGHLIGHT_DATE_PREF, true);
                if(highlight)
                {
                    Date date = item.date;
                    Date today = Calendar.getInstance().getTime();
                    int compare = date.compareTo(today);
                    if(date.getYear() == today.getYear() && date.getMonth() == today.getMonth() && date.getDate() == today.getDate() && !item.done)
                    {
                        color = Color.parseColor("#FFA500");
                    }
                    else if(date.compareTo(today) < 0 && !item.done)
                    {
                        color = Color.RED;
                    }
                }

                SpannableStringBuilder s = Util.colorTags(item.item, color);
                cb.setText(s);
                cb.setTextColor(Color.parseColor(item.color));
                cb.setChecked(item.done);
                if(item.done)
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
                        item.done = cb.isChecked();
                        if (cb.isChecked())
                        {
                            cb.setPaintFlags(cb.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        } else
                        {
                            cb.setPaintFlags(0);
                        }
                        IO.save();
                    }
                });
                list.addView(view);
            }
    }

    @Override
    public View onCreateView(LayoutInflater infl, ViewGroup parent, Bundle savedInstanceState) {
        view = infl.inflate(R.layout.fragment_tags, parent, false);

        list = (LinearLayout) view.findViewById(R.id.list);
        spin = (Spinner) view.findViewById(R.id.spinner);

        (new Handler()).postDelayed(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            lists = Data.getLists();

                                            ArrayAdapter<String> sadapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, getTags());
                                            sadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            spin.setAdapter(sadapter);
                                            current = spin.getSelectedItemPosition();
                                            updateList();
                                        }
                                    }, 500);
        spin.setOnItemSelectedListener(this);
        return view;
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
