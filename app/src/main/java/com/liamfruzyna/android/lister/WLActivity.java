package com.liamfruzyna.android.lister;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.liamfruzyna.android.lister.Data.Data;
import com.liamfruzyna.android.lister.Data.IO;
import com.liamfruzyna.android.lister.Data.Util;
import com.liamfruzyna.android.lister.Data.WishList;
import com.liamfruzyna.android.lister.Fragments.DatesFragment;
import com.liamfruzyna.android.lister.Fragments.PeopleFragment;
import com.liamfruzyna.android.lister.Fragments.SettingsFragment;
import com.liamfruzyna.android.lister.Fragments.TagsFragment;
import com.liamfruzyna.android.lister.Fragments.WLFragment;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class WLActivity extends ActionBarActivity
{
    private String[] drawerTitles = {"Home", "Tag Viewer", "People Viewer", "Date Viewer", "Settings"};
    private DrawerLayout drawer;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;

    private Activity c = this;

    public static SharedPreferences settings;

    private class Open extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            Fragment frag = new Fragment();
            String tag = "";
            switch (Integer.parseInt(params[0]))
            {
                case 0:
                    //Home
                    frag = new WLFragment();
                    tag = "WL";
                    break;
                case 1:
                    //Tag Viewer
                    frag = new TagsFragment();
                    tag = "Tags";
                    break;
                case 2:
                    //People Viewer
                    frag = new PeopleFragment();
                    tag = "People";
                    break;
                case 3:
                    //Date Viewer
                    frag = new DatesFragment();
                    tag = "Dates";
                    break;
            }
            changeFragment(frag, tag);
            return params[0];
        }

        @Override
        protected void onPostExecute(String result)
        {
            drawerList.setItemChecked(Integer.parseInt(result), true);
            drawer.closeDrawer(findViewById(R.id.drawer));
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    //main method that is run when app is started
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wl);

        settings = getSharedPreferences(IO.PREFS, 0);

        IO.finishLoad(IO.readFromFile());

        //makes sure that lists isn't null
        if (Data.getLists() == null)
        {
            Data.setLists(new ArrayList<WishList>());
        }

        Data.setUnArchived(Util.populateUnArchived());

        if (ContextCompat.checkSelfPermission(c, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(c, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else
        {
            changeFragment(new WLFragment(), "WL");
        }

        //setup the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        drawerList.setAdapter(new ArrayAdapter<>(this, R.layout.navdrawer_list_item, drawerTitles));
        // Set the list's click listener
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
            {
                if (position != 4)
                {
                    new Open().execute(Integer.toString(position));
                } else
                {
                    changeFragment(new SettingsFragment(), "Settings");
                    drawerList.setItemChecked(position, true);
                    drawer.closeDrawer(findViewById(R.id.drawer));
                }
            }
        });


        drawerToggle = new ActionBarDrawerToggle(this, drawer, R.drawable.ic_menu_white_24dp, R.string.open, R.string.closed)
        {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view)
            {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };

        // Set the drawer toggle as the DrawerListener
        drawer.setDrawerListener(drawerToggle);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case 0:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    changeFragment(new WLFragment(), "WL");
                } else
                {
                    Toast.makeText(getApplicationContext(), "Fuck you, Lister needs that!", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    public void setTitle(String title)
    {
        getSupportActionBar().setTitle(title);
    }

    public void changeFragment(Fragment fragment, String tag)
    {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(R.anim.abc_slide_in_top, R.anim.abc_fade_out, R.anim.abc_slide_in_bottom, R.anim.abc_fade_out);
        transaction.replace(R.id.container, fragment, tag);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onResume()
    {
        super.onResume();
/*
        if(Data.getLists() == null)
        {
            new RemoteReadTask().execute("");
        }*/
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (drawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}