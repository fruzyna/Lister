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
import com.liamfruzyna.android.wishlister.R;

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

    private class LoadTask extends AsyncTask<Activity, Integer, Integer>
    {
        Activity c;

        protected Integer doInBackground(Activity... a)
        {
            this.c = a[0];

            while(ContextCompat.checkSelfPermission(c, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(c, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }

            if(DbConnection.loginStatus())
            {
                onLogin(c);
                return -1;
            }
            else if(!IO.getInstance().getString(IO.SERVER_USER_PREF).equals("") && !IO.getInstance().getString(IO.SERVER_PASS_PREF).equals(""))
            {
                int result = DbConnection.login(IO.getInstance().getString(IO.SERVER_USER_PREF), IO.getInstance().getString(IO.SERVER_PASS_PREF));

                if(result == 1 || result == 4)
                {
                    onLogin(c);
                    return result;
                }
            }

            Intent intent = new Intent(c, LoginActivity.class);
            startActivity(intent);
            finish();

            return -1;
        }

        protected void onPostExecute(Integer result)
        {
            switch(result)
            {
                case 1:
                    Toast.makeText(c, "Signed In", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(c, "Offline Mode", Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    break;
                default:
                    Toast.makeText(c, DbConnection.responses[result], Toast.LENGTH_SHORT).show();
            }
        }
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
