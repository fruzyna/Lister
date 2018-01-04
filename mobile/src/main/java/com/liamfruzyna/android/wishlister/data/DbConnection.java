package com.liamfruzyna.android.wishlister.data;

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
		System.out.println("Initing client");
		client = new OkHttpClient();
	}

	public static Object runQuery(String queryExt)
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

			System.out.println("Got result!");

			List<Map<String, Object>> rows = new ArrayList<>();
			try
			{
				JSONArray json = new JSONArray(result);
				for(int i = 0; i < json.length(); i++)
				{
					JSONObject jrow = json.getJSONObject(i);
					Map<String, Object> map = new HashMap<>();
					Iterator<String> it = jrow.keys();
					while(it.hasNext())
					{
						String key = it.next();
						Object value = jrow.get(key);
						map.put(key, value);
					}
					rows.add(map);
				}
				System.out.println("Returning rows");
				return rows;
			}
			catch(JSONException e)
			{
				System.out.println("Invalid JSON, returning String");
				return result;
			}
		}
		catch(IOException e)
		{
			System.out.println("Could not complete request");
			e.printStackTrace();
		}


		return "Network Failure";
	}
}
