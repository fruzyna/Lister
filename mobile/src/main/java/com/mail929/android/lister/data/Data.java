package com.mail929.android.lister.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.mail929.android.lister.data.ListSorts.AUTO_FIRST;
import static com.mail929.android.lister.data.ListSorts.AUTO_LAST;

/**
 * Created by mail929 on 2/24/17.
 */

public class Data
{
    private static List<ListObj> lists = new ArrayList<>();

    //returns all the lists
    public static List<ListObj> getLists()
    {
        return lists;
    }

    public static void resetLists()
    {
        lists = new ArrayList<>();
    }

    private static int currentList = -1;

    public static void setCurrentList(int id)
    {
        currentList = id;
        IO.getInstance().put(IO.CURRENT_LIST_PREF , id);
    }

    public static int getCurrentListId()
    {
        if(currentList < 0)
        {
            setCurrentList(IO.getInstance().getInt(IO.CURRENT_LIST_PREF));
        }
        if(currentList < 0)
        {
            setCurrentList(lists.get(0).getId());
        }
        return currentList;
    }

    public static int getCurrentListPos()
    {
        try
        {
            return getNames().indexOf(getList(getCurrentListId()).getName());
        }
        catch(NullPointerException e)
        {
            return 0;
        }
    }

    public static ListObj getCurrentList()
    {
        return getList(getCurrentListId());
    }

    public static void sortLists(ListSorts order)
    {
        List<ListObj> sorted = new ArrayList<>();
        while(lists.size() > 0)
        {
            ListObj list;
            switch(order)
            {
                case NEWEST_FIRST:
                    list = getNewestList();
                    break;
                case AUTO_FIRST:
                case AUTO_LAST:
                case OLDEST_FIRST:
                default:
                    list = getOldestList();
                    break;
            }
            sorted.add(list);
            lists.remove(list);
        }
        lists = sorted;
        if(order == AUTO_FIRST || order == AUTO_LAST)
        {
            sorted = new ArrayList<>();
            while(lists.size() > 0)
            {
                ListObj list;
                switch(order)
                {
                    case AUTO_FIRST:
                        list = getFirstAuto(true);
                        break;
                    case AUTO_LAST:
                    default:
                        list = getFirstAuto(false);
                        break;
                }
                sorted.add(list);
                lists.remove(list);
            }
            lists = sorted;
        }
    }

    public static ListObj getNewestList()
    {
        return getList(getNewestListId());
    }

    public static ListObj getOldestList()
    {
        return getList(getOldestListId());
    }

    public static int getNewestListId()
    {
        int newest = -1;
        for(ListObj list : lists)
        {
            if(list.getId() > newest)
            {
                newest = list.getId();
            }
        }
        return newest;
    }

    public static ListObj getFirstAuto(boolean auto)
    {
        for(ListObj list : lists)
        {
            if(list.isAuto() == auto)
            {
                return list;
            }
        }
        return lists.get(0);
    }

    public static int getOldestListId()
    {
        int newest = -1;
        for(ListObj list : lists)
        {
            if(newest < 0 || list.getId() < newest)
            {
                newest = list.getId();
            }
        }
        return newest;
    }

    //returns all the unarchived lists
    public static List<ListObj> getUnArchived()
    {
        List<ListObj> unar = new ArrayList<>();
        for(ListObj list : lists)
        {
            if(!list.isArchived())
            {
                unar.add(list);
            }
        }
        return unar;
    }

    //returns all the archived lists
    public static List<ListObj> getArchived()
    {
        List<ListObj> ar = new ArrayList<>();
        for(ListObj list : lists)
        {
            if(list.isArchived())
            {
                ar.add(list);
            }
        }
        return ar;
    }

    public static List<String> getNames()
    {
        List<String> names = new ArrayList<>();
        for(ListObj list : getUnArchived())
        {
            names.add(list.getName());
        }
        Collections.sort(names);
        return names;
    }

    public static void setLists(List<ListObj> lists)
    {
        Data.lists = lists;
    }

    //takes the name of a list and returns the list object
    public static ListObj getListFromName(String name)
    {
        for (ListObj list : lists)
        {
            if (list.getName().equals(name))
            {
                return list;
            }
        }
        return null;
    }

    //returns the list object
    public static ListObj getList(int id)
    {
        for (ListObj list : lists)
        {
            if (list.getId() == id)
            {
                return list;
            }
        }
        return null;
    }

    //returns the item object
    public static Item getItem(int id)
    {
        for (ListObj list : lists)
        {
            for(Item item : list.getItems())
            {
                if(item.getId() == id)
                {
                    return item;
                }
            }
        }
        return null;
    }

    public static void replaceList(ListObj list)
    {
        boolean found = false;
        for(int i = 0; i < lists.size(); i++)
        {
            if(lists.get(i).getName().equals(list.getName()))
            {
                found = true;
                lists.set(i, list);
                break;
            }
        }
        if(!found)
        {
            lists.add(list);
        }
    }

    public static int getMaxList()
    {
        return getNewestListId();
    }

    public static int getMaxItem()
    {
        int max = 0;
        for(ListObj list : lists)
        {
            for(Item item : list.getItems())
            {
                if(item.getId() > max)
                {
                    max = item.getId();
                }
            }
        }
        return max;
    }
}
