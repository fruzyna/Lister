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
import com.liamfruzyna.android.lister.Data.WishList;
import com.liamfruzyna.android.lister.DialogFragments.SuggestionDialog;
import com.liamfruzyna.android.lister.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by mail929 on 5/23/15.
 */
public class PeopleActivity extends TagActivity
{

    //finds all the different people in unarchived lists
    @Override
    public List<String> getTags()
    {
        List<String> people = new ArrayList<>();
        for (WishList list : lists)
        {
            if (!list.archived)
            {
                for (Item item : list.items)
                {
                    for (String person : item.people)
                    {
                        boolean found = false;
                        for (int j = 0; j < people.size(); j++)
                        {
                            if (person.equals(people.get(j)))
                            {
                                found = true;
                            }
                        }
                        if (!found)
                        {
                            people.add(person);
                        }
                    }
                }
                for (String person : list.people)
                {
                    IO.log("PeopleActivity:getTags", "Looking for " + person);
                    boolean found = false;
                    for (String sPerson : people)
                    {
                        if (person.equals(sPerson))
                        {
                            found = true;
                        }
                    }
                    if (!found)
                    {
                        IO.log("PeopleActivity:getTags", "Adding " + person);
                        people.add(person);
                    }
                }

            }
        }
        return people;
    }


    //Gets all the items in unarchived lists containing a name
    @Override
    public List<Item> getTagItems(String person)
    {
        List<Item> items = new ArrayList<>();
        for (WishList list : lists)
        {
            if (!list.archived)
            {
                if(list.people.contains(person))
                {
                    for(Item item : list.items)
                    {
                        items.add(item);
                    }
                }
                else
                {
                    for (Item item : list.items)
                    {
                        for (String personn : item.people)
                        {
                            if (personn.equals(person))
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

    //updates the list on screen
    @Override
    public void updateList()
    {
        if (getTags().size() > 0)
        {
            super.updateList();
        } else
        {
            DialogFragment dialog = new SuggestionDialog();
            Bundle args = new Bundle();
            args.putString("title", "No People Tagged");
            args.putString("message", "You haven't tagged any people in your lists. To do that just add @name to an item (without spaces).");
            dialog.setArguments(args);
            dialog.show(getFragmentManager(), "");
        }
    }
}
