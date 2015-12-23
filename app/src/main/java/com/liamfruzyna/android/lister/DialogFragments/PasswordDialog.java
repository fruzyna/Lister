package com.liamfruzyna.android.lister.DialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.liamfruzyna.android.lister.Data.IO;
import com.liamfruzyna.android.lister.R;

/**
 * Created by mail929 on 12/22/15.
 */
public class PasswordDialog  extends DialogFragment {
    SharedPreferences settings;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        final View v = inflater.inflate(R.layout.new_password_item, null);
        settings = getActivity().getSharedPreferences(IO.PREFS, 0);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Enter in the password to continue.")
                .setTitle("Enter Password")
                .setView(v)
                .setPositiveButton("UNLOCK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditText password = (EditText) v.findViewById(R.id.password);
                        if(password.getText().toString().equals(settings.getString(IO.PASSWORD_PREF, "jkj;kfjkfe;lkjrn")))
                        {
                        }
                        else
                        {
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                        }
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                });
        return builder.create();
    }
}