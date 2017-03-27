package com.liamfruzyna.android.wishlister.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.liamfruzyna.android.wishlister.data.Data;
import com.liamfruzyna.android.wishlister.data.IO;
import com.liamfruzyna.android.wishlister.data.ListObj;
import com.liamfruzyna.android.wishlister.activities.SplashActivity;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by mail929 on 3/6/17.
 */

public class SignoutDialog extends DialogFragment
{
    ListObj list;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        list = Data.getCurrentList();
        String user = IO.getInstance().getString(IO.SERVER_USER_PREF);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure, you want to sign out from " + user)
                .setTitle("Sign Out: " + user)
                .setPositiveButton("SIGNOUT & RESET", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        SharedPreferences.Editor edit = IO.getInstance().getEditor();
                        edit.putString(IO.SERVER_ADDRESS_PREF, "");
                        edit.putString(IO.SERVER_USER_PREF, "");
                        edit.putString(IO.SERVER_PASSWORD_PREF, "");
                        edit.commit();

                        for(ListObj list : Data.getLists())
                        {
                            File file = new File(IO.fileDir, list.name + ".json");
                            file.delete();
                        }
                        Data.setLists(new ArrayList<ListObj>());

                        Intent intent = new Intent(getActivity(), SplashActivity.class);
                        startActivity(intent);
                    }
                })
                .setNeutralButton("SIGNOUT", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        SharedPreferences.Editor edit = IO.getInstance().getEditor();
                        edit.putString(IO.SERVER_ADDRESS_PREF, "");
                        edit.putString(IO.SERVER_USER_PREF, "");
                        edit.putString(IO.SERVER_PASSWORD_PREF, "");
                        edit.commit();
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