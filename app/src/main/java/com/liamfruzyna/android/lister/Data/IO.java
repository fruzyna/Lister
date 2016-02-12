package com.liamfruzyna.android.lister.Data;

import android.os.Environment;

import com.liamfruzyna.android.lister.Activities.WLActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mail929 on 11/6/14.
 */
public class IO
{
    public static final String PREFS = "Lister Prefs";
    public static final String HIGHLIGHT_DATE_PREF = "HIGHLIGHT_DATE_PREF";
    public static final String CURRENT_LIST_PREF = "CURRENT_LIST_PREF";
    public static final String PASSWORD_PREF = "PASSWORD_PREF";
    public static final String HAS_PASSWORD_PREF = "HAS_PASSWORD_PREF";
    public static final String fileDir = Environment.getExternalStoragePublicDirectory("Lists").toString();

    //writes list datas to files for each list
    public static void save(List<WishList> lists)
    {
        try
        {
            for (WishList list : lists)
            {
                writeToFile(list.name, getListString(list));
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    //creates a json string based off of a list
    public static String getListString(WishList list) throws JSONException
    {
        JSONObject jlist = new JSONObject();
        jlist.put("name", list.name);
        jlist.put("archived", list.archived);
        jlist.put("order", list.order);
        jlist.put("auto", list.auto);
        if(list.auto)
        {
            AutoList alist = (AutoList) list;
            JSONArray jcriteria = new JSONArray();
            List<String> criteria = alist.criteria;
            for (String c : criteria)
            {
                jcriteria.put(c);
            }
            jlist.put("criteria", jcriteria);
        }
        else
        {
            JSONArray jitems = new JSONArray();
            List<Item> items = list.items;
            for (Item item : items)
            {
                JSONObject jitem = new JSONObject();
                jitem.put("item", item.item);
                jitem.put("done", item.done);
                jitems.put(jitem);
            }
            jlist.put("items", jitems);
        }
        JSONArray jtags = new JSONArray();
        List<String> tags = list.tags;
        for (String tag : tags)
        {
            jtags.put(tag);
        }
        jlist.put("tags", jtags);
        return jlist.toString();
    }
    
    //creates lists from all the save files
    public static List<WishList> load() throws JSONException, MalformedURLException
    {
        List<WishList> lists = new ArrayList<WishList>();
        List<String> jlists = readFromFile();
        for (String jliststr : jlists)
        {
            JSONObject jlist = new JSONObject(jliststr);
            List<Item> items = new ArrayList<>();
            List<String> criteria = new ArrayList<>();
            List<String> tags = new ArrayList<String>();
            JSONArray jtags = jlist.getJSONArray("tags");
            for (int j = 0; j < jtags.length(); j++)
            {
                tags.add(jtags.getString(j));
            }
            boolean archived = false;
            if(jlist.has("archived"))
            {
                archived = jlist.getBoolean("archived");
            }
            int order = 0;
            if(jlist.has("order"))
            {
                order = jlist.getInt("order");
            }
            if(jlist.has("auto"))
            {
                boolean auto = jlist.getBoolean("auto");
                if(auto)
                {
                    JSONArray jcriteria = jlist.getJSONArray("criteria");
                    for (int j = 0; j < jcriteria.length(); j++)
                    {
                        criteria.add((String) jcriteria.get(j));
                    }
                    lists.add(new AutoList(jlist.getString("name"), tags, archived, order, criteria));
                }
                else
                {
                    JSONArray jitems = jlist.getJSONArray("items");
                    for (int j = 0; j < jitems.length(); j++)
                    {
                        Item item = new Item(jitems.getJSONObject(j).getString("item"), jitems.getJSONObject(j).getBoolean("done"));
                        items.add(item);
                    }
                    lists.add(new WishList(jlist.getString("name"), items, tags, archived, order));
                }
            }
            else
            {
                JSONArray jitems = jlist.getJSONArray("items");
                for (int j = 0; j < jitems.length(); j++)
                {
                    Item item = new Item(jitems.getJSONObject(j).getString("item"), jitems.getJSONObject(j).getBoolean("done"));
                    items.add(item);
                }
                lists.add(new WishList(jlist.getString("name"), items, tags, archived, order));
            }
        }
        return lists;
    }

    //creates a single list from a json string
    public static WishList readString(String json) throws JSONException
    {
        JSONObject jlist = new JSONObject(json);
        List<Item> items = new ArrayList<>();
        List<String> criteria = new ArrayList<>();
        boolean auto = jlist.getBoolean("auto");
        List<String> tags = new ArrayList<>();
        JSONArray jtags = jlist.getJSONArray("tags");
        for (int j = 0; j < jtags.length(); j++)
        {
            tags.add(jtags.getString(j));
        }
        boolean archived = false;
        if(jlist.has("archived"))
        {
            archived = jlist.getBoolean("archived");
        }
        int order = 0;
        if(jlist.has("order"))
        {
            order = jlist.getInt("order");
        }
        if(auto)
        {
            JSONArray jcriteria = jlist.getJSONArray("criteria");
            for (int j = 0; j < jcriteria.length(); j++)
            {
                criteria.add((String) jcriteria.get(j));
            }
            return new AutoList(jlist.getString("name"), tags, archived, order, criteria);
        }
        else
        {
            JSONArray jitems = jlist.getJSONArray("items");
            for (int j = 0; j < jitems.length(); j++)
            {
                Item item = new Item(jitems.getJSONObject(j).getString("item"), jitems.getJSONObject(j).getBoolean("done"));
                items.add(item);
            }
            return new WishList(jlist.getString("name"), items, tags, archived, order);
        }
    }

    //takes a list's json string and saves it to a file
    private static void writeToFile(String name, String data)
    {
        File dir = new File(fileDir);
        dir.mkdirs();
        File file = new File(fileDir, name + ".json");
        log("IO", "Writing to " + file.toString());
        if(!file.exists())
        {
            try
            {
                file.createNewFile();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, false));
            bw.write(data);
            bw.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //creates a list of strings from all the save files
    public static List<String> readFromFile()
    {
        List<String> data = new ArrayList<>();
        File[] files = new File(fileDir).listFiles();
        if(files == null)
        {
            files = new File[0];
        }
        for(int i = 0; i < files.length; i++)
        {
            File file = files[i];
            log("IO", "Reading from " + file.toString());
            StringBuilder sb = new StringBuilder();
            try
            {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null)
                {
                    sb.append(line);
                }
                data.add(sb.toString());
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return data;
    }

    public static void log(String title, String message)
    {
        System.out.println("[" + title + "] " + message);
    }
}
