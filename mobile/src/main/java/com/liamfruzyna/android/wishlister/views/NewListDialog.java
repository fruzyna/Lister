package com.liamfruzyna.android.wishlister.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.liamfruzyna.android.wishlister.R;
import com.liamfruzyna.android.wishlister.data.Data;
import com.liamfruzyna.android.wishlister.data.DbConnection;
import com.liamfruzyna.android.wishlister.data.IO;

/**
 * Created by mail929 on 1/5/18.
 */

public class NewListDialog extends DialogFragment
{
	LayoutInflater inflater;

	EditText name;
	EditText days;
	CheckBox auto;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		// Use the Builder class for convenient dialog construction
		inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_new_list, null);

		name = view.findViewById(R.id.newlist_edit_name);
		days = view.findViewById(R.id.newlist_edit_days);
		auto = view.findViewById(R.id.newlist_check_auto);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("Type the new list's name and click create to make a new list.")
				.setTitle("New List")
				.setView(view)
				.setPositiveButton("CREATE", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						(new Thread(new Runnable()
						{
							@Override
							public void run()
							{
								DbConnection.createList(name.getText().toString(), Integer.parseInt(days.getText().toString()), auto.isChecked());
								DbConnection.pullLists();
								Data.setCurrentList(Data.getNewestListId());
								IO.ready = true;
							}
						})).start();
					}
				})
				.setNegativeButton("CANCEL", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						IO.ready = true;
					}
				});
		return builder.create();
	}
}