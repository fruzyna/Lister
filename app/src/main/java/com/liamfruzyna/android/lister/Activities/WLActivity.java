package com.liamfruzyna.android.lister.Activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.liamfruzyna.android.lister.R;

public class WLActivity extends ActionBarActivity
{
    private String[] drawerTitles = {"Home", "Tag Viewer", "People Viewer", "Date Viewer", "Settings"};
    private DrawerLayout drawer;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private boolean upEnabled = false;

    //main method that is run when app is started
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wl);

        //setup the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        drawerList.setAdapter(new ArrayAdapter<>(this, R.layout.navdrawer_list_item, drawerTitles));
        // Set the list's click listener
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Fragment frag = new Fragment();
                String tag = "";
                switch (position)
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
                    case 4:
                        //Settings
                        frag = new SettingsFragment();
                        tag = "Settings";
                        break;
                }
                changeFragment(frag, tag);

                drawerList.setItemChecked(position, true);
                drawer.closeDrawer(findViewById(R.id.drawer));
            }
        });

        changeFragment(new WLFragment(), "WL");

        drawerToggle = new ActionBarDrawerToggle(this, drawer, R.drawable.ic_launcher, R.string.open, R.string.closed) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };

        // Set the drawer toggle as the DrawerListener
        drawer.setDrawerListener(drawerToggle);
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
        transaction.commit();
    }

    public void setUp(boolean displayUp)
    {
        upEnabled = displayUp;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        drawerToggle.setDrawerIndicatorEnabled(!displayUp);
        drawerToggle.syncState();
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if(!upEnabled)
        {
            if (drawerToggle.onOptionsItemSelected(item))
            {
                return true;
            }
        }
        else if(item.getItemId() == android.R.id.home)
        {
            getSupportFragmentManager().popBackStack();
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }
}