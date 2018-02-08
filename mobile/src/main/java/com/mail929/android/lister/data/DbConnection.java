package com.mail929.android.lister.data;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by mail929 on 12/21/17.
 */

public class DbConnection
{
	private static final String baseUrl = "http://lister.mail929.com/api/";

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
            QueryProcessor.processQuery(queryExt);
		}

		return "Network Failure";
	}

	public static String encode(String str)
	{
		try
		{
			return URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return str;
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
		respond(queryAndParse("edititem/?id=" + item.getId() + "&text=" + encode(item.getItem())));
	}

	public static void addItem(String item, ListObj list)
	{
		respond(queryAndParse("additem/?lid=" + list.getId() + "&text=" + encode(item)));
	}

	public static void createList(String name, int days, boolean auto)
	{
		respond(queryAndParse("createlist/?name=" + encode(name) + "&daysToDel=" + days + "&auto=" + auto));
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
		respond(queryAndParse("sharelist/?lid=" + list.getId() + "&level=" + permLevel + "&user=" + encode(user)));
	}

	public static void leaveList(ListObj list)
	{
		respond(queryAndParse("leavelist/?lid=" + list.getId()));
	}

	public static void resetList(ListObj list)
	{
		respond(queryAndParse("resetlist/?lid=" + list.getId()));
	}

	public static void deleteDone(ListObj list)
	{
		respond(queryAndParse("deletedone/?lid=" + list.getId()));
	}

	public static void setDateFormat(boolean monthFirst)
	{
		respond(queryAndParse("setdateformat/?monthfirst=" + monthFirst));
	}

	public static void addConstraint(int lid, String data)
	{
		boolean exclude = false;
		if(data.charAt(0) == '!')
		{
			exclude = true;
			data = data.substring(1);
		}
		String parts[] = data.split(":");
		respond(queryAndParse("addConstraint/?lid=" + lid + "&exclude=" + exclude + "&ctype=" + parts[0] + "&cdata=" + parts[1]));
	}

	public static void listSettings(int lid, String name, int days, boolean sortDone, boolean sortDate, boolean showDone)
	{
		respond(queryAndParse("listsettings/?lid=" + lid + "&name=" + encode(name) + "&daysToDel=" + days + "&sortDone=" + sortDone + "&sortDate=" + sortDate + "&showDone=" + showDone));
	}

	public static boolean changePass(String newPass, String oldPass)
	{
		if(respond(queryAndParse("changepass/?new=" + encode(newPass) + "&old=" + encode(oldPass))) == 1)
        {
            IO.getInstance().put(IO.SERVER_PASS_PREF, newPass);
            return true;
        }
        return false;
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
			    IO.wipeFiles();
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

		if(response == 4)
        {
            queryCache();
        }

		Data.sortLists(ListSorts.OLDEST_FIRST);
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
		String query = "login/?user=" + encode(user) + "&pass=" + encode(pass);
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
		String query = "createuser/?user=" + encode(user) + "&pass=" + encode(pass);
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