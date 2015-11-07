package com.liamfruzyna.android.lister.Activities;

import com.liamfruzyna.android.lister.Data.Item;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mail929 on 11/6/15.
 */
public class DatesActivity extends TagActivity
{

    //finds all the different people in unarchived lists
    @Override
    public List<String> getTags()
    {
        List<String> dates = new ArrayList<>();
        for(int i = 0; i < lists.size(); i++) {
            if (!lists.get(i).archived) {
                for (int l = 0; l < lists.get(i).items.size(); l++) {
                    boolean found = false;
                    for(int j = 0; j < dates.size(); j++)
                    {
                        if(getDate(lists.get(i).items.get(l).date).equals(dates.get(j)))
                        {
                            found = true;
                        }
                    }
                    if(!found)
                    {
                        dates.add(getDate(lists.get(i).items.get(l).date));
                    }
                }
            }
        }
        return dates;
    }


    //Gets all the items in unarchived lists containing a name
    @Override
    public List<Item> getTagItems(String person)
    {
        List<Item> items = new ArrayList<>();
        for(int i = 0; i < lists.size(); i++)
        {
            if(!lists.get(i).archived)
            {
                for(int l = 0; l < lists.get(i).items.size(); l++)
                {
                    if(getDate(lists.get(i).items.get(l).date).equals(person))
                        {
                            items.add(lists.get(i).items.get(l));
                        }
                }
            }
        }
        return items;
    }

    public String getDate(Date date)
    {
        String[] array = date.toString().split(" ");
        return array[1] + " " + array[2] + " " + array[5];
    }
}
