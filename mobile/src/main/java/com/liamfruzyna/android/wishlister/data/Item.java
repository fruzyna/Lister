package com.liamfruzyna.android.wishlister.data;

/**
 * Created by mail929 on 2/24/17.
 */

public class Item
{
    private boolean done;
    private String item;
    private int id;
    private int lid;

    public Item(String item, int id, int lid, boolean done)
    {
        this.item = item;
        this.id = id;
        this.lid = lid;
        this.done = done;
    }

    public String getItem()
    {
        return item;
    }

    public Boolean isDone()
    {
        return done;
    }

    public int getId()
    {
        return id;
    }

    public int getParent()
    {
        return lid;
    }

    public void setDone(boolean done)
    {
        this.done = done;
    }

    public void setItem(String item)
    {
        this.item = item;
    }

/*
    public String color = "#000000";
    public List<String> people = new ArrayList<>();
    public List<String> tags = new ArrayList<>();
    public Date date = new Date(1097, 3, 24);
    public String formattedDate = "NONE";

    //the constructor if you don't know what this is don't ask me
    public Item(String item, Boolean done)
    {
        this.item = item;
        parseItem();
        this.done = done;
    }

    public Item(String item)
    {
        this.item = item;
        parseItem();
        done = false;
    }

    public boolean deleteItem(int daysToDelete)
    {
        if(done && daysToDelete != 0)
        {
            Calendar byCal = Calendar.getInstance();
            byCal.add(Calendar.DAY_OF_YEAR, -daysToDelete);
            byCal.add(Calendar.MINUTE, 5);
            Date by = byCal.getTime();
            if(date.before(by))
            {
                return true;
            }
        }
        return false;
    }

    //looks for tags within a list item
    public void parseItem()
    {
        if(item.contains("@"))
        {
            //person tag
            String[] people = item.split("@");
            findPeople(people);
        }
        if(item.contains("*"))
        {
            priority = true;
        }
        else
        {
            priority = false;
        }
        if(item.contains("#"))
        {
            //tag
            String[] tags = item.split("\\#");
            findTags(tags);
        }
        if(item.contains("/"))
        {
            //date
            String[] date = item.split("/");

            int m, d;
            if(IO.getInstance().getBoolean(IO.US_DATE_FORMAT_PREF, true))
            {
                m = 0;
                d = 1;
            }
            else
            {
                m = 1;
                d = 0;
            }

            try {
                if(date.length == 3)
                {
                    //if the date has month day and year
                    int day = Integer.parseInt(date[d]);
                    int month;
                    int year;
                    if (date[m].contains(" ")) {
                        String[] start = date[m].split(" ");
                        month = Integer.parseInt(start[start.length - 1]);
                    } else {
                        month = Integer.parseInt(date[m]);
                    }
                    month -= 1;
                    if (date[2].contains(" ")) {
                        String[] end = date[2].split(" ");
                        year = Integer.parseInt(end[0]);
                    } else {
                        year = Integer.parseInt(date[2]);
                    }
                    formattedDate = (month+1) + "/" + day + "/" + year;
                    if(year < 2000)
                    {
                        year += 2000;
                    }
                    this.date = new Date(year, month, day);
                }
                else if(date.length == 2)
                {
                    //if the date just has month and day
                    int day;
                    int month;
                    int year;
                    if (date[m].contains(" ")) {
                        String[] start = date[m].split(" ");
                        month = Integer.parseInt(start[start.length - 1]);
                    } else {
                        month = Integer.parseInt(date[m]);
                    }
                    month -= 1;
                    if (date[d].contains(" ")) {
                        String[] end = date[d].split(" ");
                        day = Integer.parseInt(end[0]);
                    } else {
                        day = Integer.parseInt(date[d]);
                    }
                    year = Calendar.getInstance().getTime().getYear();
                    this.date = new Date(year, month, day);
                    formattedDate = (1+month) + "/" + day;
                }
            } catch(NumberFormatException e) {
            }
        }
    }

    //finds all the people in all the list's items
    public void findPeople(String[] strings)
    {
        for(int i = 1; i < strings.length; i++)
        {
            String person = strings[i];
            if (person.contains(" "))
            {
                person = person.split(" ")[0];
            }
            people.add(person);
        }
    }

    //finds all the tags in all the list's items
    public void findTags(String[] strings)
    {
        for(int i = 1; i < strings.length; i++)
        {
            String tag = strings[i];
            if (tag.contains(" "))
            {
                tag = tag.split(" ")[0];
            }
            tags.add(tag);

            if(isColor(tag))
            {
                //color
                String color = tag;
                if(color.contains(" "))
                {
                    color = color.split(" ")[0];
                }
                this.color = "#" + color;
            }
        }
    }

    public boolean isColor(String color)
    {
        color = color.toLowerCase();
        if(color.length() == 6)
        {
            for(int i = 0; i < color.length(); i++)
            {
                char c = color.charAt(i);
                if(c != 'a' && c != 'b' && c != 'c' && c != 'c' && c != 'e' && c != 'f' &&
                        c != '0' && c != '1' && c != '2' && c != '3' && c != '4' && c != '5' && c != '6' && c != '7' && c != '8' && c != '9')
                {
                    return false;
                }
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean equals(Item item)
    {
        return (item.done == done) && (item.item.equals(item));
    } */
}