package com.liamfruzyna.android.wishlister.dialogs;

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
import android.widget.ListView;
import android.widget.TextView;

import com.liamfruzyna.android.wishlister.R;
import com.liamfruzyna.android.wishlister.data.Data;
import com.liamfruzyna.android.wishlister.data.IO;

import org.json.JSONException;

import java.util.List;

/**
 * Created by mail929 on 3/10/17.
 */

public class ListViewDialog extends DialogFragment
{
    List<String> itemNames;
    View v;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        itemNames = getArguments().getStringArrayList("ITEMS");

        // Use the Builder class for convenient dialog construction
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.dialog_share_list, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        ListView list = (ListView) v.findViewById(R.id.listView);

        //setup list of lists to share
        list.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, itemNames) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View view;
                if (convertView == null) {
                    LayoutInflater infl = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                    convertView = infl.inflate(android.R.layout.simple_list_item_1, parent, false);
                }
                view = super.getView(position, convertView, parent);

                TextView tv = (TextView) view.findViewById(android.R.id.text1);
                tv.setText(itemNames.get(position));
                return view;
            }
        });

        //listen for list chosen
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = null;
                try {
                    shareBody = IO.getInstance().getListString(Data.getLists().get(position));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "New List");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

        //setup dialog
        builder.setMessage("Choose a list to share")
                .setTitle("Share a List")
                .setView(v)
                .setNegativeButton("BACK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do nothing
                    }
                });
        return builder.create();
    }
}