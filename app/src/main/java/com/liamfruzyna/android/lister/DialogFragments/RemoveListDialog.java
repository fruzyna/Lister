package com.liamfruzyna.android.lister.DialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.liamfruzyna.android.lister.Activities.WLActivity;
import com.liamfruzyna.android.lister.Data.IO;
import com.liamfruzyna.android.lister.Data.WishList;
import com.liamfruzyna.android.lister.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mail929 on 11/25/14.
 */

public class RemoveListDialog extends DialogFragment
{
    List<WishList> lists = WLActivity.getLists();
    WishList current = WLActivity.getCurrentList();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to delete " + current.name + "? You can never get it back.")
                .setTitle("Delete List?")
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        IO.log("RemoveListDialog", "Removing and deleting list " + current.name);
                        File file = new File(IO.fileDir, current.name + ".json");
                        file.delete();
                        lists.remove(current);
                        WLActivity.getUnArchived().remove(current);
                        IO.save(lists);
                        ((WLActivity) getActivity()).removeListSnackbar(current);
                        WLActivity.setupSpinner();
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
