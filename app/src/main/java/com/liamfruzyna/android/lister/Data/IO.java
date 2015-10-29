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
    public static final String fileDir = Environment.getExternalStoragePublicDirectory("Lists").toString();

    //turns app data into a string of json data
    public static void save(List<WishList> lists)
    {
        try
        {
            //JSONArray jlists = new JSONArray();
            for (int i = 0; i < lists.size(); i++)
            {
                JSONObject jlist = new JSONObject();
                jlist.put("name", lists.get(i).name);
                JSONArray jitems = new JSONArray();
                List<Item> items = lists.get(i).items;
                for (int j = 0; j < items.size(); j++)
                {
                    JSONObject jitem = new JSONObject();
                    jitem.put("item", items.get(j).item);
                    jitem.put("done", items.get(j).done);
                    jitems.put(jitem);
                }
                jlist.put("items", jitems);
                JSONArray jtags = new JSONArray();
                List<String> tags = lists.get(i).tags;
                for (int j = 0; j < tags.size(); j++)
                {
                    jtags.put(tags.get(j));
                }
                jlist.put("tags", jtags);
                writeToFile(lists.get(i).name, jlist.toString());
                //jlists.put(jlist);
            }
            //writeToFile(jlists.toString());
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    //converts a string of json data to useable lists
    public static List<WishList> load() throws JSONException, MalformedURLException
    {
        List<WishList> lists = new ArrayList<WishList>();
        //JSONArray jlists = new JSONArray(readFromFile());
        List<String> jlists =  readFromFile();
        for (int i = 0; i < jlists.size(); i++)
        {
            //JSONObject jlist = jlists.getJSONObject(i);
            JSONObject jlist = new JSONObject(jlists.get(i));
            List<Item> items = new ArrayList<Item>();
            JSONArray jitems = jlist.getJSONArray("items");
            for (int j = 0; j < jitems.length(); j++)
            {
                Item item = new Item(jitems.getJSONObject(j).getString("item"), jitems.getJSONObject(j).getBoolean("done"));
                items.add(item);
            }
            List<String> tags = new ArrayList<String>();
            JSONArray jtags = jlist.getJSONArray("tags");
            for (int j = 0; j < jtags.length(); j++)
            {
                tags.add(jtags.getString(j));
            }
            lists.add(new WishList(jlist.getString("name"), items, tags));
        }
        return lists;
    }

    //takes a string of data and writes it to the save file
    private static void writeToFile(String name, String data)
    {
        File dir = new File(fileDir);
        dir.mkdirs();
        File file = new File(fileDir, name + ".json");
        System.out.println("[IO] Writing to " + file.toString());
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

    //gets a string of data from the save file
    public static List<String> readFromFile()
    {
        /*File file = new File(fileDir, "data.json");
        System.out.println("[IO] Reading from " + file.toString());
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
        StringBuilder sb = new StringBuilder();
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null)
            {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return "";*/
        List<String> data = new ArrayList<>();
        File[] files = new File(fileDir).listFiles();
        for(int i = 0; i < files.length; i++)
        {
            File file = files[i];
            System.out.println("[IO] Reading from " + file.toString());
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
}
