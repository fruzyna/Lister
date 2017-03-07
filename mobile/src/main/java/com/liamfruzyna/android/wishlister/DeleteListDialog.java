package com.liamfruzyna.android.wishlister;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by mail929 on 2/27/17.
 */

public class DeleteListDialog extends DialogFragment
{
    ListObj list;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        list = Data.getCurrentList();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure, you want to delete " + list.name + "? The list will never be recoverable, locally or on the server.")
                .setTitle("Delete List: " + list.name)
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Data.getLists().remove(list);
                        IO.getInstance().deleteList(list.name);
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