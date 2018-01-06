package com.liamfruzyna.android.wishlister.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.liamfruzyna.android.wishlister.data.Data;
import com.liamfruzyna.android.wishlister.data.DbConnection;
import com.liamfruzyna.android.wishlister.data.IO;
import com.liamfruzyna.android.wishlister.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mail929 on 2/24/17.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{
    EditText username;
    EditText password;
    EditText server;
    Button login;
    Button create;
    Button skip;

    Context c;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        server = findViewById(R.id.server);
        login = findViewById(R.id.login);
        create = findViewById(R.id.create);
        skip = findViewById(R.id.skip);

        login.setOnClickListener(this);
        create.setOnClickListener(this);
        skip.setOnClickListener(this);

        c = this;
    }

    @Override
    public void onClick(View v)
    {
        String user = username.getText().toString();
        String pass = password.getText().toString();
        if(v.equals(login))
        {
            (new LoginTask()).execute(user, pass);
        }
        else if(v.equals(create))
        {
            (new CreateTask()).execute(user, pass);
        }
        else if(v.equals(skip))
        {
            Intent intent = new Intent(this, SplashActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private class LoginTask extends AsyncTask<String, Void, String>
    {
        protected String doInBackground(String... login)
        {
            return DbConnection.login(login[0], login[1]);
        }

        protected void onPostExecute(String result)
        {
            if(result.equals("Successful Login"))
            {
                Intent intent = new Intent(c, SplashActivity.class);
                startActivity(intent);
                finish();
            }
            else
            {
                Toast.makeText(c, result, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class CreateTask extends AsyncTask<String, Void, String>
    {
        protected String doInBackground(String... login)
        {
            return DbConnection.create(login[0], login[1]);
        }

        protected void onPostExecute(String result)
        {
            if(result.equals("Successful Login"))
            {
                Intent intent = new Intent(c, SplashActivity.class);
                startActivity(intent);
                finish();
            }
            else
            {
                Toast.makeText(c, result, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
