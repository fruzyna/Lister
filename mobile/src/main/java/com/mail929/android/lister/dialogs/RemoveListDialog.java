package com.mail929.android.lister.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.mail929.android.lister.data.Data;
import com.mail929.android.lister.data.DbConnection;
import com.mail929.android.lister.data.IO;
import com.mail929.android.lister.data.ListObj;

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
								ListObj list = Data.getListFromName(listName);
								if(list.getPerm() == 'o')
								{
									DbConnection.deleteList(list);
								}
								else
								{
									DbConnection.leaveList(list);
								}
								DbConnection.pullLists();
								IO.ready = true;
							}
						})).start();
					}
				})
				.setNegativeButton("CANCEL", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
					    IO.ready = false;
					}
				});
		return builder.create();
	}
}