package com.liamfruzyna.android.wishlister.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.liamfruzyna.android.wishlister.data.AutoList;
import com.liamfruzyna.android.wishlister.data.CriteriaTypes;
import com.liamfruzyna.android.wishlister.data.Criterion;
import com.liamfruzyna.android.wishlister.data.Data;
import com.liamfruzyna.android.wishlister.data.IO;
import com.liamfruzyna.android.wishlister.data.ListObj;
import com.liamfruzyna.android.wishlister.R;
import com.liamfruzyna.android.wishlister.activities.ListerActivity;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mail929 on 11/25/14.
 */

public class NewListDialog extends DialogFragment implements View.OnClickListener
{
    String[] types = {"Tag", "Person", "Date Range", "Days", "Day of Week", "Date"};
    String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    LayoutInflater inflater;

    View root;
    EditText nameView;
    EditText tagsView;
    EditText daysView;
    CheckBox deleteView;
    CheckBox showView;
    CheckBox autoView;
    ScrollView scroll;
    LinearLayout container;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // Use the Builder class for convenient dialog construction
        inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        root = inflater.inflate(R.layout.dialog_new_list, null);

        //view initialization
        nameView = (EditText) root.findViewById(R.id.name);
        tagsView = (EditText) root.findViewById(R.id.tags);
        daysView = (EditText) root.findViewById(R.id.days);
        deleteView = (CheckBox) root.findViewById(R.id.delete);
        showView = (CheckBox) root.findViewById(R.id.checked);
        autoView = (CheckBox) root.findViewById(R.id.auto);
        scroll = (ScrollView) root.findViewById(R.id.autoScroll);
        container = (LinearLayout) root.findViewById(R.id.container);

        makeView(container, true);
        autoView.setOnClickListener(this);
        scroll.setVisibility(View.GONE);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Type the new list's name and click create to make a new list.")
                .setTitle("New List")
                .setView(root)
                .setPositiveButton("CREATE", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        List<String> tags = new ArrayList<>();
                        for(String s : tagsView.getText().toString().split(" "))
                        {
                            tags.add(s);
                        }

                        int days = 0;
                        try {
                            days = Integer.parseInt(daysView.getText().toString());
                        }
                        catch (NumberFormatException e) {
                            days = 0;
                        }

                        if(autoView.isChecked())
                        {
                            Criterion criterion = makeCriteria(container);
                            AutoList list = new AutoList(nameView.getText().toString(), tags, criterion, showView.isChecked(), days);
                            Data.replaceList(list);
                        }
                        else
                        {
                            ListObj list = new ListObj(nameView.getText().toString(), tags, showView.isChecked(), days);
                            Data.replaceList(list);
                        }

                        IO.getInstance().saveAndSync();
                        ((ListerActivity) getActivity()).loadActivity();
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

    public Criterion makeCriteria(ViewGroup group)
    {
        List<Criterion> criteria = new ArrayList<>();
        for(int i = 0; i < group.getChildCount(); i++)
        {
            View child = group.getChildAt(i);
            if (child.getId() == R.id.criteriaGroupContainer)
            {
                criteria.add(makeCriteria((ViewGroup) child));
            }
            else if (child.getId() == R.id.criteriaItemContainer)
            {
                boolean not = ((CheckBox) child.findViewById(R.id.exclude)).isChecked();
                Spinner spinner = ((Spinner) child.findViewById(R.id.spinner));
                CriteriaTypes type = CriteriaTypes.TAG;
                String data = "";
                switch (spinner.getSelectedItemPosition())
                {
                    case 0:
                        data = ((EditText) child.findViewById(R.id.editText)).getText().toString();
                        break;
                    case 1:
                        type = CriteriaTypes.PERSON;
                        data = ((EditText) child.findViewById(R.id.editText)).getText().toString();
                        break;
                    case 2:
                        type = CriteriaTypes.DATE_RANGE;
                        data = ((EditText) child.findViewById(R.id.editText1)).getText().toString();
                        data += "," + ((EditText) child.findViewById(R.id.editText2)).getText().toString();
                        break;
                    case 3:
                        type = CriteriaTypes.WITHIN_DAYS;
                        data = ((EditText) child.findViewById(R.id.editText)).getText().toString();
                        break;
                    case 4:
                        type = CriteriaTypes.DAY_OF_WEEK;
                        data = ((Spinner) child.findViewById(R.id.daySpinner)).getSelectedItemPosition() + "";
                        break;
                    case 5:
                        type = CriteriaTypes.DATE;
                        data = ((EditText) child.findViewById(R.id.editText)).getText().toString();
                        break;
                }
                criteria.add(new Criterion(type, 0, not, data, new ArrayList<Criterion>()));
            }
        }
        CriteriaTypes type;
        if(((RadioButton) group.findViewById(R.id.oneButton)).isSelected())
        {
            type = CriteriaTypes.PASS_ONE;
        }
        else
        {
            type = CriteriaTypes.PASS_ALL;
        }
        boolean not = ((CheckBox) group.findViewById(R.id.groupExclude)).isChecked();
        return new Criterion(type, 0, not, "", criteria);
    }

    public void makeView(LinearLayout parent, boolean isGroup)
    {
        if(isGroup)
        {
            View group = inflater.inflate(R.layout.criteria_group, null);
            group.findViewById(R.id.addCriterion).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    ViewGroup parent = (ViewGroup) v.getParent().getParent();
                    makeView(((LinearLayout) parent.findViewById(R.id.groupCriteria)), false);
                }
            });
            group.findViewById(R.id.addGroup).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    ViewGroup parent = (ViewGroup) v.getParent().getParent();
                    makeView(((LinearLayout) parent.findViewById(R.id.groupCriteria)), true);
                }
            });
            parent.addView(group);
        }
        else
        {
            View item = inflater.inflate(R.layout.criteria_item, null);
            Spinner typeSpinner = (Spinner) item.findViewById(R.id.spinner);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, types);
            typeSpinner.setAdapter(adapter);

            typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                {
                    View itemView;
                    switch (position)
                    {
                        default:
                        case 0:
                        case 1:
                        case 5:
                            itemView = inflater.inflate(R.layout.criteria_item_string, null);
                            break;
                        case 2:
                            itemView = inflater.inflate(R.layout.criteria_item_dates, null);
                            break;
                        case 3:
                            itemView = inflater.inflate(R.layout.criteria_item_int, null);
                            break;
                        case 4:
                            itemView = inflater.inflate(R.layout.criteria_item_day, null);
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, days);
                            ((Spinner) itemView.findViewById(R.id.daySpinner)).setAdapter(adapter);
                            break;
                    }

                    LinearLayout container =  (LinearLayout) ((ViewGroup) parent.getParent()).findViewById(R.id.container);
                    container.removeAllViews();
                    container.addView(itemView);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
            parent.addView(item);
        }
    }

    @Override
    public void onClick(View v)
    {
        if(v.equals(autoView))
        {
            if(autoView.isChecked())
            {
                scroll.setVisibility(View.VISIBLE);
            }
            else
            {
                scroll.setVisibility(View.GONE);
            }
        }/*
        else if(v.equals(addView))
        {
            View group = inflater.inflate(R.layout.criteria_group, null);
            group.findViewById(R.id.addCriterion).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View groupButton)
                {
                    for(View group : groups)
                    {
                        System.out.println("searching");
                        if(group.findViewById(R.id.addCriterion).equals(groupButton))
                        {
                            System.out.println("Adding criterion");
                            View criterion = inflater.inflate(R.layout.criteria_item, null);

                            Spinner typeSpinner = ((Spinner) criterion.findViewById(R.id.spinner));
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, types);
                            typeSpinner.setAdapter(adapter);

                            typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                            {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                                {
                                    View itemView;
                                    switch (position)
                                    {
                                        default:
                                        case 0:
                                        case 1:
                                        case 5:
                                            itemView = inflater.inflate(R.layout.criteria_item_string, null);
                                            break;
                                        case 2:
                                            itemView = inflater.inflate(R.layout.criteria_item_dates, null);
                                            break;
                                        case 3:
                                            itemView = inflater.inflate(R.layout.criteria_item_int, null);
                                            break;
                                        case 4:
                                            itemView = inflater.inflate(R.layout.criteria_item_day, null);
                                            break;
                                    }

                                    for(View criterion : items)
                                    {
                                        if(criterion.findViewById(R.id.spinner).equals(parent))
                                        {
                                            LinearLayout container = ((LinearLayout) criterion.findViewById(R.id.container));
                                            container.removeAllViews();
                                            container.addView(itemView);
                                        }
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            items.add(criterion);
                            ((LinearLayout) group.findViewById(R.id.groupCriteria)).addView(criterion);
                        }
                    }
                }
            });
            groups.add(group);
            container.addView(group);
        }*/
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
                sub = inflater.inflate(R.layout.criteria_item_day, container, false);
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
                        sub = inflater.inflate(R.layout.criteria_item_day, container, false);
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
}