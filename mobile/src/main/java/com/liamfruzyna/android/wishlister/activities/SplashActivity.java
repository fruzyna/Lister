package com.liamfruzyna.android.wishlister.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.liamfruzyna.android.wishlister.data.Data;
import com.liamfruzyna.android.wishlister.data.DbConnection;
import com.liamfruzyna.android.wishlister.data.IO;
import com.liamfruzyna.android.wishlister.data.Item;
import com.liamfruzyna.android.wishlister.data.ListObj;
import com.liamfruzyna.android.wishlister.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplashActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if(IO.instance == null)
        {
            IO.firstInstance(this);
        }
        (new LoadTask()).execute(this);
    }

    private class LoadTask extends AsyncTask<Activity, Integer, Activity>
    {
        protected Activity doInBackground(Activity... c)
        {
            while(ContextCompat.checkSelfPermission(c[0], android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(c[0], new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }

            if(checkLogin(c[0]))
            {
                onLogin(c[0]);
            }
            else if(!IO.getInstance().getString(IO.SERVER_USER_PREF).equals(""))
            {
                String query = "login/?user=" + IO.getInstance().getString(IO.SERVER_USER_PREF) + "&pass=" + IO.getInstance().getString(IO.SERVER_PASS_PREF);
                Object result = DbConnection.runQuery(query);

                if(result instanceof String)
                {
                    String response = (String) result;
                    if(response.equals("Network Failure"))
                    {
                        System.out.println("Failed to connect to server");
                        Toast.makeText(c[0], "Network Error", Toast.LENGTH_SHORT).show();
                    }
                    else if(response.equals("Success"))
                    {
                        System.out.println("Successful Login");
                        onLogin(c[0]);
                    }
                    else if(response.equals("Failure"))
                    {
                        Toast.makeText(c[0], "Incorrect Login", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        System.out.println("Unknown result : " + response);
                        Toast.makeText(c[0], "Unknown Error", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    List<Map<String, Object>> data = (List<Map<String, Object>>) result;
                    System.out.println("Table of length " + data.size() + " returned");
                    Toast.makeText(c[0], "Unknown Error", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Intent intent = new Intent(c[0], LoginActivity.class);
                startActivity(intent);
                finish();
            }

            return c[0];
        }

        protected void onProgressUpdate(Integer... progress) {}

        protected void onPostExecute(Activity c) {}
    }

    public boolean checkLogin(Context c)
    {
        Object result = DbConnection.runQuery("");
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
            else if(response.contains("Lister API v2, Logged in as: "))
            {
                //Toast.makeText(c, "Welcome " + response.replace("Lister API v2, Logged in as: ", ""), Toast.LENGTH_SHORT).show();
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

    public void onLogin(Context c)
    {
        System.out.println("Syncing");
        IO.getInstance().pullLists();

        System.out.println("Starting with " + Data.getLists().size() + " list(s)");
        System.out.println("Confirming starting with " + Data.getNames().size() + " list(s)");

        Intent intent = new Intent(c, ListerActivity.class);
        startActivity(intent);
        finish();
    }
}
