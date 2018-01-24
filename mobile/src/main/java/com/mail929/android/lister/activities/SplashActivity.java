package com.mail929.android.lister.activities;

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

import com.mail929.android.lister.data.Data;
import com.mail929.android.lister.data.DbConnection;
import com.mail929.android.lister.data.IO;
import com.mail929.android.lister.R;

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

            if(DbConnection.loginStatus() == 1) //attempt login with cookies
            {
                onLogin(c);
                return 1;
            }
            else if(!IO.getInstance().getString(IO.SERVER_USER_PREF).equals("") && !IO.getInstance().getString(IO.SERVER_PASS_PREF).equals("")) //if there are saved credentials
            {
                //attempt login with saved credentials
                int result = DbConnection.login(IO.getInstance().getString(IO.SERVER_USER_PREF), IO.getInstance().getString(IO.SERVER_PASS_PREF));

                if(result == 1 || result == 4)
                {
                    if(result == 1)
                    {
                        DbConnection.queryCache();
                    }
                    onLogin(c);
                    return result;
                }
            }

            //if we make it this far request login
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
        IO.log("Syncing");
        DbConnection.pullLists();

        IO.log("Starting with " + Data.getLists().size() + " list(s)");
        IO.log("Confirming starting with " + Data.getNames().size() + " list(s)");

        Intent intent = new Intent(c, ListerActivity.class);
        startActivity(intent);
        finish();
    }
}
