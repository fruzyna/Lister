package com.liamfruzyna.android.lister.Activities;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
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
import com.liamfruzyna.android.lister.Data.Util;
import com.liamfruzyna.android.lister.Data.WishList;
import com.liamfruzyna.android.lister.DialogFragments.ArchiveListDialog;
import com.liamfruzyna.android.lister.DialogFragments.ClearListDialog;
import com.liamfruzyna.android.lister.DialogFragments.EditItemDialog;
import com.liamfruzyna.android.lister.DialogFragments.EditTagsDialog;
import com.liamfruzyna.android.lister.DialogFragments.NewItemDialog;
import com.liamfruzyna.android.lister.DialogFragments.NewListDialog;
import com.liamfruzyna.android.lister.DialogFragments.NewPasswordDialog;
import com.liamfruzyna.android.lister.DialogFragments.PasswordDialog;
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
    private static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;
    private static List<String> names;

    static LinearLayout list;
    public static Context c;
    static RelativeLayout tagcv;
    static Spinner spin;
    public static FloatingActionButton fab;

    public static WishList getCurrentList()
    {
        for(int i = 0; i < unArchived.size(); i++)
        {
            if(unArchived.get(i).name.equals(names.get(current)))
            {
                return unArchived.get(i);
            }
        }
        return null;
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

    public SpannableStringBuilder colorTags(String item, int color)
    {
        final SpannableStringBuilder sb = new SpannableStringBuilder(item);
        int alpha = Color.argb(128, Color.red(color), Color.green(color), Color.blue(color));
        String[] words = item.split(" ");
        System.out.print("Building ");
        for(int i = 0; i < words.length; i++)
        {
            if(words[i].charAt(0) == '#')
            {
                SpannableString s = new SpannableString(words[i]);
                s.setSpan(new ForegroundColorSpan(alpha), 0, words[i].length(), 0);
                sb.append(s);
                System.out.print(s);
            }
            else
            {
                sb.append(words[i]);
                System.out.print(words[i]);
            }
            if(i < words.length - 1)
            {
                sb.append(" ");
                System.out.print(" ");
            }
        }
        System.out.println();
        System.out.println("Returning " + sb);
        return sb;
    }

    public View createItem(LayoutInflater inflater, final int i)
    {
        View view = inflater.inflate(R.layout.item, list, false);

        //init checkbox and set text, checked status, and color
        final CheckBox cb = (CheckBox) view.findViewById(R.id.checkbox);
        int color = Color.parseColor(items.get(i).color);

        //color item text based off date (late is red, day of is orange)
        SharedPreferences settings = getSharedPreferences(IO.PREFS, 0);
        boolean highlight = settings.getBoolean(IO.HIGHLIGHT_DATE_PREF, true);
        if(highlight)
        {
            Date date = items.get(i).date;
            Date today = Calendar.getInstance().getTime();
            int compare = date.compareTo(today);
            if(date.getYear() == today.getYear() && date.getMonth() == today.getMonth() && date.getDate() == today.getDate() && !items.get(i).done)
            {
                color = Color.parseColor("#FFA500");
            }
            else if(date.compareTo(today) < 0 && !items.get(i).done)
            {
                color = Color.RED;
            }
        }

        SpannableStringBuilder s = colorTags(items.get(i).item, color);
        //the returned string were being doubled so I cut it in half
        cb.setText(s.subSequence(s.length()/2, s.length()));
        cb.setTextColor(color);
        cb.setChecked(items.get(i).done);

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
        for(String list : getListFromName(names.get(current)).tags)
        {
            sb.append(list + " ");
        }
        TextView tv = new TextView(c);
        tv.setText(sb.toString());
        return tv;
    }

    public WishList getListFromName(String name)
    {
        for(WishList list : getUnArchived())
        {
            if(list.name.equals(name))
            {
                return list;
            }
        }
        return null;
    }

    //rebuilds the list of items
    public void updateList()
    {
        if(unArchived.size() > 0)
        {
            spin.setSelection(prefs.getInt(IO.CURRENT_LIST_PREF, 0));
            current = spin.getSelectedItemPosition();
            IO.log("WLActivity:updateList", "Spinner is at " + spin.getSelectedItemPosition());
            IO.log("WLActivity:updateList", "Set spinner to " + prefs.getInt(IO.CURRENT_LIST_PREF, 0));

            //reorganizes all the items by date then doneness
            items = Util.sortByDone(Util.sortByPriority(Util.sortByDate(Util.newList(getListFromName(names.get(current)).items))));

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

        prefs = getSharedPreferences(IO.PREFS, 0);
        editor = prefs.edit();

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
        spin.setSelection(prefs.getInt(IO.CURRENT_LIST_PREF, 0));
        current = spin.getSelectedItemPosition();
        IO.log("WLActivity:updateList", "Spinner is at " + spin.getSelectedItemPosition());
        IO.log("WLActivity:updateList", "Set spinner to " + prefs.getInt(IO.CURRENT_LIST_PREF, 0));


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
        view.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                if(unArchived.size() > 0)
                {
                    DialogFragment dialog = new ClearListDialog();
                    dialog.show(getFragmentManager(), "");
                }
                return false;
            }
        });
        container.addView(view);

        updateList();
    }

    //Takes a list of lists and reorganizes it based off the order variable
    public static List<String> sortLists(List<WishList> lists)
    {
        List<WishList> copy = new ArrayList<>(lists);
        List<String> names = new ArrayList<>();
        List<String> extra = new ArrayList<>();
        while(copy.size() > 0)
        {
            int lowest = 9999;
            int count = 0;
            for(int j = 0; j < copy.size(); j++)
            {
                if(copy.get(j).order != 0)
                {
                    if(copy.get(j).order < lowest)
                    {
                        IO.log("WLActivity:sortLists", "New Lowest " + copy.get(j).name + " with order of " + copy.get(j).order);
                        lowest = copy.get(j).order;
                        count = j;
                    }
                }
                else
                {
                    extra.add(copy.get(j).name);
                }
            }
            IO.log("WLActivity:sortLists", "Adding " + copy.get(count).name + " with order of " + copy.get(count).order);
            names.add(copy.get(count).name);
            copy.remove(count);
        }
        for(String xtra : extra)
        {
            if(!names.contains(xtra))
            {
                names.add(xtra);
            }
        }

        return names;
    }

    //repopulates the spinner
    public static void setupSpinner()
    {
        //creates a list of events level and distance to fill out the spinner
        names = sortLists(unArchived);

        //sets up adapter
        ArrayAdapter<String> sadapter = new ArrayAdapter<>(c, R.layout.spinner_item, names);
        sadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(sadapter);
        current = spin.getSelectedItemPosition();

        ((WLActivity)c).updateList();
    }

    public static void openNewest()
    {
        //creates a list of events level and distance to fill out the spinner
        names = sortLists(unArchived);

        //sets up adapter
        ArrayAdapter<String> sadapter = new ArrayAdapter<>(c, R.layout.spinner_item, names);
        sadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(sadapter);
        spin.setSelection(names.size() - 1);
        current = spin.getSelectedItemPosition();

        editor.putInt(IO.CURRENT_LIST_PREF, current);
        IO.log("WLActivity:openNewest", "Saved position of " + current);
        IO.log("WLActivity:openNewest", "Position is saved as " + prefs.getInt(IO.CURRENT_LIST_PREF, 0));
        editor.commit();

        ((WLActivity)c).updateList();
    }

    //finds all the unAchived lists and groups them
    public List<WishList> populateUnArchived()
    {
        List<WishList> unArchived = new ArrayList<>();
        for(WishList list : lists)
        {
            if(!list.archived)
            {
                unArchived.add(list);
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
        editor.putInt(IO.CURRENT_LIST_PREF, current);
        IO.log("WLActivity:onItemSelected", "Saved position of " + current);
        IO.log("WLActivity:onItemSelected", "Position is saved as " + prefs.getInt(IO.CURRENT_LIST_PREF, 0));
        editor.commit();
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
        else if (id == R.id.action_dates)
        {
            Intent goTags = new Intent(this, DatesActivity.class);
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