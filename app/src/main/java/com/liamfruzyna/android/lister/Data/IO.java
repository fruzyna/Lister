package com.liamfruzyna.android.lister.Data;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.liamfruzyna.android.lister.WLActivity;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by mail929 on 11/6/14.
 */
public class IO
{
    public static final String PREFS = "Lister Prefs";
    public static final String HIGHLIGHT_DATE_PREF = "HIGHLIGHT_DATE_PREF";
    public static final String CURRENT_LIST_PREF = "CURRENT_LIST_PREF";
    public static final String NAME_OBJ = "name";
    public static final String ARCHIVED_OBJ = "archived";
    public static final String ORDER_OBJ = "order";
    public static final String AUTO_OBJ = "auto";
    public static final String SHOW_DONE_OBJ = "showDone";
    public static final String DAYS_TO_DELETE_OBJ = "daysToDelete";
    public static final String CRITERIA_OBJ = "criteria";
    public static final String ITEM_OBJ = "item";
    public static final String DONE_OBJ = "done";
    public static final String ITEMS_OBJ = "items";
    public static final String TAGS_OBJ = "tags";

    public static final String fileDir = Environment.getExternalStoragePublicDirectory("Lists").toString();

    private static Context c;

    //writes list datas to files for each list
    public static void save()
    {
        try
        {
            for (WishList list : Data.getLists())
            {
                writeToFile(list.name, getListString(list));
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public static void saveList()
    {
        WishList list = Data.getCurrentList();
        System.out.println("Saving: " + list.name);
        try
        {
            if (list.auto)
            {
                save();
            }
            else
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
        jlist.put(NAME_OBJ, list.name);
        jlist.put(ARCHIVED_OBJ, list.archived);
        jlist.put(ORDER_OBJ, list.order);
        jlist.put(AUTO_OBJ, list.auto);
        jlist.put(SHOW_DONE_OBJ, list.showDone);
        jlist.put(DAYS_TO_DELETE_OBJ, list.daysToDelete);
        if (list.auto)
        {
            AutoList alist = (AutoList) list;
            JSONArray jcriteria = new JSONArray();
            List<String> criteria = alist.criteria;
            for (String c : criteria)
            {
                jcriteria.put(c);
            }
            jlist.put(CRITERIA_OBJ, jcriteria);
        }
        else
        {
            JSONArray jitems = new JSONArray();
            List<Item> items = list.items;
            for (Item item : items)
            {
                JSONObject jitem = new JSONObject();
                jitem.put(ITEM_OBJ, item.item);
                jitem.put(DONE_OBJ, item.done);
                jitems.put(jitem);
            }
            jlist.put(ITEMS_OBJ, jitems);
        }
        JSONArray jtags = new JSONArray();
        List<String> tags = list.tags;
        for (String tag : tags)
        {
            jtags.put(tag);
        }
        jlist.put(TAGS_OBJ, jtags);
        return jlist.toString();
    }

    public static boolean getBoolean(String name, JSONObject container) throws JSONException
    {
        if (container.has(name))
        {
            return container.getBoolean(name);
        }
        return false;
    }

    public static int getInt(String name, JSONObject container) throws JSONException
    {
        if (container.has(name))
        {
            return container.getInt(name);
        }
        return 0;
    }

    public static void finishLoad(List<String> jlists)
    {
        List<WishList> lists = new ArrayList<>();
        for (String jliststr : jlists)
        {
            try
            {
                lists.add(readString(jliststr));
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        Data.setLists(lists);
    }

    //creates a single list from a json string
    public static WishList readString(String json) throws JSONException
    {
        JSONObject jlist = new JSONObject(json);
        List<Item> items = new ArrayList<>();
        List<String> criteria = new ArrayList<>();
        boolean auto = getBoolean(AUTO_OBJ, jlist);
        List<String> tags = new ArrayList<>();
        JSONArray jtags = jlist.getJSONArray(TAGS_OBJ);
        for (int j = 0; j < jtags.length(); j++)
        {
            tags.add(jtags.getString(j));
        }
        boolean archived = getBoolean(ARCHIVED_OBJ, jlist);
        boolean showDone = getBoolean(SHOW_DONE_OBJ, jlist);
        int daysToDelete = getInt(DAYS_TO_DELETE_OBJ, jlist);
        int order = getInt(ORDER_OBJ, jlist);
        if (auto)
        {
            JSONArray jcriteria = jlist.getJSONArray(CRITERIA_OBJ);
            for (int j = 0; j < jcriteria.length(); j++)
            {
                criteria.add((String) jcriteria.get(j));
            }
            return new AutoList(jlist.getString(NAME_OBJ), tags, archived, order, criteria, showDone, daysToDelete);
        }
        else
        {
            JSONArray jitems = jlist.getJSONArray(ITEMS_OBJ);
            for (int j = 0; j < jitems.length(); j++)
            {
                Item item = new Item(jitems.getJSONObject(j).getString(ITEM_OBJ), jitems.getJSONObject(j).getBoolean(DONE_OBJ));
                items.add(item);
            }
            return new WishList(jlist.getString(NAME_OBJ), items, tags, archived, order, showDone, daysToDelete);
        }
    }

    //takes a list's json string and saves it to a file
    private static File writeToFile(String name, String data)
    {
        File dir = new File(fileDir);
        dir.mkdirs();
        File file = new File(fileDir, name + ".json");
        log("IO", "Writing to " + file.toString());
        if (!file.exists())
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
        System.out.println("File TS: " + file.lastModified());
        return file;
    }

    //creates a list of strings from all the save files
    public static List<String> readFromFile()
    {
        List<String> data = new ArrayList<>();
        File[] files = new File(fileDir).listFiles();
        if (files == null)
        {
            files = new File[0];
        }
        for (int i = 0; i < files.length; i++)
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
