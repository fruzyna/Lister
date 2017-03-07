package com.liamfruzyna.android.wishlister;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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
    public static final String CURRENT_LIST_PREF = "CURRENT_LIST_PREF";
    public static final String FIRST_PREF = "FIRST_PREF";
    public static final String SERVER_PREF = "SERVER_PREF";
    public static final String SERVER_ADDRESS_PREF = "SERVER_ADDRESS_PREF";
    public static final String SERVER_USER_PREF = "SERVER_USER_PREF";
    public static final String SERVER_PASSWORD_PREF = "SERVER_PASSWORD_PREF";
    public static final String TIME_PREF = "TIME_PREF";
    public static final String NAME_OBJ = "name";
    public static final String ARCHIVED_OBJ = "archived";
    public static final String AUTO_OBJ = "auto";
    public static final String SHOW_DONE_OBJ = "showDone";
    public static final String DAYS_TO_DELETE_OBJ = "daysToDelete";
    public static final String CRITERIA_OBJ = "criteria";
    public static final String ITEM_OBJ = "item";
    public static final String DONE_OBJ = "done";
    public static final String ITEMS_OBJ = "items";
    public static final String TAGS_OBJ = "tags";

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


    public JSONObject toJSONObject(ListObj list) throws JSONException
    {
        JSONObject jlist = new JSONObject();
        jlist.put(NAME_OBJ, list.name);
        jlist.put(ARCHIVED_OBJ, list.archived);
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
        } else
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
        return jlist;
    }

    //creates a json string based off of a list
    public String getListString(ListObj list) throws JSONException
    {
        return toJSONObject(list).toString();
    }

    public boolean getBoolean(String name, JSONObject container) throws JSONException
    {
        if (container.has(name))
        {
            return container.getBoolean(name);
        }
        return false;
    }

    public int getInt(String name, JSONObject container) throws JSONException
    {
        if (container.has(name))
        {
            return container.getInt(name);
        }
        return 0;
    }

    //creates a single list from a json string
    public ListObj readString(String json) throws JSONException
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
        if (auto)
        {
            JSONArray jcriteria = jlist.getJSONArray(CRITERIA_OBJ);
            for (int j = 0; j < jcriteria.length(); j++)
            {
                criteria.add((String) jcriteria.get(j));
            }
            return new AutoList(jlist.getString(NAME_OBJ), tags, archived, criteria, showDone, daysToDelete);
        } else
        {
            JSONArray jitems = jlist.getJSONArray(ITEMS_OBJ);
            for (int j = 0; j < jitems.length(); j++)
            {
                Item item = new Item(jitems.getJSONObject(j).getString(ITEM_OBJ), jitems.getJSONObject(j).getBoolean(DONE_OBJ));
                items.add(item);
            }
            return new ListObj(jlist.getString(NAME_OBJ), items, tags, archived, showDone, daysToDelete);
        }
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

    public void loadFromFile()
    {
        List<String> files = readFromFile();
        List<ListObj> lists = new ArrayList<>();
        for (String file : files)
        {
            try
            {
                lists.add(readString(file));
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        Data.setLists(lists);
    }

    public void saveToFile()
    {
        for (ListObj list : Data.getLists())
        {
            try
            {
                writeToFile(list.name, getListString(list));
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void uploadLists()
    {
        for (ListObj list : Data.getLists())
        {
            try
            {
                (new UploadListTask()).execute(getListString(list));
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void saveAndSync()
    {
        ListObj current = Data.getCurrentList();
        if(current.auto)
        {
            saveToFile();
            if(checkNetwork())
            {
                uploadLists();
            }
        }
        else
        {
            try
            {
                String listString = getListString(current);
                System.out.println("Saving " + listString);
                writeToFile(current.name, listString);
                if(checkNetwork())
                {
                    (new UploadListTask()).execute(listString);
                }
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        SharedPreferences.Editor edit = getEditor();
        edit.putLong(TIME_PREF, System.currentTimeMillis());
        edit.commit();
    }

    public void deleteList(String name)
    {
        DeleteListTask task = new DeleteListTask();
        task.execute(name);
        File file = new File(fileDir, name + ".json");
        file.delete();
        while(!task.done);
    }

    private class UploadListTask extends AsyncTask<String, Integer, String>
    {
        protected String doInBackground(String... urls)
        {
            String data = urls[0].replace("#", "[^]");
            String urlString = "http://" + getString(SERVER_ADDRESS_PREF) + "/sync/?user=" + getString(SERVER_USER_PREF) + "&password=" + getString(SERVER_PASSWORD_PREF) + "&data=" + data;
            return webRequest(urlString);
        }

        protected void onProgressUpdate(Integer... progress)
        {
        }

        protected void onPostExecute(String result)
        {
        }
    }

    private class DeleteListTask extends AsyncTask<String, Integer, String>
    {
        boolean done = false;
        protected String doInBackground(String... urls)
        {
            String urlString = "http://" + prefs.getString(SERVER_ADDRESS_PREF, "") + "/remove/?user=" + prefs.getString(SERVER_USER_PREF, "") + "&password=" + prefs.getString(SERVER_PASSWORD_PREF, "") + "&list=" + urls[0];
            done = true;
            return webRequest(urlString);
        }

        protected void onProgressUpdate(Integer... progress)
        {
        }

        protected void onPostExecute(String result)
        {
            System.out.println(result);
            done = true;
        }
    }

    public String sync()
    {
        SyncListsTask task = new SyncListsTask();
        task.execute();
        System.out.println("started");
        String status = task.status;
        while (status.equals("RUNNING"))
        {
            //System.out.println("status: " + task.status);
            status = task.status;
        }
        System.out.println(status);
        return task.status;
    }

    private class SyncListsTask extends AsyncTask<String, Integer, ArrayList<ListObj>>
    {
        String status = "RUNNING";

        protected ArrayList<ListObj> doInBackground(String... urls)
        {
            try
            {
                String server = getString(SERVER_ADDRESS_PREF);
                String user = getString(SERVER_USER_PREF);
                String password = getString(SERVER_PASSWORD_PREF);
                String urlString = "http://" + server + "/getLists/?user=" + user + "&password=" + password;
                String result = webRequest(urlString);
                if (result != null)
                {
                    ArrayList<ListObj> list = new ArrayList<>();
                    System.out.println("Avail Lists: " + result);
                    JSONArray lists = new JSONArray(result);
                    for (int i = 0; i < lists.length(); i++)
                    {
                        try
                        {
                            JSONObject jlist = lists.getJSONObject(i);
                            if (Long.parseLong(jlist.getString("time")) > getLong(TIME_PREF))
                            {
                                urlString = "http://" + server + "/get/?user=" + server + "&password=" + password + "&list=" + jlist.getString("name");
                                String output = webRequest(urlString);
                                System.out.println(jlist.getString("name") + ": " + output);
                                ListObj wl = readString(output);
                                list.add(wl);
                                Data.replaceList(wl);
                            } else
                            {
                            }
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                    }
                    status = "DONE";
                    return list;
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            status = "ERROR";
            return null;
        }

        protected void onProgressUpdate(Integer... progress)
        {
        }

        protected void onPostExecute(ArrayList<ListObj> result)
        {
            if (result != null)
            {
                if (result.size() > 0)
                {
                    saveToFile();
                }
            } else
            {
                System.out.println("Error fetching remote lists");
            }
        }
    }

    public void syncOffThread()
    {
        loadFromFile();
        String server = getString(SERVER_ADDRESS_PREF);
        String user = getString(SERVER_USER_PREF);
        String password = getString(SERVER_PASSWORD_PREF);
        String urlString = "http://" + server + "/getLists/?user=" + user + "&password=" + password;
        String result = webRequest(urlString);
        if (result != null)
        {
            ArrayList<ListObj> lists = new ArrayList<>();
            System.out.println("Avail Lists: " + result);
            JSONArray jLists = null;
            try
            {
                jLists = new JSONArray(result);
                for (int i = 0; i < jLists.length(); i++)
                {
                    try
                    {
                        JSONObject jlist = jLists.getJSONObject(i);
                        if (Long.parseLong(jlist.getString("time")) > getLong(TIME_PREF))
                        {
                            urlString = "http://" + server + "/get/?user=" + user + "&password=" + password + "&list=" + jlist.getString("name");
                            String output = webRequest(urlString);
                            ListObj wl = readString(output);
                            lists.add(wl);
                            Data.replaceList(wl);
                        } else
                        {
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
            if (lists.size() > 0)
            {
                saveToFile();
            }
        }
    }

    public String createAccount(String username, String password, String server)
    {
        CreateAccountTask task = new CreateAccountTask();
        task.execute(server, username, password);
        String status = task.status;
        while (status.equals("RUNNING"))
        {
            System.out.println("status: " + task.status);
            status = task.status;
        }
        System.out.println(status);
        return task.status;
    }

    private class CreateAccountTask extends AsyncTask<String, Integer, String>
    {
        String status = "RUNNING";

        protected String doInBackground(String... urls)
        {
            try
            {
                URL url = new URL("http://" + urls[0] + "/createUser/?user=" + urls[1] + "&password=" + urls[2]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try
                {
                    BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    byte[] contents = new byte[1024];

                    int bytesRead = 0;
                    String result = "";
                    while ((bytesRead = in.read(contents)) != -1)
                    {
                        result += new String(contents, 0, bytesRead);
                    }

                    status = result;
                    return result;

                } finally
                {
                    urlConnection.disconnect();
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            status = "ERROR";
            return "ERROR";
        }

        protected void onProgressUpdate(Integer... progress)
        {
        }

        protected void onPostExecute(String result)
        {
            status = result;
        }
    }

    public String auth(String username, String password, String server)
    {
        AuthTask task = new AuthTask();
        task.execute(server, username, password);
        String status = task.status;
        while (status.equals("RUNNING"))
        {
            System.out.println("status: " + task.status);
            status = task.status;
        }
        System.out.println(status);
        return task.status;
    }

    private class AuthTask extends AsyncTask<String, Integer, String>
    {
        String status = "RUNNING";

        protected String doInBackground(String... urls)
        {
            try
            {
                URL url = new URL("http://" + urls[0] + "/auth/?user=" + urls[1] + "&password=" + urls[2]);
                System.out.println(url);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try
                {
                    BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    byte[] contents = new byte[1024];

                    int bytesRead = 0;
                    String result = "";
                    while ((bytesRead = in.read(contents)) != -1)
                    {
                        result += new String(contents, 0, bytesRead);
                    }

                    status = result;
                    return result;

                } finally
                {
                    urlConnection.disconnect();
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            status = "ERROR";
            return "ERROR";
        }

        protected void onProgressUpdate(Integer... progress)
        {
        }

        protected void onPostExecute(String result)
        {
            status = result;
        }
    }

    public String webRequest(String urlString)
    {
        try
        {
            System.out.println(urlString);
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try
            {
                BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
                byte[] contents = new byte[1024];

                int bytesRead = 0;
                String result = "";
                while ((bytesRead = in.read(contents)) != -1)
                {
                    result += new String(contents, 0, bytesRead);
                }
                in.close();
                System.out.println("Returning: " + result);
                return result;

            } finally
            {
                urlConnection.disconnect();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return "ERROR";
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
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return !getString(SERVER_ADDRESS_PREF).equals("") && isConnected;
    }
}
