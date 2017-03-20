package com.liamfruzyna.android.wishlister.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.liamfruzyna.android.wishlister.data.Data;
import com.liamfruzyna.android.wishlister.data.IO;
import com.liamfruzyna.android.wishlister.R;

/**
 * Created by mail929 on 2/24/17.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    EditText username;
    EditText password;
    EditText server;
    Button login;
    Button create;
    Button skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
    }

    @Override
    public void onClick(View v) {
        String user = username.getText().toString();
        String pass = password.getText().toString();
        String serv = server.getText().toString();
        if(v == login) {
            String result = IO.getInstance().auth(user, pass, serv);
            if(result.equals("AUTH_SUCCESSFUL")) {
                SharedPreferences.Editor edit = IO.getInstance().getEditor();
                edit.putString(IO.SERVER_USER_PREF, user);
                edit.putString(IO.SERVER_PASSWORD_PREF, pass);
                edit.putString(IO.SERVER_ADDRESS_PREF, serv);
                edit.putBoolean(IO.FIRST_PREF, false);
                edit.commit();

                if(Data.getLists().size() > 0)
                {
                    IO.getInstance().saveAndSync();
                }

                Intent intent = new Intent(this, SplashActivity.class);
                startActivity(intent);
            }
            else if(result.equals("USER_NOT_FOUND")) {
                Toast.makeText(this, "User Not Found", Toast.LENGTH_SHORT).show();
            }
            else if(result.equals("WRONG_PASSWORD")) {
                Toast.makeText(this, "Wrong Password", Toast.LENGTH_SHORT).show();
            }
            else if(result.equals("ERROR")) {
                Toast.makeText(this, "Wrong Server", Toast.LENGTH_SHORT).show();
            }
        }
        else if(v == create) {
            String result = IO.getInstance().createAccount(user, pass, serv);
            if(result.equals("USER_CREATED")) {
                SharedPreferences.Editor edit = IO.getInstance().getEditor();
                edit.putString(IO.SERVER_USER_PREF, user);
                edit.putString(IO.SERVER_PASSWORD_PREF, pass);
                edit.putString(IO.SERVER_ADDRESS_PREF, serv);
                edit.putBoolean(IO.FIRST_PREF, false);
                edit.commit();

                if(Data.getLists().size() > 0)
                {
                    IO.getInstance().saveAndSync();
                }

                Intent intent = new Intent(this, SplashActivity.class);
                startActivity(intent);
            }
            else if(result.equals("USER_EXISTS")) {
                Toast.makeText(this, "User Exists", Toast.LENGTH_SHORT).show();
            }
            else if(result.equals("ERROR")) {
                Toast.makeText(this, "Wrong Server", Toast.LENGTH_SHORT).show();
            }
        }
        else if(v == skip) {
            SharedPreferences.Editor edit = IO.getInstance().getEditor();
            edit.putBoolean(IO.FIRST_PREF, false);
            edit.commit();
            Intent intent = new Intent(this, SplashActivity.class);
            startActivity(intent);
        }
    }
}
