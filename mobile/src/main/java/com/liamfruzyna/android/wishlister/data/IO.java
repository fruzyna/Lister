package com.liamfruzyna.android.wishlister.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
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
    public static final String SERVER_ADDRESS_PREF = "SERVER_ADDRESS_PREF";
    public static final String SERVER_USER_PREF = "SERVER_USER_PREF";
    public static final String SERVER_PASS_PREF = "SERVER_PASS_PREF";

    public static final String fileDir = Environment.getExternalStoragePublicDirectory("Lister").toString();

    public static IO instance;

    private Activity c;
    private SharedPreferences prefs;

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
        this.c = c;
        prefs = c.getSharedPreferences(PREFS, 0);

        File dir = new File(fileDir);
        if(!dir.exists())
        {
            dir.mkdirs();
        }
    }

    public IO()
    {
        System.out.println("Warning creating IO w/o context");

        File dir = new File(fileDir);
        if(!dir.exists())
        {
            dir.mkdirs();
        }
    }

    public boolean getBoolean(String name, JSONObject container, boolean defaultValue) throws JSONException
    {
        if (container.has(name))
        {
            return container.getBoolean(name);
        }
        return defaultValue;
    }

    public int getInt(String name, JSONObject container) throws JSONException
    {
        if (container.has(name))
        {
            return container.getInt(name);
        }
        return 0;
    }

    public String getString(String name, JSONObject container) throws JSONException
    {
        if (container.has(name))
        {
            return container.getString(name);
        }
        return "";
    }

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

    public static void storeData(String name, String json)
    {
        try
        {
            File file = new File(fileDir + File.separator + name);
            if(!file.exists())
            {
                file.createNewFile();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(json);
            bw.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static String retrieveData(String name)
    {
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(fileDir + File.separator + name));
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
        return "";
    }

    public static void parseListItems(int lid, Object listData)
    {
        List<Map<String, Object>> data = (List<Map<String, Object>>) listData;
        System.out.println("Table of length " + data.size() + " returned");
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

    public static void parseLists(Object listData)
    {
        List<Map<String, Object>> data = (List<Map<String, Object>>) listData;
        System.out.println("Table of length " + data.size() + " returned");
        Data.resetLists();
        for(Map<String, Object> map : data)
        {
            String name = (String) map.get("name");
            int id = Integer.parseInt((String) map.get("id"));
            char perm = ((String) map.get("perm")).charAt(0);
            boolean archived = Boolean.parseBoolean((String) map.get("archived"));
            ListObj list = new ListObj(name, id, perm, archived);
            Data.replaceList(list);
        }
    }

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
            System.out.println("Returning rows");
            return rows;
        }
        catch(JSONException e)
        {
            System.out.println("Invalid JSON, returning String");
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
        return prefs.getInt(key, 0);
    }

    public long getLong(String key)
    {
        return prefs.getLong(key, 0);
    }

    public boolean getBoolean(String key, boolean temp)
    {
        return prefs.getBoolean(key, temp);
    }

    public boolean checkNetwork()
    {
        System.out.print("Checking network: ");
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if(getString(SERVER_ADDRESS_PREF).equals(""))
        {
            System.out.println("Bad address");
            return false;
        }

        if(activeNetwork == null)
        {
            System.out.println("No network");
            return false;
        }

        if(!activeNetwork.isConnectedOrConnecting())
        {
            System.out.println("Not connected");
            return false;
        }

        return true;
    }
}