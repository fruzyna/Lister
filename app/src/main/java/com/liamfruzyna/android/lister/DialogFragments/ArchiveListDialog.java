package com.liamfruzyna.android.lister.DialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.liamfruzyna.android.lister.Activities.WLActivity;
import com.liamfruzyna.android.lister.Data.WishList;
import com.liamfruzyna.android.lister.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mail929 on 10/29/15.
 */
public class ArchiveListDialog extends DialogFragment
{
    List<WishList> lists = WLActivity.lists;
    int current = WLActivity.current;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to archive " + lists.get(current).name + "?")
                .setTitle("Archive List?")
                .setPositiveButton("ARCHIVE", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        final WishList old = lists.get(current);
                        old.archived = true;
                        List<WishList> lists = WLActivity.unArchieved;
                        for(int i = 0; i < lists.size(); i++)
                        {
                            if(lists.get(i).name.equals(old.name))
                            {
                                lists.remove(i);
                            }
                        }
                        List<WishList> unArchieved = WLActivity.unArchieved;
                        Spinner spin = (Spinner) getActivity().findViewById(R.id.spinner);
                        //creates a list of events level and distance to fill out the spinner
                        List<String> names = new ArrayList<String>();
                        for (int i = 0; i < unArchieved.size(); i++)
                        {
                            names.add(unArchieved.get(i).name);
                        }
                        //setup spinner
                        ArrayAdapter<String> sadapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, names);
                        sadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spin.setAdapter(sadapter);
                        WLActivity.current = spin.getSelectedItemPosition();
                        ((WLActivity) getActivity()).updateList();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        //do nothing
                    }
                });
        return builder.create();
    }
}