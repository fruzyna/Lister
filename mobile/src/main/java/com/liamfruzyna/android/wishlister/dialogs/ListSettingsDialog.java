package com.liamfruzyna.android.wishlister.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.liamfruzyna.android.wishlister.data.Data;
import com.liamfruzyna.android.wishlister.data.IO;
import com.liamfruzyna.android.wishlister.data.Item;
import com.liamfruzyna.android.wishlister.data.ListObj;
import com.liamfruzyna.android.wishlister.R;
import com.liamfruzyna.android.wishlister.activities.ListerActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mail929 on 2/27/17.
 */

public class ListSettingsDialog extends DialogFragment
{
    ListObj list;
    View v;
    EditText name;
    EditText days;
    CheckBox reset;
    CheckBox deleteDone;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        list = Data.getCurrentList();

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.dialog_list_settings, null);
        name = (EditText) v.findViewById(R.id.name);
        name.setText(list.name);
        days = (EditText) v.findViewById(R.id.days);
        days.setText(list.daysToDelete + "");
        reset = (CheckBox) v.findViewById(R.id.reset);
        reset.setChecked(false);
        deleteDone = (CheckBox) v.findViewById(R.id.deleteDone);
        deleteDone.setChecked(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(list.name)
                .setView(v)
                .setPositiveButton("SAVE", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        if(reset.isChecked())
                        {
                            list.items = new ArrayList<>();
                        }
                        else if(deleteDone.isChecked())
                        {
                            boolean done = true;
                            while(done)
                            {
                                boolean found = false;
                                for(int i = 0; i < Data.getItems().size(); i++)
                                {
                                    if(Data.getItems().get(i).done)
                                    {
                                        found = true;
                                        Data.getItems().remove(i);
                                        break;
                                    }
                                }
                                done = found;
                            }
                        }
                        String daysString = days.getText().toString();
                        if(!daysString.equals(""))
                        {
                            list.daysToDelete = Integer.parseInt(daysString);
                        }
                        String nameString = name.getText().toString();
                        if(!nameString.equals(list.name))
                        {
                            boolean found = false;
                            for(ListObj list : Data.getLists())
                            {
                                if(list.name.equals(nameString))
                                {
                                    found = true;
                                }
                            }
                            if(!found)
                            {
                                Data.getLists().remove(list);
                                IO.getInstance().deleteList(list.name);
                                list.name = nameString;
                                Data.getLists().add(list);
                                ((ListerActivity) getActivity()).saveCurrent(Data.getUnArchived().size() - 1);
                            }
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
}