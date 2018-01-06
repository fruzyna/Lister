package com.liamfruzyna.android.wishlister.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.liamfruzyna.android.wishlister.R;
import com.liamfruzyna.android.wishlister.data.Data;
import com.liamfruzyna.android.wishlister.data.DbConnection;

/**
 * Created by mail929 on 1/5/18.
 */

public class ShareListDialog extends DialogFragment
{
	LayoutInflater inflater;

	EditText user;
	Spinner perm;

	String listName;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		// Use the Builder class for convenient dialog construction
		inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_share_list, null);

		user = view.findViewById(R.id.share_edit_user);
		perm = view.findViewById(R.id.share_spin_perm);

		listName = getArguments().getString("list");

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("Allow another Lister user to view/edit the list")
				.setTitle("Share List")
				.setView(view)
				.setPositiveButton("SHARE", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						(new Thread(new Runnable()
						{
							@Override
							public void run()
							{
								String selected = ((String) perm.getSelectedItem()).toLowerCase();
								DbConnection.shareList(Data.getListFromName(listName), user.getText().toString(), selected.charAt(0));
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