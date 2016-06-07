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
import android.widget.TextView;

import com.liamfruzyna.android.lister.Data.Data;
import com.liamfruzyna.android.lister.Fragments.WLFragment;
import com.liamfruzyna.android.lister.Data.AutoList;
import com.liamfruzyna.android.lister.Data.IO;
import com.liamfruzyna.android.lister.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mail929 on 2/14/16.
 */
public class EditCriteriaDialog extends DialogFragment
{
    String[] types = {"Tag", "Person", "Date Range", "Time", "Day"};
    String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    LayoutInflater inflater;
    List<View> views;
    View v;
    AutoList list;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.dialog_edit_criteria, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        updateList();
        builder.setMessage("Edit criteria of " + list.name)
                .setTitle("Edit Criteria")
                .setView(v)
                .setPositiveButton("APPEND", new DialogInterface.OnClickListener()
                {
                    List<String> criteria = new ArrayList<>();

                    public void onClick(DialogInterface dialog, int id)
                    {
                        List<String> criteria = new ArrayList<>();
                        for (View view : views)
                        {
                            Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
                            LinearLayout container = (LinearLayout) view.findViewById(R.id.container);
                            CheckBox mandatory = (CheckBox) view.findViewById(R.id.mandatory);
                            CheckBox exclude = (CheckBox) view.findViewById(R.id.exclude);
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
                            System.out.println("Criteria: " + sb.toString());
                            criteria.add(sb.toString());
                        }
                        list.setCriteria(criteria);
                        WLFragment.getFrag(getActivity()).updateList();
                        IO.saveList();
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

    public void updateList()
    {
        final LinearLayout container = (LinearLayout) v.findViewById(R.id.container);
        container.removeAllViews();
        list = (AutoList) Data.getCurrentList();
        views = new ArrayList<>();
        for(String c : list.getCriteria())
        {
            View view = inflater.inflate(R.layout.criteria_item, null);
            String[] data = c.split(" ");

            if(data[0].equals("mandatory"))
            {
                ((CheckBox) view.findViewById(R.id.mandatory)).setChecked(true);
            }
            else
            {
                ((CheckBox) view.findViewById(R.id.mandatory)).setChecked(false);
            }
            if(data[1].equals("include"))
            {
                ((CheckBox) view.findViewById(R.id.exclude)).setChecked(false);
            }
            else
            {
                ((CheckBox) view.findViewById(R.id.exclude)).setChecked(true);
            }

            int num = 0;
            if(data[2].equals("tag"))
            {
                num = 0;
            }
            else if(data[2].equals("person"))
            {
                num = 1;
            }
            else if(data[2].equals("date_range"))
            {
                num = 2;
            }
            else if(data[2].equals("time"))
            {
                num = 3;
            }
            else if(data[2].equals("day"))
            {
                num = 4;
            }
            setupSpinner(view, num, c.split(data[2])[1]);
            views.add(view);
            container.addView(view);
        }

        ((Button) v.findViewById(R.id.add)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                View view = inflater.inflate(R.layout.criteria_item, null);
                setupSpinner(view, 0, "");
                views.add(view);
                container.addView(view);
            }
        });
    }

    public void setupSpinner(View view, int pos, final String data)
    {
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        final LinearLayout container = (LinearLayout) view.findViewById(R.id.container);
        ArrayAdapter<String> sadapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, types);
        sadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(sadapter);
        final LayoutInflater inflater = LayoutInflater.from(getActivity());
        spinner.setSelection(pos);

        View sub = null;
        switch (spinner.getSelectedItemPosition())
        {
            case 0:
                //Tag
                sub = inflater.inflate(R.layout.criteria_item_string, container, false);
                ((EditText) sub.findViewById(R.id.editText)).setText(data.substring(1));
                break;
            case 1:
                //Person
                sub = inflater.inflate(R.layout.criteria_item_string, container, false);
                ((EditText) sub.findViewById(R.id.editText)).setText(data.substring(1));
                break;
            case 2:
                //Date Range
                sub = inflater.inflate(R.layout.criteria_item_dates, container, false);
                ((EditText) sub.findViewById(R.id.editText1)).setText(data.split(" ")[0].substring(1));
                ((EditText) sub.findViewById(R.id.editText2)).setText(data.split(" ")[1].substring(1));
                break;
            case 3:
                //Time
                sub = inflater.inflate(R.layout.criteria_item_int, container, false);
                ((EditText) sub.findViewById(R.id.editText)).setText(data.substring(1));
                break;
            case 4:
                //Day
                sub = inflater.inflate(R.layout.criterial_item_day, container, false);
                Spinner day = (Spinner) sub.findViewById(R.id.spinner);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, days);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                day.setAdapter(adapter);
                for(int i = 0; i < days.length; i++)
                {
                    if(days[i].equals(data.substring(1) + "day"))
                    {
                        day.setSelection(i);
                        break;
                    }
                }
                break;
        }
        container.addView(sub);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            boolean done = false;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if(done)
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
                done = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
    }
}