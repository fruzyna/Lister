package com.liamfruzyna.android.lister.Activities;

import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.liamfruzyna.android.lister.Data.DataContainer;
import com.liamfruzyna.android.lister.Data.IO;
import com.liamfruzyna.android.lister.Data.Item;
import com.liamfruzyna.android.lister.Data.WishList;
import com.liamfruzyna.android.lister.DialogFragments.NewItemDialog;
import com.liamfruzyna.android.lister.DialogFragments.NewListDialog;
import com.liamfruzyna.android.lister.DialogFragments.RemoveListDialog;
import com.liamfruzyna.android.lister.Views.Fab;
import com.liamfruzyna.android.lister.R;

import org.json.JSONException;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WLActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener
{

    public static List<WishList> lists = new ArrayList<WishList>();
    public static List<Item> items = new ArrayList<Item>();
    public static int current = 0;

    static LinearLayout list;
    static Context c;
    static RelativeLayout tagcv;
    static Spinner spin;
    public static Fab fab;

    //finds all the different tags there are
    public List<String> getTags()
    {
        List<String> tags = new ArrayList<String>();
        for(int i = 0; i < lists.size(); i++)
        {
            for(int j = 0; j < lists.get(i).tags.size(); j++)
            {
                boolean found = false;
                for(int l = 0; l < tags.size(); l++)
                {
                    if(tags.get(l).equals(lists.get(i).tags.get(j)))
                    {
                        found = true;
                    }
                }
                if(!found)
                {
                    tags.add(lists.get(i).tags.get(j));
                }
            }
        }
        return tags;
    }

    //rebuilds the list of items
    public static void updateList()
    {
        List<Item> temp = lists.get(current).items;
        items = new ArrayList<Item>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < temp.size(); i++)
        {
            if(!temp.get(i).done)
            {
                if(DataContainer.showArchived)
                {
                    items.add(temp.get(i));
                }
                else if(!temp.get(i).archived)
                {
                    items.add(temp.get(i));
                }
            }
        }
        for (int i = 0; i < temp.size(); i++)
        {
            if(temp.get(i).done)
            {
                if(DataContainer.showArchived)
                {
                    items.add(temp.get(i));
                }
                else if(!temp.get(i).archived)
                {
                    items.add(temp.get(i));
                }
            }
        }
        list.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(c);
        for (int i = 0; i < items.size(); i++)
        {
            final int j = i;
            View view = inflater.inflate(R.layout.item, list, false);
            //init checkbox and set text
            final CheckBox cb = (CheckBox) view.findViewById(R.id.checkbox);
            cb.setText(items.get(i).item);
            cb.setChecked(items.get(i).done);
            if(items.get(i).done)
            {
                cb.setPaintFlags(cb.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else
            {
                cb.setPaintFlags(0);
            }

            if(items.get(i).archived)
            {
                cb.setTextColor(Color.parseColor("#808080"));
            }

            cb.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(!items.get(j).archived)
                    {
                        items.get(j).done = cb.isChecked();
                        if (cb.isChecked())
                        {
                            cb.setPaintFlags(cb.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        } else
                        {
                            cb.setPaintFlags(0);
                        }
                        IO.save(WLActivity.lists);
                    }
                }
            });
            cb.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    if(!items.get(j).archived)
                    {
                        items.get(j).archived = true;
                        WLActivity.lists.get(WLActivity.current).items = items;
                        if(DataContainer.showArchived)
                        {
                            cb.setTextColor(Color.parseColor("#808080"));
                        }
                        else
                        {
                            cb.setTextColor(Color.parseColor("#FFFFFF"));
                        }
                        IO.save(WLActivity.lists);
                    }
                    else
                    {
                        items.get(j).archived = false;
                        WLActivity.lists.get(WLActivity.current).items = items;
                        cb.setTextColor(Color.parseColor("#000000"));
                        IO.save(WLActivity.lists);
                    }
                    return false;
                }
            });
            list.addView(view);
        }
        sb.append("Tags: ");
        for(int i = 0; i < lists.get(current).tags.size(); i++)
        {
            sb.append(lists.get(current).tags.get(i) + " ");
        }
        TextView tv = new TextView(c);
        tv.setText(sb.toString());
        tagcv.removeAllViews();
        tagcv.addView(tv);
        IO.save(lists);
    }

    //main method that is run when app is started
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wl);
        c = this;
        DataContainer.dir = getFilesDir().toString();

        try
        {
            lists = IO.load();
        } catch (JSONException e)
        {
            e.printStackTrace();
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        }

        //init view widgets
        ImageButton settings = (ImageButton) findViewById(R.id.settings);
        fab = (Fab) findViewById(R.id.fab);
        tagcv = (RelativeLayout) findViewById(R.id.tag);
        list = (LinearLayout) findViewById(R.id.list);

        settings.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent goSettings = new Intent(WLActivity.this, SettingsActivity.class);
                WLActivity.this.startActivity(goSettings);
            }
        });

        //set up fab (Floating Action Button)
        fab.setFabDrawable(getResources().getDrawable(R.drawable.ic_add_white));
        fab.setFabColor(getResources().getColor(R.color.fab));
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(lists.size() > 0)
                {
                    DialogFragment dialog = new NewItemDialog();
                    dialog.show(getFragmentManager(), "");
                    fab.hideFab();
                }
            }
        });
        fab.setOnLongClickListener(new View.OnLongClickListener()
        {
            public boolean onLongClick(View v)
            {
                Toast.makeText(v.getContext(), "New Item", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        //makes sure that lists isn't null
        if (lists == null)
        {
            lists = new ArrayList<WishList>();
        }

        //sets up spinner
        spin = (Spinner) findViewById(R.id.spinner);
        if (lists.size() > 0)
        {
            //creates a list of events level and distance to fill out the spinner
            List<String> names = new ArrayList<String>();
            for (int i = 0; i < lists.size(); i++)
            {
                names.add(lists.get(i).name);
            }
            //setup spinner
            ArrayAdapter<String> sadapter = new ArrayAdapter<String>(this, R.layout.spinner_item, names);
            sadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spin.setAdapter(sadapter);
            current = spin.getSelectedItemPosition();
            updateList();
        } else
        {
            DialogFragment dialog = new NewListDialog();
            dialog.show(getFragmentManager(), "");
        }

        spin.setOnItemSelectedListener(this);

        ImageButton newlist = (ImageButton) findViewById(R.id.add);
        ImageButton removelist = (ImageButton) findViewById(R.id.remove);
        newlist.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                DialogFragment dialog = new NewListDialog();
                dialog.show(getFragmentManager(), "");
            }
        });
        removelist.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(lists.size() > 0)
                {
                    DialogFragment dialog = new RemoveListDialog();
                    dialog.show(getFragmentManager(), "");
                }
            }
        });

        updateList();
    }

    //creates the notification
    public static void startAlarm(Calendar calendar)
    {
        AlarmManager alarmManager = (AlarmManager) c.getSystemService(c.ALARM_SERVICE);
        long when = calendar.getTimeInMillis();         // notification time
        Intent intent = new Intent(c, WLActivity.class);
        PendingIntent pendingIntent = PendingIntent.getService(c, 0, intent, 0);
        alarmManager.set(AlarmManager.RTC, when, pendingIntent);
    }

    //updates list when new list is selected in spinner
    @Override
    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
    {
        current = spin.getSelectedItemPosition();
        updateList();
    }

    //when nothing is selected in the spinner
    @Override public void onNothingSelected(AdapterView<?> parent){}
}