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
import com.liamfruzyna.android.lister.Data.IO;
import com.liamfruzyna.android.lister.Data.Item;
import com.liamfruzyna.android.lister.Data.WishList;
import com.liamfruzyna.android.lister.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by mail929 on 6/1/15.
 */
public class EditItemDialog extends DialogFragment
{
    EditText editText;
    int position;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt("position");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // Use the Builder class for convenient dialog construction
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        final View v = inflater.inflate(R.layout.new_item_item, null);
        editText = (EditText) v.findViewById(R.id.name);
        editText.setText(WLActivity.getItems().get(position).item);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Edit: " + WLActivity.getItems().get(position).item)
                .setTitle("Edit List Item")
                .setView(v)
                .setPositiveButton("APPEND", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        if (editText.getText().toString().equals(""))
                        {
                            //remove the item
                            Item item = WLActivity.getItems().get(position);
                            IO.log("EditItemDialog", "EditText is blank removing " + item);
                            WLActivity.getItems().remove(position);
                            WLActivity.getCurrentList().items.remove(item);
                            ((WLActivity) getActivity()).removeItemSnackbar(item);
                        } else
                        {
                            IO.log("EditItemDialog", "Updating " + WLActivity.getItems().get(position).item + " to " + editText.getText().toString());
                            WLActivity.getItems().get(position).item = editText.getText().toString();
                        }
                        IO.save(WLActivity.getLists());
                        ((WLActivity) getActivity()).updateList();
                    }
                })
                .setNeutralButton("REMOVE", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        //remove the item
                        Item item = WLActivity.getItems().get(position);
                        IO.log("EditItemDialog", "Removing " + item);
                        WLActivity.getItems().remove(position);
                        WLActivity.getCurrentList().items.remove(item);
                        ((WLActivity) getActivity()).removeItemSnackbar(item);
                        IO.save(WLActivity.getLists());
                        ((WLActivity) getActivity()).updateList();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do nothing
                    }
                });
        return builder.create();
    }
}