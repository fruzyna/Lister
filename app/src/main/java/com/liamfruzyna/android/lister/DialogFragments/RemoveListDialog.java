package com.liamfruzyna.android.lister.DialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.liamfruzyna.android.lister.Activities.WLActivity;
import com.liamfruzyna.android.lister.Data.IO;
import com.liamfruzyna.android.lister.Data.WishList;
import com.liamfruzyna.android.lister.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mail929 on 11/25/14.
 */

public class RemoveListDialog extends DialogFragment
{
    List<WishList> lists = WLActivity.lists;
    int current = WLActivity.current;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to delete " + lists.get(current).name + "? You can never get it back.")
                .setTitle("Delete List?")
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {

                        File file = new File(IO.fileDir, lists.get(current).name + ".json");
                        file.delete();
                        final WishList old = lists.get(current);
                        lists.remove(current);
                        //setup spinner
                        List<String> names = new ArrayList<String>();
                        for (int i = 0; i < lists.size(); i++)
                        {
                            names.add(lists.get(i).name);
                        }
                        ArrayAdapter<String> sadapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, names);
                        sadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Spinner spin = (Spinner) getActivity().findViewById(R.id.spinner);
                        spin.setAdapter(sadapter);
                        current = spin.getSelectedItemPosition();
                        if (names.size() == 0)
                        {
                            IO.save(lists);
                            LinearLayout list = (LinearLayout) getActivity().findViewById(R.id.list);
                            RelativeLayout tagcv = (RelativeLayout) getActivity().findViewById(R.id.tag);
                            list.removeAllViews();
                            tagcv.removeAllViews();

                        }
                        ((WLActivity) getActivity()).removeListSnackbar(old);
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
