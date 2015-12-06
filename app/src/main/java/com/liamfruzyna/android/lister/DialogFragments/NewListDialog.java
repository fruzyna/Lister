package com.liamfruzyna.android.lister.DialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.liamfruzyna.android.lister.Activities.WLActivity;
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
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // Use the Builder class for convenient dialog construction
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        final View v = inflater.inflate(R.layout.new_list_item, null);
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
                        IO.log("NewListDialog", "Creating list " + name.getText().toString());
                        List<WishList> lists = WLActivity.getLists();
                        WishList newList = new WishList(name.getText().toString(), new ArrayList<>(Arrays.asList(tags.getText().toString().split(" "))));
                        lists.add(newList);
                        List<WishList> unArchieved = WLActivity.getUnArchived();
                        unArchieved.add(newList);
                        WLActivity.openNewest();
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