package com.liamfruzyna.android.lister.Fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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

/**
 * Created by mail929 on 2/19/16.
 */
public class WLFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener, View.OnLongClickListener
{
    private View view;
    private static SharedPreferences.Editor editor;

    static LinearLayout list;
    public static Context c;
    WLFragment f = this;
    static LinearLayout tagcv;
    static RelativeLayout criteria;
    static TextView autotv;
    static Spinner spin;
    Button editCriteria;
    ImageButton removelist;
    ImageButton archiveList;
    CheckBox showDone;
    View addItem;
    Button tagEdit;
    public static FloatingActionButton fab;

    public int edit = -1;
    public boolean editTags = false;

    boolean firstRun = true;
    boolean firstSelect = true;

    //rebuilds the list of items
    public void updateList()
    {
        if (Data.getUnArchived().size() > 0)
        {
            if (Data.getCurrent() >= Data.getNames().size())
            {
                Data.setCurrent(Data.getNames().size() - 1);
                spin.setSelection(Data.getCurrent());
            }

            final WishList wl = Data.getCurrentList();

            showDone.setChecked(wl.showDone);
            criteria.removeAllViews();
            if (wl.auto)
            {
                ((AutoList) wl).findItems();
                criteria.addView(Views.createCriteria(getActivity()));
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
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            for (int i = 0; i < Data.getItems().size(); i++)
            {
                if (!Data.getItems().get(i).done || wl.showDone)
                {
                    if (edit == i)
                    {
                        list.addView(Views.createEditItem(getActivity(), inflater, i, list, this));
                    } else
                    {
                        list.addView(Views.createItem(getActivity(), i, list, this));
                    }
                }
            }

            //populates the tags
            tagcv.removeAllViews();
            inflater = LayoutInflater.from(getActivity());
            View view;
            final Activity a = getActivity();
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
                        Data.getCurrentList().tags = new ArrayList<>(Arrays.asList(editText.getText().toString().split(" ")));
                        View view = a.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                        updateList();
                        IO.getInstance().saveList();
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
                        View view = a.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                        updateList();
                    }
                });
            } else
            {
                view = inflater.inflate(R.layout.tags_list_item, tagcv, false);
                ((HorizontalScrollView) view.findViewById(R.id.tagscroll)).removeAllViews();
                ((HorizontalScrollView) view.findViewById(R.id.tagscroll)).addView(Views.createTags(getActivity(), this));
                tagEdit = (Button) view.findViewById(R.id.editTag);
                tagEdit.setOnClickListener(f);
            }
            tagcv.addView(view);
            System.out.println("refreshing");
        }
    }

    @Override
    public View onCreateView(LayoutInflater infl, ViewGroup parent, Bundle savedInstanceState)
    {
        view = infl.inflate(R.layout.fragment_wl, parent, false);

        //setup shared preferences
        editor = IO.getInstance().getPrefs().edit();

        c = getActivity();

        //set title
        getActivity().setTitle("Lister");

        //init spinner
        spin = (Spinner) view.findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);

        //make fab visible and style
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_white));
        fab.setRippleColor(getResources().getColor(R.color.fab));

        firstRun = false;

        //init other view components
        removelist = (ImageButton) view.findViewById(R.id.remove);
        tagcv = (LinearLayout) view.findViewById(R.id.tagsContainer);
        criteria = (RelativeLayout) view.findViewById(R.id.criterion);
        list = (LinearLayout) view.findViewById(R.id.list);
        autotv = (TextView) view.findViewById(R.id.auto);
        editCriteria = (Button) view.findViewById(R.id.editCriteria);
        archiveList = (ImageButton) view.findViewById(R.id.archive);
        showDone = (CheckBox) view.findViewById(R.id.showDone);

        //check if there are unarchived lists
        if (Data.getUnArchived().size() > 0)
        {
            setupSpinner();
        } else
        {
            //if there are no lists prompt to make a new one
            DialogFragment dialog = new NewListDialog();
            dialog.show(getFragmentManager(), "");
        }

        //setup spinner
        spin.setSelection(IO.getInstance().getInt(IO.CURRENT_LIST_PREF));
        Data.setCurrent(spin.getSelectedItemPosition());

        //sets listeners
        fab.setOnClickListener(f);
        fab.setOnLongClickListener(f);
        tagcv.setOnLongClickListener(f);
        criteria.setOnLongClickListener(f);
        editCriteria.setOnClickListener(f);
        removelist.setOnClickListener(f);
        archiveList.setOnClickListener(f);
        showDone.setOnClickListener(f);

        //setup button to create a new item
        LinearLayout container = (LinearLayout) view.findViewById(R.id.newitem);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        addItem = inflater.inflate(R.layout.button_add_item, container, false);
        addItem.setOnClickListener(f);
        addItem.setOnLongClickListener(f);
        container.addView(addItem);

        //put shit on the screen
        updateList();
        return view;
    }

    //repopulates the spinner
    public void setupSpinner()
    {
        //creates a list of events level and distance to fill out the spinner
        Data.setNames(Util.sortLists(Data.getUnArchived()));
        System.out.println("Names: " + Data.getNames());

        //sets up adapter
        ArrayAdapter<String> sadapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, Data.getNames());
        sadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(sadapter);
        Data.setCurrent(spin.getSelectedItemPosition());

        spin.setOnLongClickListener(f);

        updateList();
    }

    //sets the Data.getCurrent() list to the last open list
    public static void openNewest()
    {
        //creates a list of events level and distance to fill out the spinner
        Data.setNames(Util.sortLists(Data.getUnArchived()));

        //sets up adapter
        ArrayAdapter<String> sadapter = new ArrayAdapter<>(c, R.layout.spinner_item, Data.getNames());
        sadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(sadapter);
        spin.setSelection(Data.getNames().size() - 1);
        Data.setCurrent(spin.getSelectedItemPosition());

        editor.putInt(IO.CURRENT_LIST_PREF, Data.getCurrent());
        editor.commit();

        getFrag((WLActivity) c).updateList();
    }

    //updates list when new list is selected in spinner
    @Override
    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
    {
        if(firstSelect)
        {
            firstSelect = false;
        }
        else
        {
            edit = -1;
            editTags = false;
            Data.setCurrent(spin.getSelectedItemPosition());
            editor.putInt(IO.CURRENT_LIST_PREF, Data.getCurrent());
            editor.commit();
            if(Data.getCurrentList().auto)
            {
                ((AutoList) Data.getCurrentList()).findItems();
            }
            updateList();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent){}

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

    @Override
    public void onClick(View v)
    {
        if (v.equals(fab))
        {
            DialogFragment dialog = new NewListDialog();
            dialog.show(getFragmentManager(), "");
        } else if (v.equals(editCriteria))
        {
            if (Data.getListFromName(Data.getNames().get(Data.getCurrent())).auto && Data.getUnArchived().size() > 0)
            {
                DialogFragment dialog = new EditCriteriaDialog();
                dialog.show(getFragmentManager(), "");
            }
        } else if (v.equals(editCriteria))
        {
            if (Data.getUnArchived().size() > 0)
            {
                DialogFragment dialog = new RemoveListDialog();
                dialog.show(getFragmentManager(), "");
            }
        } else if (v.equals(removelist))
        {
            if (Data.getUnArchived().size() > 0)
            {
                DialogFragment dialog = new RemoveListDialog();
                dialog.show(getFragmentManager(), "");
            }
        } else if (v.equals(archiveList))
        {
            if (Data.getUnArchived().size() > 0)
            {
                DialogFragment dialog = new ArchiveListDialog();
                dialog.show(getFragmentManager(), "");
            }
        } else if (v.equals(addItem))
        {
            if (Data.getUnArchived().size() > 0)
            {
                Item newItem = new Item("", false);
                Data.getCurrentList().items.add(newItem);
                updateList();
                edit = Data.getItems().indexOf(newItem);
                updateList();
            }
        } else if (v.equals(showDone))
        {
            Data.getCurrentList().showDone = ((CheckBox) v).isChecked();
            updateList();
            IO.getInstance().saveList();
        } else if (v.equals(tagEdit))
        {
            if (Data.getUnArchived().size() > 0)
            {
                editTags = true;
                updateList();
            }
        }
    }

    @Override
    public boolean onLongClick(View v)
    {
        if (v.equals(fab))
        {
            Toast.makeText(v.getContext(), "New Item", Toast.LENGTH_SHORT).show();
            return true;
        } else if (v.equals(tagcv))
        {
            if (Data.getUnArchived().size() > 0)
            {
                editTags = true;
                updateList();
            }
            return false;
        } else if (v.equals(criteria))
        {
            if (Data.getListFromName(Data.getNames().get(Data.getCurrent())).auto && Data.getUnArchived().size() > 0)
            {
                DialogFragment dialog = new EditCriteriaDialog();
                dialog.show(getFragmentManager(), "");
            }
            return false;
        } else if (v.equals(addItem))
        {
            if (Data.getUnArchived().size() > 0)
            {
                DialogFragment dialog = new ClearListDialog();
                dialog.show(getFragmentManager(), "");
            }
            return false;
        } else if (v.equals(spin))
        {
            DialogFragment dialog = new EditListNameDialog();
            dialog.show(getFragmentManager(), "");
            return false;
        }
        return false;
    }
}