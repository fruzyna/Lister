package com.liamfruzyna.android.lister.Data;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;

import com.liamfruzyna.android.lister.Fragments.WLFragment;
import com.liamfruzyna.android.lister.WLActivity;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPHTTPClient;
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
import java.net.MalformedURLException;
import java.net.UnknownHostException;
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
    public static final String SAVE_REMOTE_PREF = "SAVE_REMOTE_PREF";
    public static final String SERVER_ADDRESS_PREF = "SERVER_ADDRESS_PREF";
    public static final String SERVER_USER_PREF = "SERVER_USER_PREF";
    public static final String SERVER_PASSWORD_PREF = "SERVER_PASSWORD_PREF";
    public static final String SERVER_DIR_PREF = "SERVER_DIR_PREF";

    public static final String fileDir = Environment.getExternalStoragePublicDirectory("Lists").toString();

    //writes list datas to files for each list
    public static void save()
    {
        try
        {
            for (WishList list : Data.getLists())
            {
                if(WLActivity.settings.getBoolean(SAVE_REMOTE_PREF, false))
                {
                    new RemoteWriteTask().execute(list.name + "%" + getListString(list));
                }
                else
                {
                    writeToFile(list.name, getListString(list));
                }
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
        jlist.put("showDone", list.showDone);
        jlist.put("daysToDelete", list.daysToDelete);
        if (list.auto)
        {
            AutoList alist = (AutoList) list;
            JSONArray jcriteria = new JSONArray();
            List<String> criteria = alist.criteria;
            for (String c : criteria)
            {
                jcriteria.put(c);
            }
            jlist.put("criteria", jcriteria);
        } else
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

    public static void finishLoad(List<String> jlists)
    {
        List<WishList> lists = new ArrayList<>();
        for (String jliststr : jlists)
        {
            try
            {
                System.out.println("Reading: " + jliststr);
                JSONObject jlist = new JSONObject(jliststr);
                List<Item> items = new ArrayList<>();
                List<String> criteria = new ArrayList<>();
                List<String> tags = new ArrayList<>();
                JSONArray jtags = jlist.getJSONArray("tags");
                for (int j = 0; j < jtags.length(); j++)
                {
                    tags.add(jtags.getString(j));
                }
                boolean archived = false;
                if (jlist.has("archived"))
                {
                    archived = jlist.getBoolean("archived");
                }
                int order = 0;
                if (jlist.has("order"))
                {
                    order = jlist.getInt("order");
                }
                int daysToDelete = 0;
                if (jlist.has("daysToDelete"))
                {
                    daysToDelete = jlist.getInt("daysToDelete");
                }
                boolean showDone = true;
                if (jlist.has("showDone"))
                {
                    showDone = jlist.getBoolean("showDone");
                }
                if (jlist.has("auto"))
                {
                    boolean auto = jlist.getBoolean("auto");
                    if (auto)
                    {
                        JSONArray jcriteria = jlist.getJSONArray("criteria");
                        for (int j = 0; j < jcriteria.length(); j++)
                        {
                            criteria.add((String) jcriteria.get(j));
                        }
                        lists.add(new AutoList(jlist.getString("name"), tags, archived, order, criteria, showDone, daysToDelete));
                    } else
                    {
                        JSONArray jitems = jlist.getJSONArray("items");
                        for (int j = 0; j < jitems.length(); j++)
                        {
                            Item item = new Item(jitems.getJSONObject(j).getString("item"), jitems.getJSONObject(j).getBoolean("done"));
                            items.add(item);
                        }
                        lists.add(new WishList(jlist.getString("name"), items, tags, archived, order, showDone, daysToDelete));
                    }
                } else
                {
                    JSONArray jitems = jlist.getJSONArray("items");
                    for (int j = 0; j < jitems.length(); j++)
                    {
                        Item item = new Item(jitems.getJSONObject(j).getString("item"), jitems.getJSONObject(j).getBoolean("done"));
                        items.add(item);
                    }
                    lists.add(new WishList(jlist.getString("name"), items, tags, archived, order, showDone, daysToDelete));
                }
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
        boolean auto = jlist.getBoolean("auto");
        List<String> tags = new ArrayList<>();
        JSONArray jtags = jlist.getJSONArray("tags");
        for (int j = 0; j < jtags.length(); j++)
        {
            tags.add(jtags.getString(j));
        }
        boolean archived = false;
        if (jlist.has("archived"))
        {
            archived = jlist.getBoolean("archived");
        }
        int order = 0;
        if (jlist.has("order"))
        {
            order = jlist.getInt("order");
        }
        int daysToDelete = 0;
        if (jlist.has("daysToDelete"))
        {
            daysToDelete = jlist.getInt("daysToDelete");
        }
        boolean showDone = true;
        if (jlist.has("showDone"))
        {
            showDone = jlist.getBoolean("showDone");
        }
        if (auto)
        {
            JSONArray jcriteria = jlist.getJSONArray("criteria");
            for (int j = 0; j < jcriteria.length(); j++)
            {
                criteria.add((String) jcriteria.get(j));
            }
            return new AutoList(jlist.getString("name"), tags, archived, order, criteria, showDone, daysToDelete);
        } else
        {
            JSONArray jitems = jlist.getJSONArray("items");
            for (int j = 0; j < jitems.length(); j++)
            {
                Item item = new Item(jitems.getJSONObject(j).getString("item"), jitems.getJSONObject(j).getBoolean("done"));
                items.add(item);
            }
            return new WishList(jlist.getString("name"), items, tags, archived, order, showDone, daysToDelete);
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

    public static List<String> readFromRemoteFile() throws IOException
    {
        if(WLActivity.settings.getBoolean(SAVE_REMOTE_PREF, false))
        {
            FTPClient client = new FTPClient();
            client.connect(InetAddress.getByName(WLActivity.settings.getString(SERVER_ADDRESS_PREF, "none")));
            client.enterLocalPassiveMode();
            client.login(WLActivity.settings.getString(SERVER_USER_PREF, "none"), WLActivity.settings.getString(SERVER_PASSWORD_PREF, "none"));
            client.changeWorkingDirectory(WLActivity.settings.getString(SERVER_DIR_PREF, "Liam/Lists"));
            client.setFileType(FTP.ASCII_FILE_TYPE);
            FTPFile[] files = client.listFiles();
            if (files == null)
            {
                files = new FTPFile[0];
            }
            for (int i = 0; i < files.length; i++)
            {
                if (!files[i].getName().equals(".") && !files[i].getName().equals(".."))
                {
                    File newFile = new File(fileDir, files[i].getName());
                    log("IO", "Reading from " + newFile.toString());
                    if (!newFile.exists())
                    {
                        newFile.createNewFile();
                    }
                    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(newFile));
                    client.retrieveFile(files[i].getName(), out);
                    out.close();
                }
            }
            client.logout();
            client.disconnect();
        }
        return readFromFile();
    }

    public static void writeToRemoteFile(String name, String data) throws IOException
    {
        FTPClient client = new FTPClient();
        client.connect(InetAddress.getByName(WLActivity.settings.getString(SERVER_ADDRESS_PREF, "none")));
        client.enterLocalPassiveMode();
        client.login(WLActivity.settings.getString(SERVER_USER_PREF, "none"), WLActivity.settings.getString(SERVER_PASSWORD_PREF, "none"));
        client.changeWorkingDirectory(WLActivity.settings.getString(SERVER_DIR_PREF, "Liam/Lists"));
        client.setFileType(FTP.ASCII_FILE_TYPE);
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(writeToFile(name, data)));
        client.storeFile(name + ".json", in);
        in.close();
        client.logout();
        client.disconnect();
    }

    public static void log(String title, String message)
    {
        System.out.println("[" + title + "] " + message);
    }

    static class RemoteWriteTask extends AsyncTask<String, Void, String>
    {

        protected String doInBackground(String... data)
        {
            String[] datas = data[0].split("%");
            try
            {
                writeToRemoteFile(datas[0], datas[1]);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            return "";
        }

        protected void onPostExecute()
        {
        }
    }
}
