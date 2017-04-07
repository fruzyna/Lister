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

    public AutoList(String name, java.util.List<String> tags, boolean archived, String criteria, boolean showDone, int daysToDelete, boolean sortChecked, boolean sortDate)
    {
        super(name, tags, archived, showDone, daysToDelete, sortChecked, sortDate);
        auto = true;
        this.criteria = parseCriterion(criteria);
        this.showDone = showDone;
    }

    public AutoList(String name, java.util.List<String> tags, String criteria, boolean showDone, int daysToDelete)
    {
        super(name, tags, showDone, daysToDelete);
        auto = true;
        this.criteria = parseCriterion(criteria);
        this.showDone = showDone;
    }

    public Criterion parseCriterion(String string)
    {
        for(String criterion : string.split("\n"))
        {
            String[] parts = criterion.split(" ");
            boolean not = criterion.contains("not");
            int group = Integer.parseInt(parts[3]);
            CriteriaTypes type = CriteriaTypes.valueOf(parts[6]);
            Criterion c = new Criterion(type, group, not, parts[8], new ArrayList<Criterion>());
            return c;
        }
    }

    public void findItems()
    {
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
