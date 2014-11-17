package com.liamfruzyna.android.wishlister;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Activity for customizing app settings.
 */
public class SettingsActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        LinearLayout layout = (LinearLayout) findViewById(R.id.settings);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.subheader, null);
        layout.addView(v);
        TextView tv = (TextView) v.findViewById(R.id.textView);
        tv.setText("About");

        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.settings_text_item, null);
        layout.addView(v);
        TextView number = (TextView) v.findViewById(R.id.big);
        TextView description = (TextView) v.findViewById(R.id.little);
        number.setText("Lister Version 1.1.0");
        description.setText("Settings, Remove Items, UI Tweaks");

        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.settings_text_item, null);
        layout.addView(v);
        TextView by = (TextView) v.findViewById(R.id.big);
        TextView link = (TextView) v.findViewById(R.id.little);
        by.setText("2014 Liam Fruzyna");
        link.setText("liamfruzyna.com");
    }
}
