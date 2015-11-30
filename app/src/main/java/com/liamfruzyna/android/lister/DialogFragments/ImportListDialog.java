package com.liamfruzyna.android.lister.DialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.liamfruzyna.android.lister.Activities.WLActivity;
import com.liamfruzyna.android.lister.Data.IO;
import com.liamfruzyna.android.lister.R;

import org.json.JSONException;

/**
 * Created by mail929 on 10/29/15.
 */
public class ImportListDialog extends DialogFragment
{
    EditText editText;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // Use the Builder class for convenient dialog construction
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        final View v = inflater.inflate(R.layout.import_list_item, null);
        editText = (EditText) v.findViewById(R.id.name);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Paste list string here")
                .setTitle("Import New List")
                .setView(v)
                .setPositiveButton("IMPORT", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        try {
                            IO.log("ImportListDialog", "Importing list from " + editText.getText().toString());
                            WLActivity.getLists().add(IO.readString(editText.getText().toString()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            IO.log("ImportListDialog", "Error reading list json");
                        }
                        IO.save(WLActivity.getLists());
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do nothing
                    }
                });
        return builder.create();
    }
}