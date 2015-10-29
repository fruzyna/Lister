package com.liamfruzyna.android.lister.Activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liamfruzyna.android.lister.Data.IO;
import com.liamfruzyna.android.lister.Data.WishList;
import com.liamfruzyna.android.lister.DialogFragments.ChooseListDialog;
import com.liamfruzyna.android.lister.DialogFragments.EditItemDialog;
import com.liamfruzyna.android.lister.DialogFragments.ImportListDialog;
import com.liamfruzyna.android.lister.R;

import org.json.JSONException;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;

/**
 * Activity for customizing app settings.
 */
public class SettingsActivity extends PreferenceActivity
{
    View v;
    CheckBox cb;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        PreferenceScreen ps = getPreferenceManager().createPreferenceScreen(this);

        PreferenceCategory gen = new PreferenceCategory(this);
        gen.setTitle("General");
        ps.addPreference(gen);

        Preference clear = new Preference(this);
        clear.setTitle("Clear Data");
        clear.setSummary("This may require app restart");
        clear.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                File file = new File(IO.fileDir, "data.json");
                file.delete();
                WLActivity.lists = new ArrayList<>();
                return true;
            }
        });
        gen.addPreference(clear);

        Preference share = new Preference(this);
        share.setTitle("Share List");
        share.setSummary("Share list with someone else");
        share.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                DialogFragment dialog = new ChooseListDialog();
                dialog.show(getFragmentManager(), "");
                return true;
            }
        });
        gen.addPreference(share);

        Preference importList = new Preference(this);
        importList.setTitle("Import List");
        importList.setSummary("Import list from someone else");
        importList.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                DialogFragment dialog = new ImportListDialog();
                dialog.show(getFragmentManager(), "");
                return true;
            }
        });
        gen.addPreference(importList);

        PreferenceCategory about = new PreferenceCategory(this);
        about.setTitle("About");
        ps.addPreference(about);

        Preference version = new Preference(this);
        version.setTitle("App Version");
        try
        {
            version.setSummary(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        about.addPreference(version);

        Preference me = new Preference(this);
        me.setTitle("2015 Liam Fruzyna");
        me.setSummary("mail929.com");
        me.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://mail929.com"));
                startActivity(browserIntent);
                return true;
            }
        });
        about.addPreference(me);

        setPreferenceScreen(ps);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);

        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
        bar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        bar.setTranslationZ(8);
        root.addView(bar, 0);
        bar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs)
    {
        // Allow super to try and create a view first
        final View result = super.onCreateView(name, context, attrs);
        if (result != null)
        {
            return result;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            // If we're running pre-L, we need to 'inject' our tint aware Views in place of the
            // standard framework versions
            switch (name) {
                case "EditText":
                    return new AppCompatEditText(this, attrs);
                case "Spinner":
                    return new AppCompatSpinner(this, attrs);
                case "CheckBox":
                    return new AppCompatCheckBox(this, attrs);
                case "RadioButton":
                    return new AppCompatRadioButton(this, attrs);
                case "CheckedTextView":
                    return new AppCompatCheckedTextView(this, attrs);
            }
        }

        return null;
    }
}
