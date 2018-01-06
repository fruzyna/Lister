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

            if(DbConnection.loginStatus())
            {
                onLogin(c[0]);
            }
            else if(!IO.getInstance().getString(IO.SERVER_USER_PREF).equals(""))
            {
                String result = DbConnection.login(IO.getInstance().getString(IO.SERVER_USER_PREF), IO.getInstance().getString(IO.SERVER_PASS_PREF));

                if(result.equals("Successful Login") || result.equals("Network Error"))
                {
                    onLogin(c[0]);
                }
                else
                {
                    Toast.makeText(c[0], result, Toast.LENGTH_SHORT).show();
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

    public void onLogin(Context c)
    {
        System.out.println("Syncing");
        DbConnection.pullLists();

        System.out.println("Starting with " + Data.getLists().size() + " list(s)");
        System.out.println("Confirming starting with " + Data.getNames().size() + " list(s)");

        Intent intent = new Intent(c, ListerActivity.class);
        startActivity(intent);
        finish();
    }
}
