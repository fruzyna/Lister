package com.liamfruzyna.android.wishlister.activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.liamfruzyna.android.wishlister.data.Data;
import com.liamfruzyna.android.wishlister.data.IO;
import com.liamfruzyna.android.wishlister.views.UnarchiveListDialog;

/**
 * Created by mail929 on 3/3/17.
 */

public class SettingsActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    public static class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener
    {
        Preference unArchive;
        Preference signout;
        Preference highlight;
        Preference highlightWhole;
        Preference dateFormat;
        Preference datesAsDay;
        Preference datesAsDays;
        Preference version;
        Preference me;

        @Override
        public boolean onPreferenceClick(Preference preference)
        {
            if(preference.equals(unArchive))
            {
                if(Data.getArchived().size() == 0)
                {
                    Toast.makeText(getActivity(), "No archived lists!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    DialogFragment dialog = new UnarchiveListDialog();
                    dialog.show(getFragmentManager(), "");
                }
            }
            else if(preference.equals(signout))
            {
                if(IO.getInstance().getString(IO.SERVER_ADDRESS_PREF).equals("") || IO.getInstance().getString(IO.SERVER_USER_PREF).equals(""))
                {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
                else
                {
                    /*DialogFragment dialog = new SignoutDialog();
                    dialog.show(getFragmentManager(), "");*/
                    return true;
                }
            }
            else if(preference.equals(highlight))
            {
                SharedPreferences.Editor editor = IO.getInstance().getEditor();
                editor.putBoolean(IO.HIGHLIGHT_DATE_PREF, ((CheckBoxPreference) preference).isChecked());
                editor.commit();
                return false;
            }
            else if(preference.equals(highlightWhole))
            {
                SharedPreferences.Editor editor = IO.getInstance().getEditor();
                editor.putBoolean(IO.HIGHLIGHT_WHOLE_ITEM_PREF, ((CheckBoxPreference) preference).isChecked());
                editor.commit();
                return false;
            }
            else if(preference.equals(dateFormat))
            {
                SharedPreferences.Editor editor = IO.getInstance().getEditor();
                editor.putBoolean(IO.US_DATE_FORMAT_PREF, ((CheckBoxPreference) preference).isChecked());
                editor.commit();
                return false;
            }
            else if(preference.equals(datesAsDay))
            {
                SharedPreferences.Editor editor = IO.getInstance().getEditor();
                editor.putBoolean(IO.DATES_AS_DAY, ((CheckBoxPreference) preference).isChecked());
                editor.commit();
                return false;
            }
            else if(preference.equals(datesAsDays))
            {
                SharedPreferences.Editor editor = IO.getInstance().getEditor();
                editor.putBoolean(IO.DATES_AS_DAYS_UNTIL, ((CheckBoxPreference) preference).isChecked());
                editor.commit();
                return false;
            }
            else if(preference.equals(version))
            {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://github.com/mail929/Lister"));
                startActivity(browserIntent);
                return true;
            }
            else if(preference.equals(me))
            {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://mail929.com"));
                startActivity(browserIntent);
                return true;
            }
            return true;
        }

        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);

            PreferenceScreen ps = getPreferenceManager().createPreferenceScreen(getActivity());

            PreferenceCategory gen = new PreferenceCategory(getActivity());
            gen.setTitle("Lists");
            ps.addPreference(gen);

            //Prompts to choose a list to unarchive
            unArchive = new Preference(getActivity());
            unArchive.setTitle("Unarchive List");
            unArchive.setSummary("Unarchive a list so that it can be seen again");
            unArchive.setOnPreferenceClickListener(this);
            gen.addPreference(unArchive);

            //Prompts to signout
            signout = new Preference(getActivity());
            if(IO.getInstance().getString(IO.SERVER_ADDRESS_PREF).equals("") || IO.getInstance().getString(IO.SERVER_USER_PREF).equals(""))
            {
                signout.setTitle("Sign In");
                signout.setSummary("No user currently logged in");
            }
            else
            {
                signout.setTitle("Sign Out");
                signout.setSummary("Currently logged in to " + IO.getInstance().getString(IO.SERVER_USER_PREF));
            }
            signout.setOnPreferenceClickListener(this);
            gen.addPreference(signout);

            PreferenceCategory item = new PreferenceCategory(getActivity());
            item.setTitle("Items");
            ps.addPreference(item);

            //Whether or not to highlight items based off date
            highlight = new CheckBoxPreference(getActivity());
            highlight.setTitle("Highlight Items");
            highlight.setSummary("Highlight items based off their due dates");
            ((CheckBoxPreference) highlight).setChecked(IO.getInstance().getBoolean(IO.HIGHLIGHT_DATE_PREF, true));
            highlight.setOnPreferenceClickListener(this);
            item.addPreference(highlight);

            //Whether or not to highlight whole items
            highlightWhole = new CheckBoxPreference(getActivity());
            highlightWhole.setTitle("Highlight Whole Items");
            highlightWhole.setSummary("Highlight entire item instead of just tag");
            ((CheckBoxPreference) highlightWhole).setChecked(IO.getInstance().getBoolean(IO.HIGHLIGHT_WHOLE_ITEM_PREF, true));
            highlightWhole.setOnPreferenceClickListener(this);
            item.addPreference(highlightWhole);

            //Use US date format
            dateFormat = new CheckBoxPreference(getActivity());
            dateFormat.setTitle("Use US date format");
            dateFormat.setSummary("MM/DD or DD/MM");
            ((CheckBoxPreference) dateFormat).setChecked(IO.getInstance().getBoolean(IO.US_DATE_FORMAT_PREF, true));
            dateFormat.setOnPreferenceClickListener(this);
            item.addPreference(dateFormat);

            //Display dates as day of the week
            datesAsDay = new CheckBoxPreference(getActivity());
            datesAsDay.setTitle("Display dates as day of week");
            datesAsDay.setSummary("Within 7 days of current day");
            ((CheckBoxPreference) datesAsDay).setChecked(IO.getInstance().getBoolean(IO.DATES_AS_DAY, true));
            datesAsDay.setOnPreferenceClickListener(this);
            item.addPreference(datesAsDay);

            //Display dates as days until
            datesAsDays = new CheckBoxPreference(getActivity());
            datesAsDays.setTitle("Display dates as days until due");
            datesAsDays.setSummary("If displaying as day of week, will display as days for days more 7 days away");
            ((CheckBoxPreference) datesAsDays).setChecked(IO.getInstance().getBoolean(IO.DATES_AS_DAYS_UNTIL, false));
            datesAsDays.setOnPreferenceClickListener(this);
            item.addPreference(datesAsDays);


            PreferenceCategory about = new PreferenceCategory(getActivity());
            about.setTitle("About");
            ps.addPreference(about);

            //The version number of the app
            version = new Preference(getActivity());
            version.setTitle("App Version");
            try
            {
                version.setSummary(getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName);
            } catch (PackageManager.NameNotFoundException e)
            {
                e.printStackTrace();
            }
            version.setOnPreferenceClickListener(this);
            about.addPreference(version);

            //About me and a link to my site
            me = new Preference(getActivity());
            me.setTitle("2014-18 Liam Fruzyna");
            me.setSummary("mail929.com");
            me.setOnPreferenceClickListener(this);
            about.addPreference(me);

            setPreferenceScreen(ps);
        }
    }

}
