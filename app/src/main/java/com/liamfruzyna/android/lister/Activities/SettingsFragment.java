package com.liamfruzyna.android.lister.Activities;

import android.app.DialogFragment;
import android.app.Fragment;
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
import android.preference.PreferenceFragment;
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
import android.view.ViewGroup;
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
public class SettingsFragment extends PreferenceFragment
{
    SharedPreferences settings;
    View view;

    @Override
    public View onCreateView(LayoutInflater infl, ViewGroup parent, Bundle savedInstanceState)
    {
        view = infl.inflate(R.layout.settings, parent, false);
        ((WLActivity) getActivity()).setUp(true);

        getActivity().setTitle("Settings");

        PreferenceScreen ps = getPreferenceManager().createPreferenceScreen(getActivity());

        PreferenceCategory gen = new PreferenceCategory(getActivity());
        gen.setTitle("Lists");
        ps.addPreference(gen);

        //Allows user to sort lists  to their choosing
        Preference sort = new Preference(getActivity());
        sort.setTitle("Sort Lists");
        sort.setSummary("Sort the order lists show up");
        sort.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                DialogFragment dialog = new SortListsDialog();
                dialog.show(getFragmentManager(), "");
                return true;
            }
        });
        gen.addPreference(sort);

        //Shares a list's data with the android share menu
        Preference share = new Preference(getActivity());
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
        Preference importList = new Preference(getActivity());
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
        Preference unArchive = new Preference(getActivity());
        unArchive.setTitle("Unarchive List");
        unArchive.setSummary("Unarchive a list so that it can be seen again");
        unArchive.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                DialogFragment dialog = new UnArchiveDialog();
                dialog.show(getFragmentManager(), "");
                return true;
            }
        });
        gen.addPreference(unArchive);

        PreferenceCategory item = new PreferenceCategory(getActivity());
        item.setTitle("Items");
        ps.addPreference(item);

        //Whether or not to highlight items based off date
        Preference highlight = new CheckBoxPreference(getActivity());
        highlight.setTitle("Highlight Items");
        highlight.setSummary("Highlight items based off their due dates");
        settings = getActivity().getSharedPreferences(IO.PREFS, 0);
        ((CheckBoxPreference) highlight).setChecked(settings.getBoolean(IO.HIGHLIGHT_DATE_PREF, true));
        highlight.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(IO.HIGHLIGHT_DATE_PREF, ((CheckBoxPreference) preference).isChecked());
                editor.commit();
                return false;
            }
        });
        item.addPreference(highlight);

        PreferenceCategory about = new PreferenceCategory(getActivity());
        about.setTitle("About");
        ps.addPreference(about);

        //The version number of the app
        Preference version = new Preference(getActivity());
        version.setTitle("App Version");
        try
        {
            version.setSummary(getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        about.addPreference(version);

        //About me and a link to my site
        Preference me = new Preference(getActivity());
        me.setTitle("2014-16 Liam Fruzyna");
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

        return view;
    }
}
