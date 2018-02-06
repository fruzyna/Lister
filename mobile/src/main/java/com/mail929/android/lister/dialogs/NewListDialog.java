package com.mail929.android.lister.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.mail929.android.lister.R;
import com.mail929.android.lister.data.Data;
import com.mail929.android.lister.data.DbConnection;
import com.mail929.android.lister.data.IO;
import com.mail929.android.lister.views.ConstraintView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mail929 on 1/5/18.
 */

public class NewListDialog extends DialogFragment implements View.OnClickListener
{
	LayoutInflater inflater;

	EditText name;
	EditText days;
	CheckBox auto;

	LinearLayout autoContainer;
	LinearLayout constraintsView;
	Button addConstraint;

	List<ConstraintView> constraints;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		// Use the Builder class for convenient dialog construction
		inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_new_list, null);

		constraints = new ArrayList<>();

		name = view.findViewById(R.id.newlist_edit_name);
		days = view.findViewById(R.id.newlist_edit_days);
		auto = view.findViewById(R.id.newlist_check_auto);

		autoContainer = view.findViewById(R.id.newlist_auto);
		constraintsView = view.findViewById(R.id.newlist_auto_container);
		addConstraint = view.findViewById(R.id.newlist_button_constraint);

		auto.setOnClickListener(this);
		addConstraint.setOnClickListener(this);

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
                                int newest = Data.getNewestListId();
                                if(auto.isChecked())
                                {
                                    for(ConstraintView c : constraints)
                                    {
                                        DbConnection.addConstraint(newest, c.getData());
                                    }
                                    DbConnection.pullList(newest);
                                }
								Data.setCurrentList(newest);
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

	@Override
	public void onClick(View view)
	{
		if(view.equals(auto))
		{
			if(auto.isChecked())
			{
				autoContainer.setVisibility(View.VISIBLE);
			}
			else
			{
				autoContainer.setVisibility(View.GONE);
			}
		}
		else if(view.equals(addConstraint))
        {
            LinearLayout constraint = (LinearLayout) inflater.inflate(R.layout.auto_constraint_item, constraintsView, false);
            constraints.add(new ConstraintView(inflater, constraint, getActivity()));
            constraintsView.addView(constraint);
        }
	}
}