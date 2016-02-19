package com.liamfruzyna.android.lister.Activities;

import android.app.DialogFragment;
import android.os.Bundle;

import com.liamfruzyna.android.lister.Data.IO;
import com.liamfruzyna.android.lister.Data.Item;
import com.liamfruzyna.android.lister.Data.WishList;
import com.liamfruzyna.android.lister.DialogFragments.SuggestionDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mail929 on 5/23/15.
 */
public class PeopleFragment extends TagFragment
{

    //finds all the different people in unarchived lists
    @Override
    public List<String> getTags()
    {
        List<String> people = new ArrayList<>();
        for (WishList list : lists)
        {
            if (!list.archived && !list.auto)
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
                    IO.log("PeopleFragment:getTags", "Looking for " + person);
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
                        IO.log("PeopleFragment:getTags", "Adding " + person);
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
            if (!list.archived && !list.auto)
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
