package com.liamfruzyna.android.lister.Activities;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.sax.Element;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import com.liamfruzyna.android.lister.DialogFragments.EditTagsDialog;
import com.liamfruzyna.android.lister.DialogFragments.NewItemDialog;
import com.liamfruzyna.android.lister.DialogFragments.NewListDialog;
import com.liamfruzyna.android.lister.DialogFragments.RemoveListDialog;
import com.liamfruzyna.android.lister.Views.Fab;
import com.liamfruzyna.android.lister.R;

import org.json.JSONException;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class WLActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener
{

    public static List<WishList> lists = new ArrayList<WishList>();
    public static List<Item> items = new ArrayList<Item>();
    public static int current = 0;

    static LinearLayout list;
    public static Context c;
    static RelativeLayout tagcv;
    static Spinner spin;
    public static Fab fab;

    //rebuilds the list of items
    public static void updateList()
    {
        if(lists.size() > 0)
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
                cb.setTextColor(Color.parseColor(items.get(i).color));
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
    }

    //main method that is run when app is started
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wl);
        c = this;
        DataContainer.dir = getExternalStoragePublicDirectory("Lists").toString();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        fab = (Fab) findViewById(R.id.fab);
        tagcv = (RelativeLayout) findViewById(R.id.tag);
        list = (LinearLayout) findViewById(R.id.list);

        //sets listener for editing tags
        tagcv.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                if (lists.size() > 0)
                {
                    DialogFragment dialog = new EditTagsDialog();
                    dialog.show(getFragmentManager(), "");
                }
                return false;
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
                    fab.hideFab();
                    DialogFragment dialog = new NewListDialog();
                    dialog.show(getFragmentManager(), "");
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
            fab.hideFab();
            DialogFragment dialog = new NewListDialog();
            dialog.show(getFragmentManager(), "");
        }

        spin.setOnItemSelectedListener(this);

        ImageButton removelist = (ImageButton) findViewById(R.id.remove);
        removelist.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (lists.size() > 0)
                {
                    DialogFragment dialog = new RemoveListDialog();
                    dialog.show(getFragmentManager(), "");
                }
            }
        });

        LinearLayout container = (LinearLayout) findViewById(R.id.newitem);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.add_item, container, false);
        view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (lists.size() > 0)
                {
                    DialogFragment dialog = new NewItemDialog();
                    dialog.show(getFragmentManager(), "");
                    fab.hideFab();
                }
            }
        });
        container.addView(view);
        updateList();
    }

    public void onResume()
    {
        super.onResume();
        if(lists.size() > 0 && lists != null)
        {
            updateList();
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.wl, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_settings)
        {
            Intent goSettings = new Intent(this, SettingsActivity.class);
            this.startActivity(goSettings);
            return true;
        }
        else if (id == R.id.action_tags)
        {
            DataContainer.lists = lists;
            Intent goTags = new Intent(this, TagsActivity.class);
            this.startActivity(goTags);
            return true;
        }
        else if (id == R.id.action_people)
        {
            DataContainer.lists = lists;
            Intent goTags = new Intent(this, PeopleActivity.class);
            this.startActivity(goTags);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}