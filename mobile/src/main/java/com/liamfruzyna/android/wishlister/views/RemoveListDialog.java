package com.liamfruzyna.android.wishlister.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.liamfruzyna.android.wishlister.data.Data;
import com.liamfruzyna.android.wishlister.data.IO;

/**
 * Created by mail929 on 1/5/18.
 */

public class RemoveListDialog extends DialogFragment
{
	String listName;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		// Use the Builder class for convenient dialog construction
		listName = getArguments().getString("list");

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("Are you sure you want to delete " + listName + "?")
				.setTitle("Delete List")
				.setPositiveButton("DELETE", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						(new Thread(new Runnable()
						{
							@Override
							public void run()
							{
								IO.getInstance().deleteList(Data.getListFromName(listName));
								IO.getInstance().pullLists();
							}
						})).start();
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