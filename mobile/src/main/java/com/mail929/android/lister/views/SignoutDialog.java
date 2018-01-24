package com.mail929.android.lister.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.mail929.android.lister.activities.LoginActivity;
import com.mail929.android.lister.data.DbConnection;
import com.mail929.android.lister.data.IO;

/**
 * Created by mail929 on 1/6/18.
 */

public class SignoutDialog extends DialogFragment
{
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("Are you sure you want to sign out?")
				.setTitle("Sign Out")
				.setPositiveButton("SIGN OUT", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						IO.getInstance().put(IO.SERVER_USER_PREF, "");
						IO.getInstance().put(IO.SERVER_PASS_PREF, "");
						DbConnection.resetHeaders();
						IO.wipeFiles();
						Intent intent = new Intent(getActivity(), LoginActivity.class);
						startActivity(intent);
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