package com.liamfruzyna.android.lister.DialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.liamfruzyna.android.lister.Fragments.WLFragment;
import com.liamfruzyna.android.lister.Data.IO;
import com.liamfruzyna.android.lister.Data.WishList;
import com.liamfruzyna.android.lister.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by mail929 on 12/20/14.
 */
public class EditTagsDialog extends DialogFragment
{
    EditText tags;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        final View v = inflater.inflate(R.layout.dialog_new_item, null);
        tags = (EditText) v.findViewById(R.id.name);
        tags.setHint("Tags");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final WishList list = WLFragment.getCurrentList();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < list.tags.size(); i++)
        {
            sb.append(list.tags.get(i) + " ");
        }
        tags.setText(sb.toString());
        builder.setMessage("Edit tags of " + list.name)
                .setTitle("Edit Tags")
                .setView(v)
                .setPositiveButton("APPEND", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        IO.log("EditTagDialog", "Settings " + list.name + "'s tags to " + tags.getText().toString());
                        list.tags = new ArrayList<>(Arrays.asList(tags.getText().toString().split(" ")));
                        WLFragment.getFrag(getActivity()).updateList();
                        IO.save(WLFragment.getLists());
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