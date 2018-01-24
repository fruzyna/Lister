package com.mail929.android.lister.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mail929.android.lister.R;
import com.mail929.android.lister.data.DbConnection;

/**
 * Created by mail929 on 1/24/18.
 */

public class ChangePassDialog extends DialogFragment
{
    LayoutInflater inflater;

    EditText oldPass;
    EditText newPass;
    EditText checkPass;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // Use the Builder class for convenient dialog construction
        inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_change_pass, null);

        oldPass = view.findViewById(R.id.pass_edit_old);
        newPass = view.findViewById(R.id.pass_edit_new);
        checkPass = view.findViewById(R.id.pass_edit_check);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Enter your current password and new desired password to change account password.")
                .setTitle("Change Password")
                .setView(view)
                .setPositiveButton("CHANGE", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        if(newPass.getText().toString().equals(checkPass.getText().toString()))
                        {
                            (new ChangePassTask(getActivity())).execute(newPass.getText().toString(), oldPass.getText().toString());
                        }
                        else
                        {
                            Toast.makeText(getActivity(), "Passwords must match", Toast.LENGTH_SHORT).show();
                        }
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