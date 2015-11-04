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

        if(items.get(i).done)
        {
            cb.setPaintFlags(cb.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else
        {
            cb.setPaintFlags(0);
        }

        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                items.get(i).done = cb.isChecked();
                if (cb.isChecked()) {
                    cb.setPaintFlags(cb.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    cb.setPaintFlags(0);
                }
                IO.save(WLActivity.lists);

            }
        });
        cb.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
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
            items = sortByDone(sortByDate(newList(unArchived.get(current).items)));

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

            tagcv.removeAllViews();
            tagcv.addView(createTags());

            IO.save(unArchived);
        }
    }

    //main method that is run when app is started
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wl);
        c = this;

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
        } else
        {
            DialogFragment dialog = new NewListDialog();
            dialog.show(getFragmentManager(), "");
        }

        spin.setOnItemSelectedListener(this);

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

    public static void setupSpinner()
    {
        //creates a list of events level and distance to fill out the spinner
        List<String> names = new ArrayList<String>();
        for (int i = 0; i < unArchived.size(); i++)
        {
            names.add(unArchived.get(i).name);
        }

        //setup spinner
        ArrayAdapter<String> sadapter = new ArrayAdapter<>(c, R.layout.spinner_item, names);
        sadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(sadapter);
        current = spin.getSelectedItemPosition();
        ((WLActivity)c).updateList();
    }

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

    @Override
    public void onResume()
    {
        super.onResume();
        if(unArchived.size() > 0 && unArchived != null)
        {
            updateList();
        }
        //sets up spinner
        spin = (Spinner) findViewById(R.id.spinner);
        if (unArchived.size() > 0)
        {
            //creates a list of events level and distance to fill out the spinner
            List<String> names = new ArrayList<String>();
            for (int i = 0; i < unArchived.size(); i++)
            {
                names.add(unArchived.get(i).name);
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

    public void removeListSnackbar(final WishList list)
    {
        final Context c = this;

        Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "\"" + list.name + "\" Deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        unArchived.add(list);
                        lists.add(list);
                        List<String> names = new ArrayList<String>();
                        for (int i = 0; i < unArchived.size(); i++)
                        {
                            names.add(unArchived.get(i).name);
                        }
                        ArrayAdapter<String> sadapter = new ArrayAdapter<String>(c, R.layout.spinner_item, names);
                        sadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Spinner spin = (Spinner) findViewById(R.id.spinner);
                        spin.setAdapter(sadapter);
                        updateList();
                    }
                })
                .show();
    }

    public void removeItemSnackbar(final Item item)
    {
        final Context c = this;

        Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "\"" + item.item + "\" Deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        lists.get(current).items.add(item);
                        unArchived.get(current).items.add(item);
                        IO.save(lists);
                        updateList();
                    }
                })
                .show();
    }
}