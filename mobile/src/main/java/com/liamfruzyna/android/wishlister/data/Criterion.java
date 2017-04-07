package com.liamfruzyna.android.wishlister.data;

import java.util.List;

/**
 * Created by liam on 4/7/17.
 */

public class Criterion
{
    boolean not;
    int category;
    String data;
    CriteriaTypes type;
    List<Criterion> criteria;

    public Criterion(CriteriaTypes type, int category, boolean not, String data, List<Criterion> criteria)
    {
        this.type = type;
        this.category = category;
        this.criteria = criteria;
        this.data = data;
        this.not = not;
    }

    public boolean passesCriterion(String item)
    {
        boolean state = false;
        switch(type)
        {
            case PASS_ALL:
                state = true;
                for(Criterion c : criteria)
                {
                    if(!c.passesCriterion(item))
                    {
                        state = false;
                    }
                }
                break;
            case PASS_ONE:
                for(Criterion c : criteria)
                {
                    if(c.passesCriterion(item))
                    {
                        state = true;
                        break;
                    }
                }
                break;
            case TAG:
                if(item.contains("#" + data))
                {
                    state = true;
                }
                break;
            case PERSON:
                if(item.contains("@" + data))
                {
                    state = true;
                }
                break;
            case DATE:
                break;
            case DATE_RANGE:
                break;
            case DAY_OF_WEEK:
                break;
            case WITHIN_DAYS:
                break;
        }

        if(not)
        {
            state = !state;
        }
        return state;
    }
}
