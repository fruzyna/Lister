package com.mail929.android.lister.views;

import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.mail929.android.lister.R;
import com.mail929.android.lister.data.IO;

/**
 * Created by mail929 on 2/6/18.
 */

public class ConstraintView implements AdapterView.OnItemSelectedListener
{
    LinearLayout constraint;
    LayoutInflater inflater;

    Spinner type;
    CheckBox exclude;
    LinearLayout container;

    LinearLayout data;

    String types[] = {"Tag", "Person", "Date", "Within", "Before", "After", "Between"};
    String entryTypes[] = {"String", "String", "Date", "Number", "Date", "Date", "Dual"};

    public ConstraintView(LayoutInflater inflater, LinearLayout inflatedView, Context c)
    {
        this.inflater = inflater;
        this.constraint = inflatedView;

        type = constraint.findViewById(R.id.constraint_spinner);
        exclude = constraint.findViewById(R.id.constraint_check_exclude);
        container = constraint.findViewById(R.id.constraint_container);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(c, R.layout.spinner_item, types);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        type.setAdapter(adapter);
        type.setOnItemSelectedListener(this);
        type.setSelection(0);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
        String selectedType = types[i];
        String entryType = entryTypes[i];

        container.removeAllViews();

        if(entryType.equals("Dual"))
        {
            data = (LinearLayout) inflater.inflate(R.layout.dual_constraint_item, container, false);
            container.addView(data);
        }
        else
        {
            data = (LinearLayout) inflater.inflate(R.layout.text_constraint_item, container, false);
            container.addView(data);

            TextView text = data.findViewById(R.id.textc_text_type);
            text.setText(selectedType + ": ");

            EditText input = data.findViewById(R.id.textc_edit_data);
            int inputType = 0;
            switch(entryType)
            {
                case "String":
                    inputType = InputType.TYPE_CLASS_TEXT;
                    break;
                case "Date":
                    inputType = 20;
                    break;
                case "Number":
                    inputType = InputType.TYPE_CLASS_NUMBER;
                    break;
            }
            input.setInputType(inputType);
        }
    }

    public String getData()
    {
        int i = type.getSelectedItemPosition();
        String selectedType = types[i];
        String entryType = entryTypes[i];

        String output = "";
        if(exclude.isChecked())
        {
            output += "!";
        }
        output += selectedType + ":";
        if(entryType.equals("Dual"))
        {
            EditText data1 = data.findViewById(R.id.textc_edit_data1);
            EditText data2 = data.findViewById(R.id.textc_edit_data2);
            output += data1.getText().toString() + "," + data2.getText().toString();
        }
        else
        {
            switch(selectedType)
            {
                case "Tag":
                    output += "#";
                    break;
                case "Person":
                    output += "@";
                    break;
            }
            EditText dataView = data.findViewById(R.id.textc_edit_data);
            output += dataView.getText().toString();
        }
        return output;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView){}
}
