package com.liamfruzyna.android.lister.DialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.liamfruzyna.android.lister.Fragments.WLFragment;
import com.liamfruzyna.android.lister.Data.AutoList;
import com.liamfruzyna.android.lister.Data.IO;
import com.liamfruzyna.android.lister.R;
import com.liamfruzyna.android.lister.Data.WishList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mail929 on 11/25/14.
 */

public class NewListDialog extends DialogFragment
{
    List<View> views = new ArrayList<>();
    LinearLayout container;
    String[] types = {"Tag", "Person", "Date Range", "Time", "Day"};
    String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    CheckBox cb;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // Use the Builder class for convenient dialog construction
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        final View v = inflater.inflate(R.layout.dialog_new_list, null);

        container = (LinearLayout) v.findViewById(R.id.container);
        final LayoutInflater linflater = LayoutInflater.from(getActivity());
        View view = linflater.inflate(R.layout.criteria_item, container, false);
        setupSpinner(view);
        views.add(view);

        v.findViewById(R.id.scrollView2).setVisibility(View.GONE);
        cb = (CheckBox) v.findViewById(R.id.auto);
        cb.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v2)
            {
                if(cb.isChecked())
                {
                    v.findViewById(R.id.scrollView2).setVisibility(View.VISIBLE);
                    v.findViewById(R.id.daysContainer).setVisibility(View.GONE);
                }
                else
                {
                    v.findViewById(R.id.scrollView2).setVisibility(View.GONE);
                    v.findViewById(R.id.daysContainer).setVisibility(View.VISIBLE);
                }
            }
        });

        repopulate();
        Button add = (Button) v.findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                View newView = linflater.inflate(R.layout.criteria_item, container, false);
                setupSpinner(newView);
                views.add(newView);
                repopulate();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Type the new list's name and click create to make a new list.")
                .setTitle("New List")
                .setView(v)
                .setPositiveButton("CREATE", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        EditText name = (EditText) v.findViewById(R.id.name);
                        EditText tags = (EditText) v.findViewById(R.id.tags);
                        EditText day = (EditText) v.findViewById(R.id.days);
                        CheckBox done = (CheckBox) v.findViewById(R.id.checked);
                        CheckBox exclude = (CheckBox) v.findViewById(R.id.exclude);
                        CheckBox delete = (CheckBox) v.findViewById(R.id.delete);

                        int daysToDelete = 0;
                        if (delete.isChecked() && !cb.isChecked())
                        {
                            if (day.getText().toString().equals(""))
                            {
                                daysToDelete = 365;
                            }
                            else
                            {
                                daysToDelete = Integer.parseInt(day.getText().toString());
                            }
                        }

                        IO.log("NewListDialog", "Creating list " + name.getText().toString());
                        List<WishList> lists = WLFragment.getLists();
                        WishList newList;
                        if (cb.isChecked())
                        {
                            List<String> criteria = new ArrayList<>();
                            for (View view : views)
                            {
                                Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
                                LinearLayout container = (LinearLayout) view.findViewById(R.id.container);
                                CheckBox mandatory = (CheckBox) view.findViewById(R.id.mandatory);
                                StringBuilder sb = new StringBuilder();
                                if (mandatory.isChecked())
                                {
                                    sb.append("mandatory ");
                                } else
                                {
                                    sb.append("optional ");
                                }
                                if (exclude.isChecked())
                                {
                                    sb.append("exclude ");
                                } else
                                {
                                    sb.append("include ");
                                }
                                switch (spinner.getSelectedItemPosition())
                                {
                                    case 0:
                                        //tag
                                        sb.append("tag ");
                                        sb.append(((EditText) container.findViewById(R.id.editText)).getText().toString());
                                        break;
                                    case 1:
                                        //person
                                        sb.append("person ");
                                        sb.append(((EditText) container.findViewById(R.id.editText)).getText().toString());
                                        break;
                                    case 2:
                                        //date range
                                        sb.append("date_range ");
                                        sb.append(((EditText) container.findViewById(R.id.editText1)).getText().toString() + " " + ((EditText) container.findViewById(R.id.editText2)).getText().toString());
                                        break;
                                    case 3:
                                        //time
                                        sb.append("time ");
                                        sb.append(((EditText) container.findViewById(R.id.editText)).getText().toString());
                                        break;
                                    case 4:
                                        //day
                                        sb.append("day ");
                                        sb.append(days[((Spinner) container.findViewById(R.id.spinner)).getSelectedItemPosition()]);
                                        break;
                                }
                                criteria.add(sb.toString());
                            }
                            newList = new AutoList(name.getText().toString(), new ArrayList<>(Arrays.asList(tags.getText().toString().split(" "))), criteria, done.isChecked(), daysToDelete);
                        } else
                        {
                            newList = new WishList(name.getText().toString(), new ArrayList<>(Arrays.asList(tags.getText().toString().split(" "))), done.isChecked(), daysToDelete);
                        }
                        lists.add(newList);
                        List<WishList> unArchieved = WLFragment.getUnArchived();
                        unArchieved.add(newList);
                        WLFragment.openNewest();
                        IO.save(WLFragment.getLists());
                        WLFragment.getFrag(getActivity()).updateList();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                    }
                });
        return builder.create();
    }

    public void setupSpinner(View view)
    {
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        final LinearLayout container = (LinearLayout) view.findViewById(R.id.container);
        ArrayAdapter<String> sadapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, types);
        sadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(sadapter);
        final LayoutInflater inflater = LayoutInflater.from(getActivity());

        View sub = null;
        switch (spinner.getSelectedItemPosition())
        {
            case 0:
                //Tag
                sub = inflater.inflate(R.layout.criteria_item_string, container, false);
                break;
            case 1:
                //Person
                sub = inflater.inflate(R.layout.criteria_item_string, container, false);
                break;
            case 2:
                //Date Range
                sub = inflater.inflate(R.layout.criteria_item_dates, container, false);
                break;
            case 3:
                //Time
                sub = inflater.inflate(R.layout.criteria_item_int, container, false);
                break;
            case 4:
                //Day
                sub = inflater.inflate(R.layout.criterial_item_day, container, false);
                Spinner day = (Spinner) sub.findViewById(R.id.spinner);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, days);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                day.setAdapter(adapter);
                break;
        }
        container.addView(sub);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                container.removeAllViews();
                View sub = null;
                switch (position)
                {
                    case 0:
                        //Tag
                        sub = inflater.inflate(R.layout.criteria_item_string, container, false);
                        break;
                    case 1:
                        //Person
                        sub = inflater.inflate(R.layout.criteria_item_string, container, false);
                        break;
                    case 2:
                        //Date Range
                        sub = inflater.inflate(R.layout.criteria_item_dates, container, false);
                        break;
                    case 3:
                        //Time
                        sub = inflater.inflate(R.layout.criteria_item_int, container, false);
                        break;
                    case 4:
                        //Day
                        sub = inflater.inflate(R.layout.criterial_item_day, container, false);
                        Spinner day = (Spinner) sub.findViewById(R.id.spinner);
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, days);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        day.setAdapter(adapter);
                        break;
                }
                container.addView(sub);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
    }
    public void repopulate()
    {
        //populates the list with the items
        container.removeAllViews();
        for (int i = 0; i < views.size(); i++)
        {
            container.addView(views.get(i));
        }
    }
}