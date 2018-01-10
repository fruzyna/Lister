package com.liamfruzyna.android.wishlister.data;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by mail929 on 2/24/17.
 */

public class IO
{
    public static final String PREFS = "LISTER";
    public static final String DATES_AS_DAY = "DATES_AS_DAY";
    public static final String DATES_AS_DAYS_UNTIL = "DATES_AS_DAYS_UNTIL";
    public static final String US_DATE_FORMAT_PREF = "US_DATE_FORMAT_PREF";
    public static final String HIGHLIGHT_DATE_PREF = "HIGHLIGHT_DATE_PREF";
    public static final String HIGHLIGHT_WHOLE_ITEM_PREF = "HIGHLIGHT_WHOLE_ITEM_PREF";
    public static final String SERVER_USER_PREF = "SERVER_USER_PREF";
    public static final String SERVER_PASS_PREF = "SERVER_PASS_PREF";
    public static final String CURRENT_LIST_PREF = "CURRENT_LIST_PREF";

    public static final String fileDir = Environment.getExternalStoragePublicDirectory("Lister").toString();

    public static IO instance;

    private SharedPreferences prefs;

    public static boolean ready;

    /**
     * Used to establish IO and it's SharedPreferences
     *
     * @param c Main activity used to init SharedPreferenes
     * @return Current instance of IO
     */
    public static IO firstInstance(Activity c)
    {
        if (instance == null)
        {
            instance = new IO(c);
        }
        return instance;
    }

    /**
     * Used to retrieve the current instance of IO
     * @return Current instance of IO
     */
    public static IO getInstance()
    {
        if (instance == null)
        {
            instance = new IO();
        }
        return instance;
    }

    public IO(Activity c)
    {
        prefs = c.getSharedPreferences(PREFS, 0);

        checkDir();
    }

    public IO()
    {
        log("Warning creating IO w/o context");

        checkDir();
    }

    /**
     * Creates the Lister directory if it doesn't exist
     */
    public void checkDir()
    {
        File dir = new File(fileDir);
        if(!dir.exists())
        {
            dir.mkdirs();
        }
    }

    /**
     * Saves a generic object to preferences
     * @param key Key to use for saving
     * @param obj Value to save
     */
    public void put(String key, Object obj)
    {
        SharedPreferences.Editor edit = getEditor();
        if(obj instanceof String)
        {
            edit.putString(key, (String) obj);
        }
        else if(obj instanceof Integer)
        {
            edit.putInt(key, (Integer) obj);
        }
        else if(obj instanceof Boolean)
        {
            edit.putBoolean(key, (Boolean) obj);
        }
        edit.commit();
    }

    /**
     * Stores a given String to file
     * @param name Name to use for the file
     * @param json Text to save in the file
     * @param append Append to existing file
     */
    public static void storeData(String name, String json, boolean append)
    {
        try
        {
            File file = new File(fileDir + File.separator + name);
            if(!file.exists())
            {
                file.createNewFile();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, append));
            bw.write(json);
            bw.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Stores a given String to file
     * @param name Name to use for the file
     * @param json Text to save in the file
     */
    public static void storeData(String name, String json)
    {
        storeData(name, json, false);
    }

    /**
     * Retrieves the saved data in a file
     * @param name Name of the file where data is saved
     * @return Text stored in the file
     */
    public static String retrieveData(String name)
    {
        File cache = new File(fileDir + File.separator + name);
        if(cache.exists())
        {
            try
            {
                BufferedReader br = new BufferedReader(new FileReader(cache));
                String json = "";
                String line;
                while((line = br.readLine()) != null)
                {
                    json += line + "\n";
                }
                return json;
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static void wipeFiles()
    {
        for(File file : (new File(fileDir)).listFiles())
        {
            file.delete();
        }
    }

    public static File getFile(String name)
    {
        return new File(fileDir + File.separator + name);
    }

    /**
     * Parse list items from a object (assuming it's a map)
     * @param lid Id of the list
     * @param listData Object containing the items
     */
    public static void parseListItems(int lid, Object listData)
    {
        List<Map<String, Object>> data = (List<Map<String, Object>>) listData;
        log("Table of length " + data.size() + " returned");
        ListObj list = Data.getList(lid);
        list.resetList();
        for(Map<String, Object> map : data)
        {
            int id = Integer.parseInt((String) map.get("id"));
            String text = (String) map.get("text");
            boolean done = Integer.parseInt((String) map.get("done")) == 1;
            Item item = new Item(text, id, lid, done);
            list.addItem(item);
        }
    }

    /**
     * Parse basic list data from a object (assuming it's a map)
     * @param listData Object containing the list data
     */
    public static void parseLists(Object listData)
    {
        List<Map<String, Object>> data = (List<Map<String, Object>>) listData;
        log("Table of length " + data.size() + " returned");
        Data.resetLists();
        for(Map<String, Object> map : data)
        {
            String name = (String) map.get("name");
            int id = Integer.parseInt((String) map.get("id"));
            char perm = ((String) map.get("perm")).charAt(0);
            boolean archived = Integer.parseInt((String) map.get("archived")) == 1;
            boolean showDone = Integer.parseInt((String) map.get("showDone")) == 1;
            boolean sortDate = Integer.parseInt((String) map.get("sortDate")) == 1;
            boolean sortDone = Integer.parseInt((String) map.get("sortDone")) == 1;
            int daysToDel = Integer.parseInt((String) map.get("daysToDel"));
            ListObj list = new ListObj(name, id, perm, archived, daysToDel, showDone, sortDate, sortDone);
            Data.replaceList(list);
        }
    }

    /**
     * Parses retrieved data from API or back up file to appropriate object
     * @param result String of data retrieved
     * @return Data as a String or Map
     */
    public static Object parseJSON(String result)
    {
        List<Map<String, Object>> rows = new ArrayList<>();
        try
        {
            JSONArray json = new JSONArray(result);
            for(int i = 0; i < json.length(); i++)
            {
                JSONObject jrow = json.getJSONObject(i);
                Map<String, Object> map = new HashMap<>();
                Iterator<String> it = jrow.keys();
                while(it.hasNext())
                {
                    String key = it.next();
                    Object value = jrow.get(key);
                    map.put(key, value);
                }
                rows.add(map);
            }
            log("Returning rows");
            return rows;
        }
        catch(JSONException e)
        {
            log("Invalid JSON, returning String");
            return result;
        }
    }

    public SharedPreferences getPrefs()
    {
        return prefs;
    }

    public SharedPreferences.Editor getEditor()
    {
        return prefs.edit();
    }

    public String getString(String key)
    {
        return prefs.getString(key, "");
    }

    public int getInt(String key)
    {
        return prefs.getInt(key, -1);
    }

    public long getLong(String key)
    {
        return prefs.getLong(key, 0);
    }

    public boolean getBoolean(String key, boolean temp)
    {
        return prefs.getBoolean(key, temp);
    }

    public static void log(String output)
    {
        String className = Thread.currentThread().getStackTrace()[3].getClassName();
        String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
        int lineNumber = Thread.currentThread().getStackTrace()[3].getLineNumber();

        //remove package from class name
        if(className.contains("."))
        {
            className = className.substring(className.lastIndexOf(".") + 1);
        }

        //only print if debug mode is enabled
        System.out.println("[" + className + "." + methodName + ":" + lineNumber + "] " + output);
    }
}