package com.liamfruzyna.android.wishlister.data;

import android.content.Intent;
import android.widget.Toast;

import com.liamfruzyna.android.wishlister.activities.SplashActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by mail929 on 12/21/17.
 */

public class DbConnection
{
	private static final String baseUrl = "http://192.168.1.25:8080/api/";

	private static OkHttpClient client;
	private static Headers headers;

	private static void init()
	{
		client = new OkHttpClient();
	}

	private static String runQuery(String queryExt)
	{
		String url = baseUrl + queryExt;

		if(client == null)
		{
			init();
		}

		Request.Builder builder = new Request.Builder().url(url);
		if(headers != null)
		{
			builder = builder.addHeader("Cookie", headers.get("Set-Cookie"));
		}
		Request request = builder.get().build();

		System.out.println("Making query: " + url);

		try
		{
			Response response = client.newCall(request).execute();
			String result = response.body().string();

			if(headers == null)
			{
				headers = response.headers();
			}

			return result;
		}
		catch(IOException e)
		{
			System.out.println("Could not complete request");
			e.printStackTrace();
		}
		
		return "Network Failure";
	}
	
	private static Object queryAndParse(String queryExt)
	{
		return IO.parseJSON(runQuery(queryExt));
	}

	public static void checkItem(Item item)
	{
		Object result = queryAndParse("checkitem/?id=" + item.getId() + "&done=" + item.isDone());
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

	public static void removeItem(Item item)
	{
		Object result = queryAndParse("deleteitem/?id=" + item.getId());
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

	public static void editItem(Item item)
	{
		Object result = queryAndParse("edititem/?id=" + item.getId() + "&text=" + item.getItem());
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

	public static void addItem(String item, ListObj list)
	{
		Object result = queryAndParse("additem/?lid=" + list.getId() + "&text=" + item);
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

	public static void createList(String name, int days, boolean auto)
	{
		Object result = queryAndParse("createlist/?name=" + name + "&daysToDel=" + days + "&auto=" + auto);
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

	public static void deleteList(ListObj list)
	{
		Object result = queryAndParse("deletelist/?lid=" + list.getId());
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

	public static void archiveList(ListObj list)
	{
		Object result = queryAndParse("archivelist/?lid=" + list.getId() + "&archived=" + list.isArchived());
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

	public static void shareList(ListObj list, String user, char permLevel)
	{
		Object result = queryAndParse("sharelist/?lid=" + list.getId() + "&level=" + permLevel + "&user=" + user);
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

	public static void leaveList(ListObj list)
	{
		Object result = queryAndParse("leavelist/?lid=" + list.getId());
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


	public static void pullLists()
	{
		String resultStr = DbConnection.runQuery("getuserlists");
		Object result = IO.parseJSON(resultStr);
		if(result instanceof String)
		{
			String response = (String) result;
			if(response.equals("Network Failure"))
			{
				System.out.println("Failed to connect to server");
				result = IO.parseJSON(IO.retrieveData("Lists"));
			}
			else if(response.equals("Not logged in!"))
			{
				System.out.println("Not logged in");
				return;
			}
			else
			{
				System.out.println("Unknown result: " + response);
				return;
			}
		}
		else
		{
			IO.storeData("Lists", resultStr);
		}

		IO.parseLists(result);

		for(ListObj list : Data.getLists())
		{
			pullList(list.getId());
		}
	}

	public static void pullList(int lid)
	{
		String resultStr = DbConnection.runQuery("getlistitems/?lid=" + lid);
		Object result = IO.parseJSON(resultStr);
		if(result instanceof String)
		{
			String response = (String) result;
			if(response.equals("Network Failure"))
			{
				System.out.println("Failed to connect to server");
				result = IO.parseJSON(IO.retrieveData(Integer.toString(lid)));
			}
			else if(response.equals("Not logged in!"))
			{
				System.out.println("Not logged in");
				return;
			}
			else
			{
				System.out.println("Unknown result: " + response);
				return;
			}
		}
		else
		{
			IO.storeData(Integer.toString(lid), resultStr);
		}

		IO.parseListItems(lid, result);
	}

	public static String login(String user, String pass)
	{
		String query = "login/?user=" + user + "&pass=" + pass;
		Object result = queryAndParse(query);

		if(result instanceof String)
		{
			String response = (String) result;
			if(response.equals("Network Failure"))
			{
				System.out.println("Failed to connect to server");
				return "Network Error";
			}
			else if(response.equals("Success"))
			{
				System.out.println("Successful Login");

				IO.getInstance().put(IO.SERVER_USER_PREF, user);
				IO.getInstance().put(IO.SERVER_PASS_PREF, pass);

				return "Successful Login";
			}
			else if(response.equals("Failure"))
			{
				return "Incorrect Login";
			}
			else
			{
				System.out.println("Unknown result : " + response);
				return "Unknown Error";
			}
		}
		else
		{
			List<Map<String, Object>> data = (List<Map<String, Object>>) result;
			System.out.println("Table of length " + data.size() + " returned");
			return "Unknown Error";
		}
	}

	public static String create(String user, String pass)
	{
		String query = "createuser/?user=" + user + "&pass=" + pass;
		Object result = queryAndParse(query);

		if(result instanceof String)
		{
			String response = (String) result;
			if(response.equals("Network Failure"))
			{
				System.out.println("Failed to connect to server");
				return "Network Error";
			}
			else if(response.equals("Success"))
			{
				System.out.println("Successful Login");

				IO.getInstance().put(IO.SERVER_USER_PREF, user);
				IO.getInstance().put(IO.SERVER_PASS_PREF, pass);

				return "Successful Login";
			}
			else if(response.equals("Failure"))
			{
				return "Incorrect Login";
			}
			else
			{
				System.out.println("Unknown result : " + response);
				return "Unknown Error";
			}
		}
		else
		{
			List<Map<String, Object>> data = (List<Map<String, Object>>) result;
			System.out.println("Table of length " + data.size() + " returned");
			return "Unknown Error";
		}
	}

	public static boolean loginStatus()
	{
		Object result = queryAndParse("");
		if(result instanceof String)
		{
			String response = (String) result;
			if(response.equals("Network Failure"))
			{
				System.out.println("Failed to connect to server");
				return true;
			}
			else if(response.equals("Not logged in!"))
			{
				System.out.println("Not logged in");
			}
			else if(response.contains("Lister API v2, Logged in as: "))
			{
				return true;
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
		}
		return false;
	}
}
