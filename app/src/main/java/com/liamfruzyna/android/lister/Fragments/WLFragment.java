package com.liamfruzyna.android.lister.Fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.liamfruzyna.android.lister.Data.AutoList;
import com.liamfruzyna.android.lister.Data.Data;
import com.liamfruzyna.android.lister.Data.IO;
import com.liamfruzyna.android.lister.Data.Item;
import com.liamfruzyna.android.lister.Data.Util;
import com.liamfruzyna.android.lister.Data.WishList;
import com.liamfruzyna.android.lister.DialogFragments.ArchiveListDialog;
import com.liamfruzyna.android.lister.DialogFragments.ClearListDialog;
import com.liamfruzyna.android.lister.DialogFragments.EditCriteriaDialog;
import com.liamfruzyna.android.lister.DialogFragments.EditListNameDialog;
import com.liamfruzyna.android.lister.DialogFragments.NewListDialog;
import com.liamfruzyna.android.lister.DialogFragments.RemoveListDialog;
import com.liamfruzyna.android.lister.R;
import com.liamfruzyna.android.lister.Views;
import com.liamfruzyna.android.lister.WLActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by mail929 on 2/19/16.
 */
public class WLFragment extends Fragment implements AdapterView.OnItemSelectedListener
{
    private View view;
    private static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;

    static LinearLayout list;
    public static Context c;
    static LinearLayout tagcv;
    static RelativeLayout criteria;
    static TextView autotv;
    static Spinner spin;
    public static FloatingActionButton fab;

    public int edit = -1;
    boolean editTags = false;

    boolean firstRun = true;

    //creates the item view that is displayed on screen
    public View createItem(LayoutInflater inflater, final int i)
    {
        View view = inflater.inflate(R.layout.checkbox_list_item, list, false);

        //init checkbox and set text, checked status, and color
        final CheckBox cb = (CheckBox) view.findViewById(R.id.checkbox);
        int color = Color.parseColor(Data.getItems().get(i).color);

        //color item text based off date (late is red, day of is orange)
        SharedPreferences settings = getActivity().getSharedPreferences(IO.PREFS, 0);
        boolean highlight = settings.getBoolean(IO.HIGHLIGHT_DATE_PREF, true);
        if (highlight)
        {
            Date date = Data.getItems().get(i).date;
            Date today = Calendar.getInstance().getTime();
            if (date.getYear() == today.getYear() && date.getMonth() == today.getMonth() && date.getDate() == today.getDate() && !Data.getItems().get(i).done)
            {
                color = Color.parseColor("#FFA500");
            } else if (date.compareTo(today) < 0 && !Data.getItems().get(i).done)
            {
                color = Color.RED;
            }
        }

        SpannableStringBuilder s = Util.colorTags(Data.getItems().get(i).item, color);
        cb.setText(s);
        cb.setTextColor(color);
        cb.setChecked(Data.getItems().get(i).done);

        //if item is done cross it out
        if (Data.getItems().get(i).done)
        {
            cb.setPaintFlags(cb.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else
        {
            cb.setPaintFlags(0);
        }

        //listen for checkbox to be checked
        cb.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Data.getItems().get(i).done = cb.isChecked();
                //if it is checked cross it out
                if (cb.isChecked())
                {
                    cb.setPaintFlags(cb.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else
                {
                    cb.setPaintFlags(0);
                }
                IO.save();

            }
        });

        //listen for item to be long pressed
        cb.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                //transition to edit checkbox
                edit = i;
                updateList();
                return true;
            }
        });
        return view;
    }


    //rebuilds the list of items
    public void updateList()
    {
        if (Data.getUnArchived().size() > 0)
        {
            spin.setSelection(prefs.getInt(IO.CURRENT_LIST_PREF, 0));
            Data.setCurrent(spin.getSelectedItemPosition());

            IO.log("WLActivity:updateList", "Spinner is at " + spin.getSelectedItemPosition());
            IO.log("WLActivity:updateList", "Set spinner to " + prefs.getInt(IO.CURRENT_LIST_PREF, 0));

            if (Data.getCurrent() >= Data.getNames().size())
            {
                Data.setCurrent(Data.getNames().size() - 1);
                spin.setSelection(Data.getCurrent());
            }

            final WishList wl = Data.getListFromName(Data.getCurrentName());

            CheckBox showDone = (CheckBox) view.findViewById(R.id.showDone);
            showDone.setChecked(wl.showDone);
            showDone.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    wl.showDone = ((CheckBox) v).isChecked();
                    updateList();
                    IO.save();
                }
            });
            criteria.removeAllViews();
            if (wl.auto)
            {
                wl.items = ((AutoList) wl).findItems();
                criteria.addView(Views.createCriteria(c));
                autotv.setText("Auto");
                view.findViewById(R.id.newitem).setVisibility(View.GONE);
                view.findViewById(R.id.criteria).setVisibility(View.VISIBLE);
            } else
            {
                autotv.setText("");
                view.findViewById(R.id.newitem).setVisibility(View.VISIBLE);
                view.findViewById(R.id.criteria).setVisibility(View.GONE);
            }
            //reorganizes all the items by date then doneness
            Data.setItems(Util.sortByDone(Util.sortByPriority(Util.sortByDate(Util.newList(wl.items)))));

            //populates the list with the items
            list.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(c);
            for (int i = 0; i < Data.getItems().size(); i++)
            {
                if (!Data.getItems().get(i).done || wl.showDone)
                {
                    if (edit == i)
                    {
                        list.addView(Views.createEditItem(inflater, i, list, this));
                    } else
                    {
                        list.addView(createItem(inflater, i));
                    }
                }
            }

            //populates the tags
            tagcv.removeAllViews();
            inflater = LayoutInflater.from(c);
            View view;
            if (editTags)
            {
                view = inflater.inflate(R.layout.tags_edit_item, tagcv, false);

                final EditText editText = (EditText) view.findViewById(R.id.tags);
                Button append = (Button) view.findViewById(R.id.append);
                Button cancel = (Button) view.findViewById(R.id.cancel);

                editText.setText(Views.createEditTags());
                append.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        IO.log("EditTagDialog", "Settings " + Data.getCurrentList().name + "'s tags to " + editText.getText().toString());
                        Data.getCurrentList().tags = new ArrayList<>(Arrays.asList(editText.getText().toString().split(" ")));
                        updateList();
                        IO.save();
                        editTags = false;
                        updateList();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        editTags = false;
                        updateList();
                    }
                });
            } else
            {
                view = inflater.inflate(R.layout.tags_list_item, tagcv, false);
                ((RelativeLayout) view.findViewById(R.id.tag)).removeAllViews();
                ((RelativeLayout) view.findViewById(R.id.tag)).addView(Views.createTags(c));
                view.findViewById(R.id.editTag).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (Data.getUnArchived().size() > 0)
                        {
                            editTags = true;
                            updateList();
                        }
                    }
                });
            }
            tagcv.addView(view);
        }
    }


    @Override
    public View onCreateView(LayoutInflater infl, ViewGroup parent, Bundle savedInstanceState)
    {
        view = infl.inflate(R.layout.fragment_wl, parent, false);

        c = getActivity();

        getActivity().setTitle("Lister");

        //init view widgets
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        spin = (Spinner) view.findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);

        //set up fab (Floating Action Button)
        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_white));
        fab.setRippleColor(getResources().getColor(R.color.fab));

        (new Handler()).postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                firstRun = false;
                prefs = getActivity().getSharedPreferences(IO.PREFS, 0);
                editor = prefs.edit();
                tagcv = (LinearLayout) view.findViewById(R.id.tagsContainer);
                criteria = (RelativeLayout) view.findViewById(R.id.criterion);
                list = (LinearLayout) view.findViewById(R.id.list);
                autotv = (TextView) view.findViewById(R.id.auto);

                //sets up spinner
                if (Data.getUnArchived().size() > 0)
                {
                    setupSpinner();
                } else
                {
                    //if there are no lists prompt to make a new one
                    DialogFragment dialog = new NewListDialog();
                    dialog.show(getFragmentManager(), "");
                }
                spin.setSelection(prefs.getInt(IO.CURRENT_LIST_PREF, 0));
                Data.setCurrent(spin.getSelectedItemPosition());
                IO.log("WLActivity:updateList", "Spinner is at " + spin.getSelectedItemPosition());
                IO.log("WLActivity:updateList", "Set spinner to " + prefs.getInt(IO.CURRENT_LIST_PREF, 0));

                fab.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        if (Data.getUnArchived().size() > 0)
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

                //sets listener for editing tags
                tagcv.setOnLongClickListener(new View.OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick(View v)
                    {
                        if (Data.getUnArchived().size() > 0)
                        {
                            editTags = true;
                            updateList();
                        }
                        return false;
                    }
                });

                //sets listener for editing criteria
                criteria.setOnLongClickListener(new View.OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick(View v)
                    {
                        if (Data.getListFromName(Data.getNames().get(Data.getCurrent())).auto && Data.getUnArchived().size() > 0)
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
                        if (Data.getListFromName(Data.getNames().get(Data.getCurrent())).auto && Data.getUnArchived().size() > 0)
                        {
                            DialogFragment dialog = new EditCriteriaDialog();
                            dialog.show(getFragmentManager(), "");
                        }
                    }
                });


                //setup button to delete Data.getCurrent() list
                ImageButton removelist = (ImageButton) view.findViewById(R.id.remove);
                removelist.setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        if (Data.getUnArchived().size() > 0)
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
                        if (Data.getUnArchived().size() > 0)
                        {
                            DialogFragment dialog = new ArchiveListDialog();
                            dialog.show(getFragmentManager(), "");
                        }
                    }
                });

                //setup button to create a new item
                LinearLayout container = (LinearLayout) view.findViewById(R.id.newitem);
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View view2 = inflater.inflate(R.layout.button_add_item, container, false);
                view2.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (Data.getUnArchived().size() > 0)
                        {
                            Item newItem = new Item("", false);
                            Data.getCurrentList().items.add(newItem);
                            updateList();
                            edit = Data.getItems().indexOf(newItem);
                            updateList();
                        }
                    }
                });
                view2.setOnLongClickListener(new View.OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick(View v)
                    {
                        if (Data.getUnArchived().size() > 0)
                        {
                            DialogFragment dialog = new ClearListDialog();
                            dialog.show(getFragmentManager(), "");
                        }
                        return false;
                    }
                });
                container.addView(view2);

                updateList();
            }
        }, 500);
        return view;
    }

    //Takes a list of lists and reorganizes it based off the order variable
    public static List<String> sortLists(List<WishList> lists)
    {
        List<WishList> copy = new ArrayList<>(lists);
        List<String> names = new ArrayList<>();
        List<String> extra = new ArrayList<>();
        while (copy.size() > 0)
        {
            int lowest = Integer.MAX_VALUE;
            int count = 0;
            for (int j = 0; j < copy.size(); j++)
            {
                if (copy.get(j).order != 0)
                {
                    if (copy.get(j).order < lowest)
                    {
                        IO.log("WLActivity:sortLists", "New Lowest " + copy.get(j).name + " with order of " + copy.get(j).order);
                        lowest = copy.get(j).order;
                        count = j;
                    }
                } else
                {
                    extra.add(copy.get(j).name);
                }
            }
            IO.log("WLActivity:sortLists", "Adding " + copy.get(count).name + " with order of " + copy.get(count).order);
            names.add(copy.get(count).name);
            copy.remove(count);
        }
        for (String xtra : extra)
        {
            if (!names.contains(xtra))
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
        Data.setNames(sortLists(Data.getUnArchived()));
        System.out.println("Names: " + Data.getNames());

        //sets up adapter
        ArrayAdapter<String> sadapter = new ArrayAdapter<>(c, R.layout.spinner_item, Data.getNames());
        sadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(sadapter);
        Data.setCurrent(spin.getSelectedItemPosition());

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

        updateList();
    }

    //sets the Data.getCurrent() list to the last open list
    public static void openNewest()
    {
        //creates a list of events level and distance to fill out the spinner
        Data.setNames(sortLists(Data.getUnArchived()));

        //sets up adapter
        ArrayAdapter<String> sadapter = new ArrayAdapter<>(c, R.layout.spinner_item, Data.getNames());
        sadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(sadapter);
        spin.setSelection(Data.getNames().size() - 1);
        Data.setCurrent(spin.getSelectedItemPosition());

        editor.putInt(IO.CURRENT_LIST_PREF, Data.getCurrent());
        IO.log("WLActivity:openNewest", "Saved position of " + Data.getCurrent());
        IO.log("WLActivity:openNewest", "Position is saved as " + prefs.getInt(IO.CURRENT_LIST_PREF, 0));
        editor.commit();

        getFrag((WLActivity) c).updateList();
    }

    //method that is run when app is resumed
    @Override
    public void onResume()
    {
        super.onResume();

        //repopulate the list if there are lists to populate it with
        if (Data.getUnArchived().size() > 0 && Data.getUnArchived() != null)
        {
            updateList();
        }

        //re-sets up spinner
        spin = (Spinner) view.findViewById(R.id.spinner);
        if (Data.getUnArchived().size() > 0)
        {
            setupSpinner();
        } else if(!firstRun)
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
        edit = -1;
        editTags = false;
        Data.setCurrent(spin.getSelectedItemPosition());
        editor.putInt(IO.CURRENT_LIST_PREF, Data.getCurrent());
        IO.log("WLActivity:onItemSelected", "Saved position of " + Data.getCurrent());
        IO.log("WLActivity:onItemSelected", "Position is saved as " + prefs.getInt(IO.CURRENT_LIST_PREF, 0));
        editor.commit();
        updateList();
    }

    //when nothing is selected in the spinner
    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {
    }

    //creates snackbar when list is removed
    public void removeListSnackbar(final WishList list)
    {
        /*Snackbar.make(view.findViewById(R.id.frame), "\"" + list.name + "\" Deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        //undoes the removal when undo is chosen
                        Data.getUnArchived().add(list);
                        lists.add(list);
                        IO.save(lists);
                        setupSpinner();
                    }
                })
                .show();*/
    }

    //creates snackbar when item is removed
    public void removeItemSnackbar(final Item item)
    {
        /*Snackbar.make(view.findViewById(R.id.frame), "\"" + item.item + "\" Deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        //undoes the removal when undo is chosen
                        lists.get(Data.getCurrent()).items.add(item);
                        Data.getUnArchived().get(Data.getCurrent()).items.add(item);
                        IO.save(lists);
                        updateList();
                    }
                })
                .show();*/
    }

    public static WLFragment getFrag(Activity c)
    {
        return ((WLFragment) c.getFragmentManager().findFragmentByTag("WL"));
    }
}