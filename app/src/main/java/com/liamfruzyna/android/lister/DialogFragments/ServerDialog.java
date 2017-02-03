package com.liamfruzyna.android.lister.DialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.liamfruzyna.android.lister.Data.Data;
import com.liamfruzyna.android.lister.Data.IO;
import com.liamfruzyna.android.lister.Data.WishList;
import com.liamfruzyna.android.lister.Fragments.WLFragment;
import com.liamfruzyna.android.lister.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mail929 on 1/18/17.
 */

public class ServerDialog extends DialogFragment {

    EditText address;
    EditText user;
    EditText password;
    CheckBox sync;
    Button nameCheck;
    TextView exists;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.dialog_edit_server, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        address = (EditText) v.findViewById(R.id.address);
        user = (EditText) v.findViewById(R.id.user);
        password = (EditText) v.findViewById(R.id.password);
        sync = (CheckBox) v.findViewById(R.id.sync);
        nameCheck = (Button) v.findViewById(R.id.namecheck);
        exists = (TextView) v.findViewById(R.id.exists);

        prefs = getActivity().getSharedPreferences(IO.PREFS, Context.MODE_PRIVATE);
        editor = prefs.edit();

        address.setText(prefs.getString(IO.SERVER_ADDRESS_PREF, ""));
        user.setText(prefs.getString(IO.SERVER_USER_PREF, ""));
        password.setText(prefs.getString(IO.SERVER_PASSWORD_PREF, ""));
        sync.setChecked(prefs.getBoolean(IO.SERVER_PREF, false));

        nameCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DownloadFilesTask().execute(address.getText().toString(), user.getText().toString(), password.getText().toString());
            }
        });

        //setup dialog
        builder.setMessage("Enter information for a new server connection, sync will apply on app restart")
                .setTitle("Add a Server")
                .setView(v)
                .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        editor.putString(IO.SERVER_ADDRESS_PREF, address.getText().toString());
                        editor.putString(IO.SERVER_USER_PREF, user.getText().toString());
                        editor.putString(IO.SERVER_PASSWORD_PREF, password.getText().toString());
                        editor.putBoolean(IO.SERVER_PREF, sync.isChecked());
                        editor.commit();
                    }
                })
                .setNegativeButton("BACK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do nothing
                    }
                });
        return builder.create();
    }

    private class DownloadFilesTask extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL("http://" + urls[0] + "/createUser/?user=" + urls[1] + "&password=" + urls[2]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    byte[] contents = new byte[1024];

                    int bytesRead = 0;
                    String result = "";
                    while ((bytesRead = in.read(contents)) != -1) {
                        result += new String(contents, 0, bytesRead);
                    }

                    return result;

                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "ERROR";
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(String result) {
            exists.setText(result);
        }
    }

}