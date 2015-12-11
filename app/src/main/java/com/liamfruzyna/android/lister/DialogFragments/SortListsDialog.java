package com.liamfruzyna.android.lister.DialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.liamfruzyna.android.lister.Activities.WLActivity;
import com.liamfruzyna.android.lister.Data.IO;
import com.liamfruzyna.android.lister.Data.WishList;
import com.liamfruzyna.android.lister.R;

import org.json.JSONException;

import java.util.List;

/**
 * Created by mail929 on 12/9/15.
 */
public class SortListsDialog extends DialogFragment
{
    List<WishList> lists;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // Use the Builder class for convenient dialog construction
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        final View v = inflater.inflate(R.layout.share_list_item, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        ListView list = (ListView) v.findViewById(R.id.listView);
        lists = WLActivity.getUnArchived();

        //setup list of lists to share
        list.setAdapter(new ArrayAdapter<WishList>(getActivity(), R.layout.sort_list_item, R.id.textView, lists) {
            public View getView(final int position, View convertView, ViewGroup parent) {
                View view;
                if (convertView == null) {
                    LayoutInflater infl = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                    convertView = infl.inflate(R.layout.sort_list_item, parent, false);
                }
                view = super.getView(position, convertView, parent);

                final TextView tv = ((TextView) view.findViewById(R.id.textView));
                tv.setText(lists.get(position).order + " - " + lists.get(position).name);

                ((Button) view.findViewById(R.id.down)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (lists.get(position).order > 1) {
                            lists.get(position).order--;
                            tv.setText(lists.get(position).order + " - " + lists.get(position).name);
                            IO.save(WLActivity.getLists());
                        }
                    }
                });
                ((Button) view.findViewById(R.id.up)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lists.get(position).order++;
                        tv.setText(lists.get(position).order + " - " + lists.get(position).name);
                        IO.save(WLActivity.getLists());
                    }
                });
                return view;
            }
        });

        //setup dialog
        builder.setMessage("Press up/down to move lists around")
                .setTitle("Sort Lists")
                .setView(v)
                .setNegativeButton("BACK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do nothing
                    }
                });
        return builder.create();
    }
}