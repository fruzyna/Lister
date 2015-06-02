package com.liamfruzyna.android.lister.Activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liamfruzyna.android.lister.Data.DataContainer;
import com.liamfruzyna.android.lister.Data.IO;
import com.liamfruzyna.android.lister.R;

import org.json.JSONException;

import java.io.File;
import java.net.MalformedURLException;

/**
 * Activity for customizing app settings.
 */
public class SettingsActivity extends Activity
{
    View v;
    CheckBox cb;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        LinearLayout layout = (LinearLayout) findViewById(R.id.settings);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.subheader, null);
        layout.addView(v);
        TextView tv = (TextView) v.findViewById(R.id.textView);
        tv.setText("General");

        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.settings_text_item, null);
        layout.addView(v);
        TextView by = (TextView) v.findViewById(R.id.big);
        TextView link = (TextView) v.findViewById(R.id.little);
        by.setText("Clear Data");
        link.setText("delete the save file for data (may require app restart)");
        v.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                File file = new File(DataContainer.dir, "data.json");
                file.delete();
                try
                {
                    IO.load();
                } catch (JSONException e)
                {
                    e.printStackTrace();
                } catch (MalformedURLException e)
                {
                    e.printStackTrace();
                }
            }
        });

        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.subheader, null);
        layout.addView(v);
        tv = (TextView) v.findViewById(R.id.textView);
        tv.setText("About");

        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.settings_text_item, null);
        layout.addView(v);
        TextView number = (TextView) v.findViewById(R.id.big);
        TextView description = (TextView) v.findViewById(R.id.little);
        number.setText("Lister Version 1.7.0");
        description.setText("Replaced Archiving with Editing");

        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.settings_text_item, null);
        layout.addView(v);
        by = (TextView) v.findViewById(R.id.big);
        link = (TextView) v.findViewById(R.id.little);
        by.setText("2014 Liam Fruzyna/mail929");
        link.setText("liamfruzyna.com");
        v.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String url = "http://liamfruzyna.com";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }
}
