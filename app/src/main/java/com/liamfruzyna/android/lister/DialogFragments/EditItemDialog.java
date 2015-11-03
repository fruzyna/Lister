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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Edit: " + WLActivity.items.get(position).item)
                .setTitle("Edit List Item")
                .setView(v)
                .setPositiveButton("APPEND", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        if (editText.getText().toString().equals(""))
                        {
                            System.out.println("[EditItemDialog] Removing Item " + position);
                            Item item = WLActivity.items.get(position);
                            WLActivity.items.remove(position);
                            WLActivity.lists.get(WLActivity.current).items.remove(item);
                            ((WLActivity) getActivity()).removeItemSnackbar(item);
                        } else
                        {
                            WLActivity.items.get(position).item = editText.getText().toString();
                        }
                        IO.save(WLActivity.lists);
                        ((WLActivity) getActivity()).updateList();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        //do nothing
                    }
                });
        return builder.create();
    }
}