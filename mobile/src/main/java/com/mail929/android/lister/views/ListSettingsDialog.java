package com.mail929.android.lister.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.mail929.android.lister.R;
import com.mail929.android.lister.data.Data;
import com.mail929.android.lister.data.DbConnection;
import com.mail929.android.lister.data.IO;
import com.mail929.android.lister.data.ListObj;

/**
 * Created by mail929 on 1/10/18.
 */

public class ListSettingsDialog extends DialogFragment
{
	LayoutInflater inflater;

	EditText name;
	EditText days;
	CheckBox deleteDone;
	CheckBox reset;
	CheckBox sortDate;
	CheckBox sortDone;
	CheckBox showDone;

	ListObj list;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		// Use the Builder class for convenient dialog construction
		inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_list_settings, null);

		name = view.findViewById(R.id.list_edit_name);
		days = view.findViewById(R.id.list_edit_days);
		deleteDone = view.findViewById(R.id.list_check_deldone);
		sortDate = view.findViewById(R.id.list_check_sortdate);
		reset = view.findViewById(R.id.list_check_reset);
		sortDone = view.findViewById(R.id.list_check_sortdone);
		showDone = view.findViewById(R.id.list_check_showdone);

		list = Data.getCurrentList();
		name.setText(list.getName());
		days.setText(list.getDaysToDel() + "");
		sortDate.setChecked(list.isSortDate());
		sortDone.setChecked(list.isSortDone());
		showDone.setChecked(list.isShowDone());

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("List Settings")
				.setView(view)
				.setPositiveButton("SAVE", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						(new Thread(new Runnable()
						{
							@Override
							public void run()
							{
								if(reset.isChecked())
								{
									DbConnection.resetList(list);
								}
								else if(deleteDone.isChecked())
								{
									DbConnection.deleteDone(list);
								}
								DbConnection.listSettings(list.getId(), name.getText().toString(), Integer.parseInt(days.getText().toString()), sortDone.isChecked(), sortDate.isChecked(), showDone.isChecked());
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
						IO.ready = true;
					}
				});
		return builder.create();
	}
}