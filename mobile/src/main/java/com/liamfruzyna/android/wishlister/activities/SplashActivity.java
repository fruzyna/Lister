package com.liamfruzyna.android.wishlister.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.liamfruzyna.android.wishlister.data.Data;
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
        protected Activity doInBackground(Activity... c) {
            while(ContextCompat.checkSelfPermission(c[0], android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(c[0], new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }

            if(IO.getInstance().getBoolean(IO.FIRST_PREF, true))
            {
                System.out.println("First Launch");
                Intent intent = new Intent(c[0], LoginActivity.class);
                startActivity(intent);
                finish();
            }
            else
            {
                if(IO.getInstance().checkNetwork())
                {
                    System.out.println("Syncing");
                    IO.getInstance().syncOffThread();
                }
                else
                {
                    System.out.println("Reading");
                    IO.getInstance().loadFromFile();
                }

                if(Data.getLists().size() == 0)
                {
                    List<Item> items = new ArrayList<>();
                    for(String s : c[0].getResources().getStringArray(R.array.welcome))
                    {
                        items.add(new Item(s));
                    }
                    ListObj list = new ListObj("Welcome List", items, new ArrayList<String>());
                    Data.replaceList(list);
                    IO.getInstance().saveAndSync();
                }

                System.out.println("Starting with " + Data.getLists().size() + " list(s)");
                System.out.println("Confirming starting with " + Data.getNames().size() + " list(s)");
                System.out.println("Current list is " + Data.getCurrent());

                Intent intent = new Intent(c[0], ListerActivity.class);
                startActivity(intent);
                finish();
            }

            return c[0];
        }

        protected void onProgressUpdate(Integer... progress) {}

        protected void onPostExecute(Activity c) {}
    }
}
