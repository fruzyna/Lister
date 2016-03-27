package com.liamfruzyna.android.lister.DialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.liamfruzyna.android.lister.Data.IO;
import com.liamfruzyna.android.lister.R;

/**
 * Created by mail929 on 3/26/16.
 */
public class EditServerDialog extends DialogFragment
{
    EditText address;
    EditText user;
    EditText password;
    EditText dir;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        final View v = inflater.inflate(R.layout.dialog_edit_server, null);

        final SharedPreferences settings = getActivity().getSharedPreferences(IO.PREFS, 0);
        address = (EditText) v.findViewById(R.id.address);
        address.setText(settings.getString(IO.SERVER_ADDRESS_PREF, "none"));
        user = (EditText) v.findViewById(R.id.user);
        user.setText(settings.getString(IO.SERVER_USER_PREF, "none"));
        password = (EditText) v.findViewById(R.id.password);
        password.setText(settings.getString(IO.SERVER_PASSWORD_PREF, "none"));
        dir = (EditText) v.findViewById(R.id.dir);
        dir.setText(settings.getString(IO.SERVER_DIR_PREF, "none/Lists"));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Edit server address, username, and password")
                .setTitle("Edit Server Info")
                .setView(v)
                .setPositiveButton("APPEND", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(IO.SERVER_ADDRESS_PREF, address.getText().toString());
                        editor.putString(IO.SERVER_USER_PREF, user.getText().toString());
                        editor.putString(IO.SERVER_PASSWORD_PREF, password.getText().toString());
                        editor.putString(IO.SERVER_DIR_PREF, dir.getText().toString());
                        editor.commit();
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
