package com.liamfruzyna.android.lister.DialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.liamfruzyna.android.lister.Activities.WLActivity;
import com.liamfruzyna.android.lister.Activities.WLFragment;
import com.liamfruzyna.android.lister.Data.AutoList;
import com.liamfruzyna.android.lister.Data.WishList;
import com.liamfruzyna.android.lister.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mail929 on 2/14/16.
 */
public class EditCriteriaDialog extends DialogFragment
{
    LayoutInflater inflater;
    List<View> views;
    View v;
    AutoList list;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.edit_criteria_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        updateList();
        builder.setMessage("Edit criteria of " + list.name)
                .setTitle("Edit Criteria")
                .setView(v)
                .setPositiveButton("APPEND", new DialogInterface.OnClickListener()
                {
                    List<String> criteria = new ArrayList<>();

                    public void onClick(DialogInterface dialog, int id)
                    {
                        for (View view : views)
                        {
                            String c = ((EditText) view.findViewById(R.id.editText)).getText().toString();
                            if (!c.equals(""))
                            {
                                criteria.add(c);
                            }
                        }
                        list.setCriteria(criteria);
                        WLFragment.getFrag(getActivity()).updateList();
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

    public void updateList()
    {
        final LinearLayout container = (LinearLayout) v.findViewById(R.id.container);
        container.removeAllViews();
        list = (AutoList) WLFragment.getCurrentList();
        views = new ArrayList<>();
        for(String c : list.getCriteria())
        {
            View view = inflater.inflate(R.layout.string_item, null);
            ((EditText) view.findViewById(R.id.editText)).setText(c);
            views.add(view);
            container.addView(view);
        }

        ((Button) v.findViewById(R.id.add)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                View view = inflater.inflate(R.layout.string_item, null);
                views.add(view);
                container.addView(view);
            }
        });
    }
}