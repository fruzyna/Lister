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

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        server = (EditText) findViewById(R.id.server);
        login = (Button) findViewById(R.id.login);
        create = (Button) findViewById(R.id.create);
        skip = (Button) findViewById(R.id.skip);

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

    private class LoginTask extends AsyncTask<String, Void, Object>
    {
        protected Object doInBackground(String... login)
        {
            String query = "login/?user=" + login[0] + "&pass=" + login[1];
            return DbConnection.runQuery(query);
        }

        protected void onPostExecute(Object result)
        {
            if(result instanceof String)
            {
                String response = (String) result;
                if(response.equals("Network Failure"))
                {
                    System.out.println("Failed to connect to server");
                    Toast.makeText(c, "Network Error", Toast.LENGTH_SHORT).show();
                }
                else if(response.equals("Success"))
                {
                    System.out.println("Successful Login");
                    Intent intent = new Intent(c, SplashActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if(response.equals("Failure"))
                {
                    Toast.makeText(c, "Incorrect Login", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    System.out.println("Unknown result : " + response);
                    Toast.makeText(c, "Unknown Error", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Map<String, Object> data = (HashMap<String, Object>) result;
                System.out.println("Table of length " + data.size() + " returned");
                Toast.makeText(c, "Unknown Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class CreateTask extends AsyncTask<String, Void, Object>
    {
        protected Object doInBackground(String... login)
        {
            String query = "createuser/?user=" + login[0] + "&pass=" + login[1];
            return DbConnection.runQuery(query);
        }

        protected void onPostExecute(Object result)
        {
            if(result instanceof String)
            {
                String response = (String) result;
                if(response.equals("Network Failure"))
                {
                    System.out.println("Failed to connect to server");
                    Toast.makeText(c, "Network Error", Toast.LENGTH_SHORT).show();
                }
                else if(response.equals("Success"))
                {
                    System.out.println("Successful Login");
                    Intent intent = new Intent(c, SplashActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if(response.equals("User Already Exists!"))
                {
                    Toast.makeText(c, "User Already Exists!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    System.out.println("Unknown result : " + response);
                    Toast.makeText(c, "Unknown Error", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Map<String, Object> data = (HashMap<String, Object>) result;
                System.out.println("Table of length " + data.size() + " returned");
                Toast.makeText(c, "Unknown Error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
