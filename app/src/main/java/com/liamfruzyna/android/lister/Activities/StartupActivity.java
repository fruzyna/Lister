package com.liamfruzyna.android.lister.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.liamfruzyna.android.lister.Data.IO;
import com.liamfruzyna.android.lister.R;

/**
 * Created by mail929 on 12/23/15.
 */
public class StartupActivity extends ActionBarActivity
{
    private static SharedPreferences prefs;

    //main method that is run when app is started
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        prefs = getSharedPreferences(IO.PREFS, 0);

        //setup the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Lister: Enter Password");
        setSupportActionBar(toolbar);

        if (!prefs.getBoolean(IO.HAS_PASSWORD_PREF, false))
        {
            //start app
            Intent goSettings = new Intent(this, WLActivity.class);
            startActivity(goSettings);
        } else
        {
            final EditText et = (EditText) findViewById(R.id.password);
            Button b = (Button) findViewById(R.id.button);
            final Context c = this;
            b.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (et.getText().toString().equals(prefs.getString(IO.PASSWORD_PREF, "dflkjfdjkfgjklg")))
                    {
                        //start app
                        Intent goSettings = new Intent(c, WLActivity.class);
                        c.startActivity(goSettings);
                    } else
                    {
                        //lol you fucked up
                        Toast.makeText(c, "Incorrect password, try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}