package com.liamfruzyna.android.wishlister.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.liamfruzyna.android.wishlister.data.Data;
import com.liamfruzyna.android.wishlister.data.IO;
import com.liamfruzyna.android.wishlister.data.ListObj;
import com.liamfruzyna.android.wishlister.activities.ListerActivity;

/**
 * Created by mail929 on 2/27/17.
 */

public class ArchiveListDialog extends DialogFragment
{
    ListObj list;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        list = Data.getCurrentList();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure, you want to archive " + list.name + "?")
                .setTitle("Archive List: " + list.name)
                .setPositiveButton("ARCHIVE", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        list.archived = true;
                        IO.getInstance().saveAndSync(list.name);
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