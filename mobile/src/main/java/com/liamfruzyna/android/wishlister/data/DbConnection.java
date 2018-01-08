package com.liamfruzyna.android.wishlister.data;

import java.io.IOException;

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

	public static String responses[] = {"Other", "Success", "Table", "Not Logged In", "Network Failure"};

	/**
	 * Makes an API request given the end of the URL
	 * @param queryExt End of the url for after baseUrl
	 * @return String sent back from the server
	 */
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

		IO.log("Making query: " + url);

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
			IO.log("Could not complete request");
			e.printStackTrace();
		}

		String root = queryExt.split("/")[0];
		if(!root.equals("login") && !root.equals("getuserlists") && !root.equals("getlistitems"))
		{
			//save for later if not available
			IO.storeData("cache", queryExt + "\n", true);
		}

		return "Network Failure";
	}

	/**
	 * Resets headers if a logout is started
	 */
	public static void resetHeaders()
	{
		headers = null;
	}

	/**
	 * Makes a query then returns the parsed String
	 * @param queryExt End of the url for after baseUrl
	 * @return Parsed String returned from the server
	 */
	private static Object queryAndParse(String queryExt)
	{
		return IO.parseJSON(runQuery(queryExt));
	}

	/**
	 * Goes through basic results from server as returns type as int
	 * @param result Parsed result from server
	 * @return Int value of response
	 */
	public static int respond(Object result)
	{
		int msg;
		if(result instanceof String)
		{
			String response = (String) result;
			if(response.equals("Network Failure"))
			{
				msg = 4;
			}
			else if(response.equals("Not logged in!"))
			{
				msg = 3;
			}
			else if(response.equals("Success"))
			{
				msg = 1;
			}
			else
			{
				msg = 0;
			}
		}
		else
		{
			msg = 2;
		}
		
		IO.log(responses[msg]);
		return msg;
	}

	public static void checkItem(Item item)
	{
		respond(queryAndParse("checkitem/?id=" + item.getId() + "&done=" + item.isDone()));
	}

	public static void removeItem(Item item)
	{
		respond(queryAndParse("deleteitem/?id=" + item.getId()));
	}

	public static void editItem(Item item)
	{
		respond(queryAndParse("edititem/?id=" + item.getId() + "&text=" + item.getItem()));
	}

	public static void addItem(String item, ListObj list)
	{
		respond(queryAndParse("additem/?lid=" + list.getId() + "&text=" + item));
	}

	public static void createList(String name, int days, boolean auto)
	{
		respond(queryAndParse("createlist/?name=" + name + "&daysToDel=" + days + "&auto=" + auto));
	}

	public static void deleteList(ListObj list)
	{
		respond(queryAndParse("deletelist/?lid=" + list.getId()));
	}

	public static void archiveList(ListObj list)
	{
		respond(queryAndParse("archivelist/?lid=" + list.getId() + "&archived=" + list.isArchived()));
	}

	public static void shareList(ListObj list, String user, char permLevel)
	{
		respond(queryAndParse("sharelist/?lid=" + list.getId() + "&level=" + permLevel + "&user=" + user));
	}

	public static void leaveList(ListObj list)
	{
		respond(queryAndParse("leavelist/?lid=" + list.getId()));
	}
	
	public static void pullLists()
	{
		String resultStr = runQuery("getuserlists");
		Object result = IO.parseJSON(resultStr);

		int response = respond(result);
		switch(response)
		{
			case 4:
				result = IO.parseJSON(IO.retrieveData("Lists"));
				break;
			case 2:
				IO.storeData("Lists", resultStr);
				break;
			default:
				return;
		}

		IO.parseLists(result);

		for(ListObj list : Data.getLists())
		{
			pullList(list.getId());
		}
	}

	public static void pullList(int lid)
	{
		String resultStr = runQuery("getlistitems/?lid=" + lid);
		Object result = IO.parseJSON(resultStr);

		int response = respond(result);
		switch(response)
		{
			case 4:
				result = IO.parseJSON(IO.retrieveData(Integer.toString(lid)));
				break;
			case 2:
				IO.storeData(Integer.toString(lid), resultStr);
				break;
			default:
				return;
		}

		IO.parseListItems(lid, result);
	}

	public static int login(String user, String pass)
	{
		String query = "login/?user=" + user + "&pass=" + pass;
		Object result = queryAndParse(query);

		int response = respond(result);
		if(response == 1)
		{
			IO.getInstance().put(IO.SERVER_USER_PREF, user);
			IO.getInstance().put(IO.SERVER_PASS_PREF, pass);
		}

		return response;
	}

	public static int create(String user, String pass)
	{
		String query = "createuser/?user=" + user + "&pass=" + pass;
		Object result = queryAndParse(query);

		int response = respond(result);
		if(response == 1)
		{
			IO.getInstance().put(IO.SERVER_USER_PREF, user);
			IO.getInstance().put(IO.SERVER_PASS_PREF, pass);
		}

		return response;
	}

	public static int loginStatus()
	{
		Object result = queryAndParse("");
		int response = respond(result);
		if(response == 0 && ((String) result).contains("Lister API v2, Logged in as: "))
		{
			return 1;
		}

		return response;
	}

	public static void queryCache()
	{
		String[] exts = IO.retrieveData("cache").split("\n");
		IO.getFile("cache").delete();
		for(String ext : exts)
		{
			runQuery(ext);
		}
	}
}
