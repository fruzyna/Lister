package com.liamfruzyna.android.lister.Activities;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import com.liamfruzyna.android.lister.Data.IO;
import com.liamfruzyna.android.lister.Data.Item;
import com.liamfruzyna.android.lister.Data.WishList;
import com.liamfruzyna.android.lister.DialogFragments.ArchiveListDialog;
import com.liamfruzyna.android.lister.DialogFragments.EditItemDialog;
import com.liamfruzyna.android.lister.DialogFragments.EditTagsDialog;
import com.liamfruzyna.android.lister.DialogFragments.NewItemDialog;
import com.liamfruzyna.android.lister.DialogFragments.NewListDialog;
import com.liamfruzyna.android.lister.DialogFragments.RemoveListDialog;
import com.liamfruzyna.android.lister.R;

import org.json.JSONException;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WLActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener
{

    private static List<WishList> lists = new ArrayList<>();
    private static List<Item> items = new ArrayList<>();
    private static List<WishList> unArchived = new ArrayList<>();
    private static int current = 0;

    static LinearLayout list;
    public static Context c;
    static RelativeLayout tagcv;
    static Spinner spin;
    public static FloatingActionButton fab;

    public static WishList getCurrentList()
    {
        return unArchived.get(current);
    }

    public static List<Item> getItems()
    {
        return items;
    }

    public static List<WishList> getLists()
    {
        return lists;
    }

    public static List<WishList> getUnArchived()
    {
        return unArchived;
    }

    public Item findEarliest(List<Item> items)
    {
        Item earliest = items.get(0);
        if(items.size() > 1)
        {
            for(int i = 1; i < items.size(); i++)
            {
                if(items.get(i).date.before(earliest.date))
                {
                    earliest = items.get(i);
                }
            }
        }
        return earliest;
    }

    public List<Item> newList(List<Item> items)
    {
        List<Item> copy = new ArrayList<>();
        for(int i = 0; i < items.size(); i++)
        {
            copy.add(items.get(i));
        }
        return copy;
    }

    public List<Item> sortByDate(List<Item> todo)
    {
        List<Item> build = new ArrayList<>();
        while(todo.size() > 0)
        {
            Item item = findEarliest(todo);
            build.add(item);
            todo.remove(item);
        }
        return build;
    }

    public List<Item> sortByDone(List<Item> temp)
    {
        List<Item> items = new ArrayList<>();

        for (int i = 0; i < temp.size(); i++)
        {
            if(!temp.get(i).done)
            {
                items.add(temp.get(i));
            }
        }
        for (int i = 0; i < temp.size(); i++)
        {
            if(temp.get(i).done)
            {
                items.add(temp.get(i));
            }
        }
        return items;
    }

    public View createItem(LayoutInflater inflater, final int i)
    {
        View view = inflater.inflate(R.layout.item, list, false);

        //init checkbox and set text, checked status, and color
        final CheckBox cb = (CheckBox) view.findViewById(R.id.checkbox);
        cb.setText(items.get(i).item);
        cb.setTextColor(Color.parseColor(items.get(i).color));
        cb.setChecked(items.get(i).done);

        //color item text based off date (late is red, day of it orange)
        Date date = items.get(i).date;
        Date today = Calendar.getInstance().getTime();
        int compare = date.compareTo(today);
        if(date.getYear() == today.getYear() && date.getMonth() == today.getMonth() && date.getDate() == today.getDate() && !items.get(i).done)
        {
            cb.setTextColor(Color.parseColor("#FFA500"));
        }
        else if(date.compareTo(today) < 0 && !items.get(i).done)
        {
            cb.setTextColor(Color.RED);
        }

        //if item is done cross it out
        if(items.get(i).done)
        {
            cb.setPaintFlags(cb.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else
        {
            cb.setPaintFlags(0);
        }

        //listen for checkbox to be checked
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                items.get(i).done = cb.isChecked();
                //if it is checked cross it out
                if (cb.isChecked()) {
                    cb.setPaintFlags(cb.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    cb.setPaintFlags(0);
                }
                IO.save(WLActivity.lists);

            }
        });

        //listen for item to be long pressed
        cb.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                //when it is open up the dialog to edit it
                DialogFragment dialog = new EditItemDialog();
                Bundle args = new Bundle();
                args.putInt("position", i);
                dialog.setArguments(args);
                dialog.show(getFragmentManager(), "");
                return true;
            }
        });
        return view;
    }

    //creates the textview with a lists tags
    public TextView createTags()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Tags: ");
        for(int i = 0; i < unArchived.get(current).tags.size(); i++)
        {
            sb.append(unArchived.get(current).tags.get(i) + " ");
        }
        TextView tv = new TextView(c);
        tv.setText(sb.toString());
        return tv;
    }

    //rebuilds the list of items
    public void updateList()
    {
        if(unArchived.size() > 0)
        {
            //reorganizes all the items by date then doneness
            items = sortByDone(sortByDate(newList(unArchived.get(current).items)));

            //populates the list with the items
            list.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(c);
            for (int i = 0; i < items.size(); i++)
            {
                if(items.get(i).item.equals(("")))
                {
                    items.remove(i);
                }
                else
                {
                    list.addView(createItem(inflater, i));
                }
            }

            //populates the tags
            tagcv.removeAllViews();
            tagcv.addView(createTags());

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

        //setup the toolbar
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
        fab = (FloatingActionButton) findViewById(R.id.fab);
        tagcv = (RelativeLayout) findViewById(R.id.tag);
        list = (LinearLayout) findViewById(R.id.list);

        //sets listener for editing tags
        tagcv.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                if (unArchived.size() > 0)
                {
                    DialogFragment dialog = new EditTagsDialog();
                    dialog.show(getFragmentManager(), "");
                }
                return false;
            }
        });

        //set up fab (Floating Action Button)
        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_white));
        fab.setRippleColor(getResources().getColor(R.color.fab));
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(unArchived.size() > 0)
                {
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
            lists = new ArrayList<>();
        }

        unArchived = populateUnArchived();

        //sets up spinner
        spin = (Spinner) findViewById(R.id.spinner);
        if (unArchived.size() > 0)
        {
            setupSpinner();
        }
        else
        {
            //if there are no lists prompt to make a new one
            DialogFragment dialog = new NewListDialog();
            dialog.show(getFragmentManager(), "");
        }
        spin.setOnItemSelectedListener(this);

        //setup button to delete current list
        ImageButton removelist = (ImageButton) findViewById(R.id.remove);
        removelist.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (unArchived.size() > 0)
                {
                    DialogFragment dialog = new RemoveListDialog();
                    dialog.show(getFragmentManager(), "");
                }
            }
        });

        //setup button to archive list
        ImageButton archiveList = (ImageButton) findViewById(R.id.archive);
        archiveList.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(unArchived.size() > 0)
                {
                    DialogFragment dialog = new ArchiveListDialog();
                    dialog.show(getFragmentManager(), "");
                }
            }
        });

        //setup button to create a new item
        LinearLayout container = (LinearLayout) findViewById(R.id.newitem);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.add_item, container, false);
        view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (unArchived.size() > 0)
                {
                    DialogFragment dialog = new NewItemDialog();
                    dialog.show(getFragmentManager(), "");
                }
            }
        });
        container.addView(view);

        updateList();
    }

    //repopulates the spinner
    public static void setupSpinner()
    {
        //creates a list of events level and distance to fill out the spinner
        List<String> names = new ArrayList<String>();
        for (int i = 0; i < unArchived.size(); i++)
        {
            names.add(unArchived.get(i).name);
        }

        //sets up adapter
        ArrayAdapter<String> sadapter = new ArrayAdapter<>(c, R.layout.spinner_item, names);
        sadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(sadapter);
        current = spin.getSelectedItemPosition();

        ((WLActivity)c).updateList();
    }

    //finds all the unAchived lists and groups them
    public List<WishList> populateUnArchived()
    {
        List<WishList> unArchived = new ArrayList<>();
        for(int i = 0; i < lists.size(); i++)
        {
            if(!lists.get(i).archived)
            {
                unArchived.add(lists.get(i));
            }
        }
        return unArchived;
    }

    //method that is run when app is resumed
    @Override
    public void onResume()
    {
        super.onResume();

        //repopulate the list if there are lists to populate it with
        if(unArchived.size() > 0 && unArchived != null)
        {
            updateList();
        }

        //re-sets up spinner
        spin = (Spinner) findViewById(R.id.spinner);
        if (unArchived.size() > 0)
        {
            setupSpinner();
        } else
        {
            DialogFragment dialog = new NewListDialog();
            dialog.show(getFragmentManager(), "");
        }
        spin.setOnItemSelectedListener(this);
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

    //inflates the option menu when the menu button is selected
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.wl, menu);
        return true;
    }

    //listens for options in menu to be pressed
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
            Intent goTags = new Intent(this, TagsActivity.class);
            this.startActivity(goTags);
            return true;
        }
        else if (id == R.id.action_people)
        {
            Intent goTags = new Intent(this, PeopleActivity.class);
            this.startActivity(goTags);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //creates snackbar when list is removed
    public void removeListSnackbar(final WishList list)
    {
        final Context c = this;

        Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "\"" + list.name + "\" Deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        //undoes the removal when undo is chosen
                        unArchived.add(list);
                        lists.add(list);
                        IO.save(lists);
                        setupSpinner();
                    }
                })
                .show();
    }

    //creates snackbar when item is removed
    public void removeItemSnackbar(final Item item)
    {
        final Context c = this;

        Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "\"" + item.item + "\" Deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        //undoes the removal when undo is chosen
                        lists.get(current).items.add(item);
                        unArchived.get(current).items.add(item);
                        IO.save(lists);
                        updateList();
                    }
                })
                .show();
    }
}