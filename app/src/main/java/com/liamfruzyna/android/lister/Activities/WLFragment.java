package com.liamfruzyna.android.lister.Activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.liamfruzyna.android.lister.Data.AutoList;
import com.liamfruzyna.android.lister.Data.IO;
import com.liamfruzyna.android.lister.Data.Item;
import com.liamfruzyna.android.lister.Data.Util;
import com.liamfruzyna.android.lister.Data.WishList;
import com.liamfruzyna.android.lister.DialogFragments.ArchiveListDialog;
import com.liamfruzyna.android.lister.DialogFragments.ClearListDialog;
import com.liamfruzyna.android.lister.DialogFragments.EditCriteriaDialog;
import com.liamfruzyna.android.lister.DialogFragments.EditItemDialog;
import com.liamfruzyna.android.lister.DialogFragments.EditListNameDialog;
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

/**
 * Created by mail929 on 2/19/16.
 */
public class WLFragment extends Fragment implements AdapterView.OnItemSelectedListener
{
    private View view;
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
    static RelativeLayout criteria;
    static TextView autotv;
    static Spinner spin;
    public static FloatingActionButton fab;

    //returns the list currently viewable on screen
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

    //returns the items currently displayed on screen
    public static List<Item> getItems()
    {
        return items;
    }

    //returns all the lists
    public static List<WishList> getLists()
    {
        return lists;
    }

    //returns all the unarchived lists
    public static List<WishList> getUnArchived()
    {
        return unArchived;
    }

    //creates the item view that is displayed on screen
    public View createItem(LayoutInflater inflater, final int i)
    {
        View view = inflater.inflate(R.layout.item, list, false);

        //init checkbox and set text, checked status, and color
        final CheckBox cb = (CheckBox) view.findViewById(R.id.checkbox);
        int color = Color.parseColor(items.get(i).color);

        //color item text based off date (late is red, day of is orange)
        SharedPreferences settings = getActivity().getSharedPreferences(IO.PREFS, 0);
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

        SpannableStringBuilder s = Util.colorTags(items.get(i).item, color);
        cb.setText(s);
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
                IO.save(WLFragment.lists);

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

    //creates the textview with a lists criteria
    public TextView createCriteria()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Criteria:");
        for(String c : ((AutoList) getListFromName(names.get(current))).getCriteria())
        {
            sb.append("\n" + c);
        }
        TextView tv = new TextView(c);
        tv.setText(sb.toString());
        return tv;
    }

    //takes the name of a list and returns the list object
    public WishList getListFromName(String name)
    {
        for(WishList list : getUnArchived())
        {
            if(list.name.equals(name))
            {
                return list;
            }
        }
        IO.log("WLActivity:getListFromName", "Returning null list");
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

            if(current >= names.size())
            {
                current = names.size() - 1;
                spin.setSelection(current);
            }

            final WishList wl = getListFromName(names.get(current));

            CheckBox showDone = (CheckBox) view.findViewById(R.id.showDone);
            showDone.setChecked(wl.showDone);
            showDone.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    wl.showDone = ((CheckBox) v).isChecked();
                    updateList();
                    IO.save(lists);
                }
            });
            criteria.removeAllViews();
            if(wl.auto)
            {
                wl.items = ((AutoList) wl).findItems();
                criteria.addView(createCriteria());
                autotv.setText("Auto");
                view.findViewById(R.id.newitem).setVisibility(View.GONE);
                view.findViewById(R.id.criteria).setVisibility(View.VISIBLE);
            }
            else
            {
                autotv.setText("");
                view.findViewById(R.id.newitem).setVisibility(View.VISIBLE);
                view.findViewById(R.id.criteria).setVisibility(View.GONE);
            }
            //reorganizes all the items by date then doneness
            items = Util.sortByDone(Util.sortByPriority(Util.sortByDate(Util.newList(wl.items))));

            //populates the list with the items
            list.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(c);
            for (int i = 0; i < items.size(); i++)
            {
                if(items.get(i).item.equals(("")))
                {
                    items.remove(i);
                }
                else if(!items.get(i).done || wl.showDone)
                {
                    list.addView(createItem(inflater, i));
                }
            }

            //populates the tags
            tagcv.removeAllViews();
            tagcv.addView(createTags());
        }
    }


    @Override
    public View onCreateView(LayoutInflater infl, ViewGroup parent, Bundle savedInstanceState) {
        view = infl.inflate(R.layout.fragment_wl, parent, false);

        c = getActivity();

        getActivity().setTitle("Lister");

        prefs = getActivity().getSharedPreferences(IO.PREFS, 0);
        editor = prefs.edit();

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
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        tagcv = (RelativeLayout) view.findViewById(R.id.tag);
        criteria = (RelativeLayout) view.findViewById(R.id.criterion);
        list = (LinearLayout) view.findViewById(R.id.list);
        autotv = (TextView) view.findViewById(R.id.auto);

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

        Button editTag = (Button) view.findViewById(R.id.editTag);
        editTag.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (unArchived.size() > 0)
                {
                    DialogFragment dialog = new EditTagsDialog();
                    dialog.show(getFragmentManager(), "");
                }
            }
        });

        //sets listener for editing criteria
        criteria.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                if (getListFromName(names.get(current)).auto && unArchived.size() > 0)
                {
                    DialogFragment dialog = new EditCriteriaDialog();
                    dialog.show(getFragmentManager(), "");
                }
                return false;
            }
        });

        Button editCriteria = (Button) view.findViewById(R.id.editCriteria);
        editCriteria.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (getListFromName(names.get(current)).auto && unArchived.size() > 0)
                {
                    DialogFragment dialog = new EditCriteriaDialog();
                    dialog.show(getFragmentManager(), "");
                }
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
        spin = (Spinner) view.findViewById(R.id.spinner);
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
        ImageButton removelist = (ImageButton) view.findViewById(R.id.remove);
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
        ImageButton archiveList = (ImageButton) view.findViewById(R.id.archive);
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
        LinearLayout container = (LinearLayout) view.findViewById(R.id.newitem);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view2 = inflater.inflate(R.layout.add_item, container, false);
        view2.setOnClickListener(new View.OnClickListener()
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
        view2.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                if (unArchived.size() > 0)
                {
                    DialogFragment dialog = new ClearListDialog();
                    dialog.show(getFragmentManager(), "");
                }
                return false;
            }
        });
        container.addView(view2);

        updateList();
        return view;
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
    public void setupSpinner()
    {
        //creates a list of events level and distance to fill out the spinner
        names = sortLists(unArchived);

        //sets up adapter
        ArrayAdapter<String> sadapter = new ArrayAdapter<>(c, R.layout.spinner_item, names);
        sadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(sadapter);
        current = spin.getSelectedItemPosition();

        spin.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                DialogFragment dialog = new EditListNameDialog();
                dialog.show(getFragmentManager(), "");
                return false;
            }
        });

        getFrag((WLActivity) c).updateList();
    }

    //sets the current list to the last open list
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

        getFrag((WLActivity)c).updateList();
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
        spin = (Spinner) view.findViewById(R.id.spinner);
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

    //creates snackbar when list is removed
    public void removeListSnackbar(final WishList list)
    {
        final Context c = getActivity();

        Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), "\"" + list.name + "\" Deleted", Snackbar.LENGTH_LONG)
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
        final Context c = getActivity();

        Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), "\"" + item.item + "\" Deleted", Snackbar.LENGTH_LONG)
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

    public static WLFragment getFrag(Activity c)
    {
        return ((WLFragment) c.getFragmentManager().findFragmentByTag("WL"));
    }
}
