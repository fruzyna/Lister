package com.liamfruzyna.android.lister.DialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.liamfruzyna.android.lister.Activities.WLActivity;
import com.liamfruzyna.android.lister.Data.IO;
import com.liamfruzyna.android.lister.Data.WishList;
import com.liamfruzyna.android.lister.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mail929 on 12/22/15.
 */
public class NewPasswordDialog extends DialogFragment {
    SharedPreferences settings;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        final View v = inflater.inflate(R.layout.new_password_item, null);
        settings = getActivity().getSharedPreferences(IO.PREFS, 0);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Enter in the desired password.")
                .setTitle("Set Password")
                .setView(v)
                .setPositiveButton("SET", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditText password = (EditText) v.findViewById(R.id.password);

                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(IO.PASSWORD_PREF, password.getText().toString());
                        editor.putBoolean(IO.HAS_PASSWORD_PREF, true);
                        editor.commit();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNeutralButton("REMOVE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean(IO.HAS_PASSWORD_PREF, false);
                        editor.commit();
                    }
                });
        return builder.create();
    }
}