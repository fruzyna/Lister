package com.liamfruzyna.android.lister.DialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.liamfruzyna.android.lister.Fragments.WLFragment;
import com.liamfruzyna.android.lister.Data.IO;
import com.liamfruzyna.android.lister.Data.WishList;
import com.liamfruzyna.android.lister.R;

import java.io.File;

/**
 * Created by mail929 on 2/17/16.
 */
public class EditListNameDialog extends DialogFragment
{
    EditText name;
    EditText day;
    CheckBox delete;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        final View v = inflater.inflate(R.layout.dialog_edit_list, null);
        name = (EditText) v.findViewById(R.id.name);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final WishList list = WLFragment.getCurrentList();
        name.setHint("name");
        name.setText(list.name);

        if(list.auto)
        {
            v.findViewById(R.id.daysContainer).setVisibility(View.GONE);
        }

        day = (EditText) v.findViewById(R.id.days);
        delete = (CheckBox) v.findViewById(R.id.delete);
        day.setText(list.daysToDelete + "");
        if(list.daysToDelete != 0)
        {
            delete.setChecked(true);
        }

        builder.setMessage("Edit name of " + list.name)
                .setTitle("Edit Name")
                .setView(v)
                .setPositiveButton("APPEND", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        String old = new String(list.name);
                        list.name = name.getText().toString();

                        int daysToDelete = 0;
                        if (delete.isChecked())
                        {
                            if (day.getText().toString().equals(""))
                            {
                                daysToDelete = 365;
                            }
                            else
                            {
                                daysToDelete = Integer.parseInt(day.getText().toString());
                            }
                        }
                        list.daysToDelete = daysToDelete;
                        WLFragment.getFrag(getActivity()).setupSpinner();
                        File file = new File(IO.fileDir, old + ".json");
                        file.delete();
                        IO.save(WLFragment.getLists());
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
