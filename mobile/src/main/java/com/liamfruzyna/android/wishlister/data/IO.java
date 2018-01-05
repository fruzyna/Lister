package com.liamfruzyna.android.wishlister.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
    public static final String HEADER_PREF = "HEADER_PREF";

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

    public void checkItem(Item item)
    {
        Object result = DbConnection.runQuery("checkitem/?id=" + item.getId() + "&done=" + item.isDone());
        if(result instanceof String)
        {
            String response = (String) result;
            if(response.equals("Network Failure"))
            {
                System.out.println("Failed to connect to server");
            }
            else if(response.equals("Not logged in!"))
            {
                System.out.println("Not logged in");
            }
            else if(response.equals("Success"))
            {
                System.out.println("Updated");
            }
            else
            {
                System.out.println("Unknown result: " + response);
            }
        }
        else
        {
            System.out.println("Unknown result: rows");
        }
    }

    public void removeItem(Item item)
    {
        Object result = DbConnection.runQuery("deleteitem/?id=" + item.getId());
        if(result instanceof String)
        {
            String response = (String) result;
            if(response.equals("Network Failure"))
            {
                System.out.println("Failed to connect to server");
            }
            else if(response.equals("Not logged in!"))
            {
                System.out.println("Not logged in");
            }
            else if(response.equals("Success"))
            {
                System.out.println("Updated");
            }
            else
            {
                System.out.println("Unknown result: " + response);
            }
        }
        else
        {
            System.out.println("Unknown result: rows");
        }
    }

    public void editItem(Item item)
    {
        Object result = DbConnection.runQuery("edititem/?id=" + item.getId() + "&text=" + item.getItem());
        if(result instanceof String)
        {
            String response = (String) result;
            if(response.equals("Network Failure"))
            {
                System.out.println("Failed to connect to server");
            }
            else if(response.equals("Not logged in!"))
            {
                System.out.println("Not logged in");
            }
            else if(response.equals("Success"))
            {
                System.out.println("Updated");
            }
            else
            {
                System.out.println("Unknown result: " + response);
            }
        }
        else
        {
            System.out.println("Unknown result: rows");
        }
    }

    public void addItem(String item, ListObj list)
    {
        Object result = DbConnection.runQuery("additem/?lid=" + list.getId() + "&text=" + item);
        if(result instanceof String)
        {
            String response = (String) result;
            if(response.equals("Network Failure"))
            {
                System.out.println("Failed to connect to server");
            }
            else if(response.equals("Not logged in!"))
            {
                System.out.println("Not logged in");
            }
            else if(response.equals("Success"))
            {
                System.out.println("Updated");
            }
            else
            {
                System.out.println("Unknown result: " + response);
            }
        }
        else
        {
            System.out.println("Unknown result: rows");
        }
    }

    public void createList(String name, int days, boolean auto)
    {
        Object result = DbConnection.runQuery("createlist/?name=" + name + "&daysToDel=" + days + "&auto=" + auto);
        if(result instanceof String)
        {
            String response = (String) result;
            if(response.equals("Network Failure"))
            {
                System.out.println("Failed to connect to server");
            }
            else if(response.equals("Not logged in!"))
            {
                System.out.println("Not logged in");
            }
            else if(response.equals("Success"))
            {
                System.out.println("Updated");
            }
            else
            {
                System.out.println("Unknown result: " + response);
            }
        }
        else
        {
            System.out.println("Unknown result: rows");
        }
    }

    public void deleteList(ListObj list)
    {
        Object result = DbConnection.runQuery("deletelist/?lid=" + list.getId());
        if(result instanceof String)
        {
            String response = (String) result;
            if(response.equals("Network Failure"))
            {
                System.out.println("Failed to connect to server");
            }
            else if(response.equals("Not logged in!"))
            {
                System.out.println("Not logged in");
            }
            else if(response.equals("Success"))
            {
                System.out.println("Deleted");
            }
            else
            {
                System.out.println("Unknown result: " + response);
            }
        }
        else
        {
            System.out.println("Unknown result: rows");
        }
    }

    public void archiveList(ListObj list)
    {
        Object result = DbConnection.runQuery("archivelist/?lid=" + list.getId() + "&archived=" + list.isArchived());
        if(result instanceof String)
        {
            String response = (String) result;
            if(response.equals("Network Failure"))
            {
                System.out.println("Failed to connect to server");
            }
            else if(response.equals("Not logged in!"))
            {
                System.out.println("Not logged in");
            }
            else if(response.equals("Success"))
            {
                System.out.println("Deleted");
            }
            else
            {
                System.out.println("Unknown result: " + response);
            }
        }
        else
        {
            System.out.println("Unknown result: rows");
        }
    }

    public void shareList(ListObj list, String user, char permLevel)
    {
        Object result = DbConnection.runQuery("sharelist/?lid=" + list.getId() + "&level=" + permLevel + "&user=" + user);
        if(result instanceof String)
        {
            String response = (String) result;
            if(response.equals("Network Failure"))
            {
                System.out.println("Failed to connect to server");
            }
            else if(response.equals("Not logged in!"))
            {
                System.out.println("Not logged in");
            }
            else if(response.equals("Success"))
            {
                System.out.println("User Added");
            }
            else
            {
                System.out.println("Unknown result: " + response);
            }
        }
        else
        {
            System.out.println("Unknown result: rows");
        }
    }

    public void leaveList(ListObj list)
    {
        Object result = DbConnection.runQuery("leavelist/?lid=" + list.getId());
        if(result instanceof String)
        {
            String response = (String) result;
            if(response.equals("Network Failure"))
            {
                System.out.println("Failed to connect to server");
            }
            else if(response.equals("Not logged in!"))
            {
                System.out.println("Not logged in");
            }
            else if(response.equals("Success"))
            {
                System.out.println("List Removed");
            }
            else
            {
                System.out.println("Unknown result: " + response);
            }
        }
        else
        {
            System.out.println("Unknown result: rows");
        }
    }


    public void pullLists()
    {
        Object result = DbConnection.runQuery("getuserlists");
        if(result instanceof String)
        {
            String response = (String) result;
            if(response.equals("Network Failure"))
            {
                System.out.println("Failed to connect to server");
            }
            else if(response.equals("Not logged in!"))
            {
                System.out.println("Not logged in");
            }
            else
            {
                System.out.println("Unknown result: " + response);
            }
        }
        else
        {
            List<Map<String, Object>> data = (List<Map<String, Object>>) result;
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

            for(ListObj list : Data.getLists())
            {
                pullList(list.getId());
            }
        }
    }

    public void pullList(int lid)
    {
        Object result = DbConnection.runQuery("getlistitems/?lid=" + lid);
        if(result instanceof String)
        {
            String response = (String) result;
            if(response.equals("Network Failure"))
            {
                System.out.println("Failed to connect to server");
            }
            else if(response.equals("Not logged in!"))
            {
                System.out.println("Not logged in");
            }
            else
            {
                System.out.println("Unknown result: " + response);
            }
        }
        else
        {
            List<Map<String, Object>> data = (List<Map<String, Object>>) result;
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