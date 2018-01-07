package com.liamfruzyna.android.wishlister.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by mail929 on 2/24/17.
 */

public class Data {
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
    public static ListObj getListFromName(String name) {
        for (ListObj list : lists) {
            if (list.getName().equals(name)) {
                return list;
            }
        }
        return null;
    }

    //returns the list object
    public static ListObj getList(int id) {
        for (ListObj list : lists) {
            if (list.getId() == id) {
                return list;
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
}
