package com.liamfruzyna.android.lister.DialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.liamfruzyna.android.lister.Data.Data;
import com.liamfruzyna.android.lister.Data.Item;
import com.liamfruzyna.android.lister.Fragments.WLFragment;
import com.liamfruzyna.android.lister.Data.IO;
import com.liamfruzyna.android.lister.Data.WishList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mail929 on 11/30/15.
 */
public class ClearListDialog  extends DialogFragment
{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final WishList list = Data.getCurrentList();
        builder.setMessage("Remove all items from " + list.name)
                .setTitle("Clear List")
                .setPositiveButton("CLEAR", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        IO.log("ClearListDialog", "Clearing list " + list.name);
                        list.items = new ArrayList<>();
                        WLFragment.getFrag(getActivity()).updateList();
                        IO.getInstance().saveList();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                    }
                })
                .setNeutralButton("CLEAR DONE", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        List<Item> toRemove = new ArrayList<>();
                        for(Item item : list.items)
                        {
                            if(item.done)
                            {
                                toRemove.add(item);
                            }
                        }
                        for(Item item : toRemove)
                        {
                            list.items.remove(item);
                        }
                        WLFragment.getFrag(getActivity()).updateList();
                        IO.getInstance().saveList();
                    }
                });
        return builder.create();
    }
}