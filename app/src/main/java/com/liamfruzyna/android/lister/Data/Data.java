package com.liamfruzyna.android.lister.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mail929 on 3/17/16.
 */
public class Data
{
    private static int current = 0;
    private static List<String> names = new ArrayList<>();
    private static List<Item> items = new ArrayList<>();
    private static List<WishList> lists = new ArrayList<>();
    private static List<WishList> unArchived = new ArrayList<>();

    //returns the list currently viewable on screen
    public static WishList getCurrentList()
    {
        for(int i = 0; i < unArchived.size(); i++)
        {
            if(unArchived.get(i).name.equals(names.get(current)))
            {
                return unArchived.get(i);
            }
        }
        return null;
    }

    //returns the name of the current selected list
    public static String getCurrentName()
    {
        return names.get(current);
    }

    //returns the items currently displayed on screen
    public static List<Item> getItems()
    {
        return items;
    }

    //returns all the lists
    public static List<WishList> getLists()
    {
        return lists;
    }

    //returns all the unarchived lists
    public static List<WishList> getUnArchived()
    {
        return unArchived;
    }

    public static void setCurrent(int current)
    {
        Data.current = current;
    }

    public static int getCurrent()
    {
        return current;
    }

    public static List<String> getNames()
    {
        return names;
    }

    public static void setNames(List<String> names)
    {
        Data.names = names;
    }

    public static void setUnArchived(List<WishList> unArchived)
    {
        Data.unArchived = unArchived;
    }

    public static void setLists(List<WishList> lists)
    {
        Data.lists = lists;
    }

    public static void setItems(List<Item> items)
    {
        Data.items = items;
    }

    //takes the name of a list and returns the list object
    public static WishList getListFromName(String name)
    {
        for (WishList list : unArchived)
        {
            if (list.name.equals(name))
            {
                return list;
            }
        }
        return null;
    }


    public static List<String> getTags()
    {
        List<String> tags = new ArrayList<>();
        for(WishList list : lists)
        {
            if(!list.archived)
            {
                for(String tag : list.tags)
                {
                    boolean found = false;
                    for(String tagg : tags)
                    {
                        if(tagg.toLowerCase().equals(tag.toLowerCase()))
                        {
                            found = true;
                        }
                    }
                    if(!found && !tag.contains("@"))
                    {
                        tags.add(tag);
                    }
                }
                for(Item item : list.items)
                {
                    for(String tag : item.tags)
                    {
                        boolean found = false;
                        for(String tagg : tags)
                        {
                            if(tag.toLowerCase().equals(tagg.toLowerCase()))
                            {
                                found = true;
                            }
                        }
                        if(!found)
                        {
                            tags.add(tag);
                        }
                    }
                }
            }
        }
        return tags;
    }
}
