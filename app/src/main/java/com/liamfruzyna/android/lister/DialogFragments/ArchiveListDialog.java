package com.liamfruzyna.android.lister.DialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.liamfruzyna.android.lister.Data.Data;
import com.liamfruzyna.android.lister.Fragments.WLFragment;
import com.liamfruzyna.android.lister.Data.IO;
import com.liamfruzyna.android.lister.Data.WishList;

import java.util.List;

/**
 * Created by mail929 on 10/29/15.
 */
public class ArchiveListDialog extends DialogFragment
{
    List<WishList> lists = Data.getUnArchived();
    WishList current = Data.getCurrentList();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to archive " + current.name + "?")
                .setTitle("Archive List?")
                .setPositiveButton("ARCHIVE", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        IO.log("ArchiveListDialog", "Archiving list " + current.name);
                        current.archived = true;
                        lists.remove(current);
                        WLFragment.getFrag(getActivity()).setupSpinner();
                        IO.getInstance().saveList();
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