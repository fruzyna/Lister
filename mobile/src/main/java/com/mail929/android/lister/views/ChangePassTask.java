package com.mail929.android.lister.views;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.mail929.android.lister.data.DbConnection;

/**
 * Created by mail929 on 1/24/18.
 */

public class ChangePassTask extends AsyncTask<String, Void, Boolean>
{
    Activity a;

    public ChangePassTask(Activity a)
    {
        this.a = a;
    }

    protected Boolean doInBackground(String... creds)
    {
        return DbConnection.changePass(creds[0], creds[1]);
    }

    protected void onPostExecute(Boolean success)
    {
        if(success)
        {
            Toast.makeText(a, "Password Changed", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(a, "Invalid Password", Toast.LENGTH_SHORT).show();
        }
    }
}
