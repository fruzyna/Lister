package com.liamfruzyna.android.wishlister.data;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mail929 on 1/20/18.
 */

public class QueryProcessor
{
    public static void processQuery(String urlExt)
    {
        String query;
        Map<String, String> params = new HashMap<>();
        if(urlExt.contains("/"))
        {
            query = urlExt.split("/")[0];
            String parts[] = urlExt.substring(urlExt.indexOf("/") + 2).split("&");
            for(String part : parts)
            {
                if(part.contains("="))
                {
                    String pair[] = part.split("=");
                    params.put(pair[0], pair[1]);
                    try
                    {
                        params.put(pair[0], URLDecoder.decode(pair[1], "UTF-8"));
                        IO.log("Pair found: " + pair[0] + ", " + URLDecoder.decode(pair[1], "UTF-8"));
                    } catch (UnsupportedEncodingException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        else
        {
            query = urlExt;
        }

        Item item;
        ListObj list;
        switch(query)
        {
            case "checkitem":
                item = Data.getItem(Integer.parseInt(params.get("id")));
                item.setDone(Boolean.parseBoolean(params.get("done")));
                break;
            case "deleteitem":
                item = Data.getItem(Integer.parseInt(params.get("id")));
                list = Data.getList(item.getParent());
                list.getItems().remove(item);
                break;
            case "edititem":
                item = Data.getItem(Integer.parseInt(params.get("id")));
                item.setItem(params.get("text"));
                break;
            case "additem":
                list = Data.getList(Integer.parseInt(params.get("lid")));
                list.addItem(new Item(params.get("text"), Data.getMaxItem() + 1, list.getId(), false));
                break;
            case "createlist":
                Data.replaceList(new ListObj(params.get("name"), Data.getMaxList() + 1, 'o', false, Integer.parseInt(params.get("daysToDel")), true, true, true));
                break;
            case "deletelist":
                list = Data.getList(Integer.parseInt(params.get("lid")));
                Data.getLists().remove(list);
                break;
            case "archivelist":
                list = Data.getList(Integer.parseInt(params.get("lid")));
                list.setArchived(Boolean.parseBoolean(params.get("archived")));
                break;
            case "sharelist":
                //can't really do this
                break;
            case "leavelist":
                //or this
                break;
            case "resetlist":
                list = Data.getList(Integer.parseInt(params.get("lid")));
                list.resetList();
                break;
            case "deletedone":
                list = Data.getList(Integer.parseInt(params.get("lid")));
                List<Item> items = list.getItems();
                for(Item i : items)
                {
                    if(i.isDone())
                    {
                        items.remove(i);
                    }
                }
                break;
            case "listsettings":
                list = Data.getList(Integer.parseInt(params.get("lid")));
                list.setName(params.get("name"));
                //others are mostly pointless
                list.setDaysToDel(Integer.parseInt(params.get("daysToDel")));
                list.setShowDone(Boolean.parseBoolean(params.get("showDone")));
                list.setSortDate(Boolean.parseBoolean(params.get("sortDate")));
                list.setSortDone(Boolean.parseBoolean(params.get("sortDone")));
                break;
        }
    }
}
