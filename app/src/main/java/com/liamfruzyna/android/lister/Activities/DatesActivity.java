package com.liamfruzyna.android.lister.Activities;

import com.liamfruzyna.android.lister.Data.Item;
import com.liamfruzyna.android.lister.Data.WishList;

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
        for(WishList list : lists) {
            if (!list.archived) {
                for (Item item : list.items) {
                    boolean found = false;
                    for(String date : dates)
                    {
                        if(item.date.equals(date))
                        {
                            found = true;
                        }
                    }
                    if(!found)
                    {
                        dates.add(getDate(item.date));
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
        for(WishList list : lists)
        {
            if(!list.archived)
            {
                for(Item item : list.items)
                {
                    if(getDate(item.date).equals(person))
                        {
                            items.add(item);
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
