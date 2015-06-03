package com.liamfruzyna.android.lister.DialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.liamfruzyna.android.lister.Activities.WLActivity;
import com.liamfruzyna.android.lister.Data.WishList;
import com.liamfruzyna.android.lister.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by mail929 on 6/1/15.
 */
public class EditItemDialog extends DialogFragment
{
    EditText item;
    int position;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt("position");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        final View v = inflater.inflate(R.layout.new_item_item, null);
        item = (EditText) v.findViewById(R.id.name);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final WishList list = WLActivity.lists.get(WLActivity.current);
        builder.setMessage("Edit: " + WLActivity.items.get(position).item)
                .setTitle("Edit List Item")
                .setView(v)
                .setPositiveButton("APPEND", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        WLActivity.items.get(position).item = item.getText().toString();
                        ((WLActivity) getActivity()).updateList();
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