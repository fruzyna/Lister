package com.liamfruzyna.android.wishlister.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by mail929 on 2/24/17.
 */

public class AutoList extends ListObj
{
    Criterion criteria;

    public AutoList(String name, List<String> tags, boolean archived, Criterion criteria, boolean showDone, int daysToDelete, boolean sortChecked, boolean sortDate)
    {
        super(name, tags, archived, showDone, daysToDelete, sortChecked, sortDate);
        auto = true;
        this.criteria = criteria;
        this.showDone = showDone;
    }

    public AutoList(String name, List<String> tags, Criterion criteria, boolean showDone, int daysToDelete)
    {
        super(name, tags, showDone, daysToDelete);
        auto = true;
        this.criteria = criteria;
        this.showDone = showDone;
    }

    public void findItems()
    {
        System.out.println(criteria.toString());
        items = new ArrayList<>();
        for(ListObj list : Data.getUnArchived())
        {
            if(!list.auto)
            {
                for(Item item : list.items)
                {
                    if(criteria.passesCriterion(item, list))
                    {
                        items.add(item);
                    }
                }
            }
        }
    }

    public Criterion getCriteria()
    {
        return criteria;
    }

    public void setCriteria(Criterion criteria)
    {
        this.criteria = criteria;
    }
}
