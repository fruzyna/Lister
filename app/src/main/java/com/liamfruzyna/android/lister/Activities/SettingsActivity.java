package com.liamfruzyna.android.lister.Activities;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
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
import android.widget.LinearLayout;

import com.liamfruzyna.android.lister.Data.IO;
import com.liamfruzyna.android.lister.DialogFragments.NewPasswordDialog;
import com.liamfruzyna.android.lister.DialogFragments.ShareListDialog;
import com.liamfruzyna.android.lister.DialogFragments.ImportListDialog;
import com.liamfruzyna.android.lister.DialogFragments.SortListsDialog;
import com.liamfruzyna.android.lister.DialogFragments.UnArchiveDialog;
import com.liamfruzyna.android.lister.R;

/**
 * Activity for customizing app settings.
 */
public class SettingsActivity extends PreferenceActivity
{
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        PreferenceScreen ps = getPreferenceManager().createPreferenceScreen(this);

        PreferenceCategory gen = new PreferenceCategory(this);
        gen.setTitle("Lists");
        ps.addPreference(gen);
/*
        //Button that clears all lists, it requires a restart for now
        Preference clear = new Preference(this);
        clear.setTitle("Clear Data");
        clear.setSummary("This may require app restart");
        clear.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                File file = new File(IO.fileDir, "data.json");
                file.delete();
                List<WishList> lists = WLActivity.getLists();
                lists = new ArrayList<>();
                return true;
            }
        });
        gen.addPreference(clear);*/

        //Allows user to sort lists  to their choosing
        Preference sort = new Preference(this);
        sort.setTitle("Sort Lists");
        sort.setSummary("Sort the order lists show up");
        sort.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                DialogFragment dialog = new SortListsDialog();
                dialog.show(getFragmentManager(), "");
                return true;
            }
        });
        gen.addPreference(sort);
        
        //Shares a list's data with the android share menu
        Preference share = new Preference(this);
        share.setTitle("Share List");
        share.setSummary("Share list with someone else");
        share.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                DialogFragment dialog = new ShareListDialog();
                dialog.show(getFragmentManager(), "");
                return true;
            }
        });
        gen.addPreference(share);

        //Prompts for a list's data and saves that
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

        //Prompts to choose a list to unarchive
        Preference unArchive = new Preference(this);
        unArchive.setTitle("Unarchive List");
        unArchive.setSummary("Unarchive a list so that it can be seen again");
        unArchive.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                DialogFragment dialog = new UnArchiveDialog();
                dialog.show(getFragmentManager(), "");
                return true;
            }
        });
        gen.addPreference(unArchive);

        //Prompts to set password for lists
        Preference password = new Preference(this);
        password.setTitle("Set Password");
        password.setSummary("Password protects lists on app launch");
        password.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                DialogFragment dialog = new NewPasswordDialog();
                dialog.show(getFragmentManager(), "");
                return true;
            }
        });
        gen.addPreference(password);

        PreferenceCategory item = new PreferenceCategory(this);
        item.setTitle("Items");
        ps.addPreference(item);

        //Whether or not to highlight items based off date
        Preference highlight = new CheckBoxPreference(this);
        highlight.setTitle("Highlight Items");
        highlight.setSummary("Highlight items based off their due dates");
        settings = getSharedPreferences(IO.PREFS, 0);
        ((CheckBoxPreference) highlight).setChecked(settings.getBoolean(IO.HIGHLIGHT_DATE_PREF, true));
        highlight.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(IO.HIGHLIGHT_DATE_PREF, ((CheckBoxPreference) preference).isChecked());
                editor.commit();
                return false;
            }
        });
        item.addPreference(highlight);

        PreferenceCategory about = new PreferenceCategory(this);
        about.setTitle("About");
        ps.addPreference(about);

        //The version number of the app
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

        //About me and a link to my site
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

        //sets up the view
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
