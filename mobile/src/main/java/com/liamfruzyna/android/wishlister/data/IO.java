package com.liamfruzyna.android.wishlister.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mail929 on 2/24/17.
 */

public class IO
{
    public static final String PREFS = "Lister Prefs";
    public static final String DATES_AS_DAY = "DATES_AS_DAY";
    public static final String DATES_AS_DAYS_UNTIL = "DATES_AS_DAYS_UNTIL";
    public static final String US_DATE_FORMAT_PREF = "US_DATE_FORMAT_PREF";
    public static final String HIGHLIGHT_DATE_PREF = "HIGHLIGHT_DATE_PREF";
    public static final String HIGHLIGHT_WHOLE_ITEM_PREF = "HIGHLIGHT_WHOLE_ITEM_PREF";
    public static final String CURRENT_LIST_PREF = "CURRENT_LIST_PREF";
    public static final String FIRST_PREF = "FIRST_PREF";
    public static final String SERVER_PREF = "SERVER_PREF";
    public static final String LOGGED_IN_PREF = "LOGGED_IN_PREF";
    public static final String SERVER_ADDRESS_PREF = "SERVER_ADDRESS_PREF";
    public static final String SERVER_USER_PREF = "SERVER_USER_PREF";
    public static final String SERVER_PASSWORD_PREF = "SERVER_PASSWORD_PREF";
    public static final String TIME_PREF = "TIME_PREF";
    public static final String NAME_OBJ = "name";
    public static final String ARCHIVED_OBJ = "archived";
    public static final String AUTO_OBJ = "auto";
    public static final String SHOW_DONE_OBJ = "showDone";
    public static final String SORT_CHECKED_OBJ = "sortChecked";
    public static final String SORT_DATE_OBJ = "sortDate";
    public static final String DAYS_TO_DELETE_OBJ = "daysToDelete";
    public static final String CRITERIA_OBJ = "criteria";
    public static final String ITEM_OBJ = "item";
    public static final String DONE_OBJ = "done";
    public static final String ITEMS_OBJ = "items";
    public static final String TAGS_OBJ = "tags";
    public static final String CRITERIA_NOT_OBJ = "criteriaNot";
    public static final String CRITERIA_GROUP_OBJ = "criteriaGroup";
    public static final String CRITERIA_TYPE_OBJ = "criteriaType";
    public static final String CRITERIA_DATA_OBJ = "criteriaData";
    public static final String CRITERIA_CHILDREN_OBJ = "criteriaChildren";

    public static final String fileDir = Environment.getExternalStoragePublicDirectory("Lists").toString();

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
    }

    public IO()
    {
        System.out.println("Warning creating IO w/o context");
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

    //takes a list's json string and saves it to a file
    private File writeToFile(String name, String data)
    {
        File dir = new File(fileDir);
        dir.mkdirs();
        File file = new File(fileDir, name + ".json");
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
        return file;
    }

    //creates a list of strings from all the save files
    public List<String> readFromFile()
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