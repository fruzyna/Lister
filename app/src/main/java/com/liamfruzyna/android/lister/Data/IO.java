package com.liamfruzyna.android.lister.Data;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;

import com.liamfruzyna.android.lister.WLActivity;

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

import static com.liamfruzyna.android.lister.Fragments.WLFragment.getFrag;

/**
 * Created by mail929 on 11/6/14.
 */
public class IO
{
    public static final String PREFS = "Lister Prefs";
    public static final String HIGHLIGHT_DATE_PREF = "HIGHLIGHT_DATE_PREF";
    public static final String CURRENT_LIST_PREF = "CURRENT_LIST_PREF";
    public static final String SERVER_PREF = "SERVER_PREF";
    public static final String SERVER_ADDRESS_PREF = "SERVER_ADDRESS_PREF";
    public static final String SERVER_USER_PREF = "SERVER_USER_PREF";
    public static final String SERVER_PASSWORD_PREF = "SERVER_PASSWORD_PREF";
    public static final String TIME_PREF = "TIME_PREF";
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

    public static IO instance;
    public static boolean ready = false;

    private Activity c;
    private SharedPreferences prefs;

    /**
     * Used to establish IO and it's SharedPreferences
     * @param c Main activity used to init SharedPreferenes
     * @return Current instance of IO
     */
    public static IO firstInstance(Activity c)
    {
        if(instance == null)
        {
            instance = new IO(c);
        }
        return instance;
    }

    public static IO getInstance()
    {
        if(instance == null)
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

    public IO() {
        System.out.println("Warning creating IO w/o context");
    }

    //writes list datas to files for each list
    public void save()
    {
        boolean sync = getBoolean(SERVER_PREF, false);
        try
        {
            for (WishList list : Data.getLists())
            {
                String data = getListString(list);
                System.out.println(list.name + ": " + data);
                writeToFile(list.name, data);
                if(sync)
                {
                    new UploadListTask().execute(data, prefs.getString(SERVER_ADDRESS_PREF, ""), prefs.getString(SERVER_USER_PREF, ""),  prefs.getString(SERVER_PASSWORD_PREF, ""), list.name);
                }
            }
            SharedPreferences.Editor edit = prefs.edit();
            edit.putLong(TIME_PREF, System.currentTimeMillis());
            edit.commit();
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void saveList()
    {
        WishList list = Data.getCurrentList();
        try
        {
            if (list.auto)
            {
                save();
            }
            else
            {
                String data = getListString(list);
                System.out.println(list.name + ": " + data);
                writeToFile(list.name, data);
                if(getBoolean(SERVER_PREF, false))
                {
                    new UploadListTask().execute(data, prefs.getString(SERVER_ADDRESS_PREF, ""), prefs.getString(SERVER_USER_PREF, ""),  prefs.getString(SERVER_PASSWORD_PREF, ""), list.name);
                }
            }
            SharedPreferences.Editor edit = prefs.edit();
            edit.putLong(TIME_PREF, System.currentTimeMillis());
            edit.commit();
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public JSONObject getListObject(WishList list) throws JSONException {
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
        return jlist;
    }

    //creates a json string based off of a list
    public String getListString(WishList list) throws JSONException
    {
        return getListObject(list).toString();
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

    public void finishLoad(List<String> jlists)
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
        if(getBoolean(SERVER_PREF, false))
        {
            new SyncListsTask().execute(prefs.getString(SERVER_ADDRESS_PREF, ""), prefs.getString(SERVER_USER_PREF, ""),  prefs.getString(SERVER_PASSWORD_PREF, ""));
        }
        else {
            ready = true;
        }
        Data.setLists(lists);
    }

    //creates a single list from a json string
    public WishList readString(String json) throws JSONException
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

    public static void log(String title, String message)
    {
        System.out.println("[" + title + "] " + message);
    }

    public static void log(String clas, String method, String message)
    {
        System.out.println("[" + clas + ":" + method + "()] " + message);
    }

    public void deleteList(String name)
    {
        new DeleteListTask().execute(prefs.getString(SERVER_ADDRESS_PREF, ""), prefs.getString(SERVER_USER_PREF, ""),  prefs.getString(SERVER_PASSWORD_PREF, ""), name);
        File file = new File(fileDir, name + ".json");
        file.delete();
    }

    private class UploadListTask extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... urls) {
            try {
                String data = urls[0].replace("#", "[^]");
                String urlString = "http://" + urls[1] + "/sync/?user=" + urls[2] + "&password=" + urls[3] + "&list=" + urls[4] + "&data=" + data;
                return webRequest(urlString);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "ERROR";
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(String result) {
            System.out.println(result);
        }
    }

    private class DeleteListTask extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... urls) {
            try {
                String urlString = "http://" + urls[0] + "/remove/?user=" + urls[1] + "&password=" + urls[2] + "&list=" + urls[3];
                return webRequest(urlString);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "ERROR";
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(String result) {
            System.out.println(result);
        }
    }

    private class SyncListsTask extends AsyncTask<String, Integer, ArrayList<WishList>> {
        protected ArrayList<WishList> doInBackground(String... urls) {
            try {
                String urlString = "http://" + urls[0] + "/getLists/?user=" + urls[1] + "&password=" + urls[2];
                String result = webRequest(urlString);
                if(result != null) {
                    ArrayList<WishList> list = new ArrayList<>();
                    System.out.println("Avail Lists: " + result);
                    JSONArray lists = new JSONArray(result);
                            for (int i = 0; i < lists.length(); i++) {
                                try {
                                    JSONObject jlist = lists.getJSONObject(i);
                                    if (Long.parseLong(jlist.getString("time")) > getLong(TIME_PREF)) {
                                        urlString = "http://" + urls[0] + "/get/?user=" + urls[1] + "&password=" + urls[2] + "&list=" + jlist.getString("name");
                                        String output = webRequest(urlString);
                                        WishList wl = readString(output);
                                        list.add(wl);
                                        Data.replaceList(wl);
                                    }
                                    else {
                                        System.out.println("local newer");
                                    }
                                }
                                catch(Exception e) {
                                    e.printStackTrace();
                                }

                            }
                    ready = true;
                    return list;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(ArrayList<WishList> result) {
            if(result != null)
            {
                if(result.size() > 0)
                {
                    try
                    {
                        ready = true;
                        for (WishList list : Data.getLists())
                        {
                            writeToFile(list.name, getListString(list));
                        }
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            else
            {
                System.out.println("Error fetching remote lists");
            }
            ready = true;
        }
    }

    public String webRequest(String urlString) throws IOException
    {
        System.out.println(urlString);
        URL url = new URL(urlString);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
            byte[] contents = new byte[1024];

            int bytesRead = 0;
            String result = "";
            while ((bytesRead = in.read(contents)) != -1) {
                result += new String(contents, 0, bytesRead);
            }
            in.close();
            return result;

        } finally {
            urlConnection.disconnect();
        }
    }

    public SharedPreferences getPrefs()
    {
        return prefs;
    }

    public String getString(String key) { return prefs.getString(key, ""); }
    public int getInt(String key) { return prefs.getInt(key, 0); }
    public long getLong(String key) { return prefs.getLong(key, 0); }
    public boolean getBoolean(String key, boolean temp) { return prefs.getBoolean(key, temp); }
}
