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
        for(int i = 0; i < lists.size(); i++) {
            if (!lists.get(i).archived) {
                for (int l = 0; l < lists.get(i).items.size(); l++) {
                    for (int k = 0; k < lists.get(i).items.get(l).people.size(); k++) {
                        boolean found = false;
                        for (int j = 0; j < people.size(); j++) {
                            if (lists.get(i).items.get(l).people.get(k).equals(people.get(j))) {
                                found = true;
                            }
                        }
                        if (!found) {
                            people.add(lists.get(i).items.get(l).people.get(k));
                        }
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
        List<Item> items = new ArrayList<Item>();
        for(int i = 0; i < lists.size(); i++)
        {
            if(!lists.get(i).archived)
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
        }
        return items;
    }

    //updates the list on screen
    @Override
    public void updateList()
    {
        if(getTags().size() > 0)
        {
        super.updateList();
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
}
