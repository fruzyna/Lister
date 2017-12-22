package com.liamfruzyna.android.wishlister.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by mail929 on 12/21/17.
 */

public class DbConnection
{
	public static final String baseUrl = "localhost:8080/api/";

	public static Object runQuery(String queryExt)
	{
		String url = baseUrl + queryExt;

		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url(url).build();

		try
		{
			Response response = client.newCall(request).execute();
			String result = response.body().string();

			List<Map<String, Object>> rows = new ArrayList<>();
			try
			{
				JSONArray json = new JSONArray(result);
				for(int i = 0; i < json.length(); i++)
				{
					JSONObject jrow = json.getJSONObject(i);
					Map<String, Object> row = new HashMap<>();
					while(jrow.keys().hasNext())
					{
						String key = jrow.keys().next();
						row.put(key, jrow.get(key));
					}
				}
				return rows;
			}
			catch(JSONException e)
			{
				System.out.println("Invalid JSON, assuming String");
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
