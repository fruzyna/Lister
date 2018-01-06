package com.liamfruzyna.android.wishlister.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.liamfruzyna.android.wishlister.R;
import com.liamfruzyna.android.wishlister.data.Data;
import com.liamfruzyna.android.wishlister.data.DbConnection;
import com.liamfruzyna.android.wishlister.data.ListObj;

import java.util.List;

/**
 * Created by mail929 on 1/6/18.
 */

public class UnarchiveListDialog extends DialogFragment
{
	LayoutInflater inflater;

	LinearLayout view;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		// Use the Builder class for convenient dialog construction
		inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
		view = (LinearLayout) inflater.inflate(R.layout.dialog_unarchive_list, null);

		List<ListObj> lists = Data.getArchived();
		for(ListObj list : lists)
		{
			CheckBox box = new CheckBox(getActivity());
			box.setText(list.getName());
			view.addView(box);
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("Check lists you desire to remove from the archive.")
				.setTitle("Unarchive Lists")
				.setView(view)
				.setPositiveButton("UNARCHIVE", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						(new Thread(new Runnable()
						{
							@Override
							public void run()
							{
								for(int i = 0; i < view.getChildCount(); i++)
								{
									CheckBox box = (CheckBox) view.getChildAt(i);
									if(box.isChecked())
									{
										ListObj list = Data.getListFromName(box.getText().toString());
										list.setArchived(false);
										DbConnection.archiveList(list);
									}
									DbConnection.pullLists();
								}
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
