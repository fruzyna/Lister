package com.mail929.android.lister.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mail929.android.lister.data.DbConnection;
import com.mail929.android.lister.R;

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

    private class LoginTask extends AsyncTask<String, Void, Integer>
    {
        protected Integer doInBackground(String... login)
        {
            return DbConnection.login(login[0], login[1]);
        }

        protected void onPostExecute(Integer result)
        {
            if(result == 1)
            {
                Intent intent = new Intent(c, SplashActivity.class);
                startActivity(intent);
                finish();
            }
            else
            {
                Toast.makeText(c, DbConnection.responses[result], Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class CreateTask extends AsyncTask<String, Void, Integer>
    {
        protected Integer doInBackground(String... login)
        {
            return DbConnection.create(login[0], login[1]);
        }

        protected void onPostExecute(Integer result)
        {
            if(result == 1)
            {
                Intent intent = new Intent(c, SplashActivity.class);
                startActivity(intent);
                finish();
            }
            else
            {
                Toast.makeText(c, DbConnection.responses[result], Toast.LENGTH_SHORT).show();
            }
        }
    }
}
