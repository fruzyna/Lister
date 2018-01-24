package com.mail929.android.lister.activities;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.mail929.android.lister.data.Data;
import com.mail929.android.lister.R;
import com.mail929.android.lister.data.DbConnection;
import com.mail929.android.lister.data.IO;
import com.mail929.android.lister.data.Item;
import com.mail929.android.lister.data.ListObj;
import com.mail929.android.lister.views.EditItemView;
import com.mail929.android.lister.views.ItemView;
import com.mail929.android.lister.views.ListSettingsDialog;
import com.mail929.android.lister.views.NewListDialog;
import com.mail929.android.lister.views.RemoveListDialog;
import com.mail929.android.lister.views.ShareListDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mail929 on 2/24/17.
 */

public class ListerActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, AdapterView.OnItemSelectedListener
{
    //Views
    Spinner listSpinner;
    ImageView settings;
    ImageView remove;
    ImageView archive;
    ImageView share;

    LinearLayout listLayout;

    List<ItemView> items;
    View addItem;

    View newItem;
    EditText text;
    Button cancel;
    Button add;
    boolean isNewItem;

    EditItemView editItem;
    int editId;

    FloatingActionButton fab;

    Context c;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lister);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        c = this;

        IO.firstInstance(this);
        (new CheckLogin()).execute();
    }

    public void setup()
    {
        isNewItem = false;
        editId = -1;

        listSpinner = findViewById(R.id.list_spinner_lists);
        listLayout = findViewById(R.id.list_layout_items);

        remove = findViewById(R.id.list_button_remove);
        remove.setOnClickListener(this);
        archive = findViewById(R.id.list_button_archive);
        archive.setOnClickListener(this);
        share = findViewById(R.id.list_button_share);
        share.setOnClickListener(this);
        settings = findViewById(R.id.list_button_settings);
        settings.setOnClickListener(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, Data.getNames());
        listSpinner.setAdapter(adapter);
        listSpinner.setSelection(Data.getCurrentListPos());
        listSpinner.setOnItemSelectedListener(this);

        if(Data.getNames().size() == 0)
        {
            listLayout.removeAllViews();
        }

        fab = findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_add_24dp);
        fab.setOnClickListener(this);
    }

    public void buildList()
    {
        System.out.println("Building list");

        Data.setCurrentList(Data.getListFromName((String) listSpinner.getSelectedItem()).getId());
        ListObj list = Data.getCurrentList();
        items = new ArrayList<>();
        listLayout.removeAllViews();

        List<Item> listItems = list.getItems();
        if(listItems.size() == 0 && !isNewItem)
        {
            View noItems = getLayoutInflater().inflate(R.layout.no_items, listLayout, false);
            listLayout.addView(noItems);
        }

        for (Item item : listItems)
        {
            System.out.println("Adding item: " + item.getItem());
            View itemView;
            if(editId == item.getId())
            {
                itemView = getLayoutInflater().inflate(R.layout.edit_item, listLayout, false);
                items.add(editItem = new EditItemView(item, itemView, this));
            }
            else
            {
                itemView = getLayoutInflater().inflate(R.layout.list_item, listLayout, false);
                ItemView i = new ItemView(item, (SwipeLayout) itemView);
                i.getLayout().setOnLongClickListener(this);
                items.add(i);
            }
            listLayout.addView(itemView);

            if(listItems.indexOf(item) != listItems.size() - 1 || isNewItem)
            {
                View divider = getLayoutInflater().inflate(R.layout.divider, listLayout, false);
                listLayout.addView(divider);
            }
        }

        if(isNewItem)
        {
            newItem = getLayoutInflater().inflate(R.layout.new_item, listLayout, false);
            text = newItem.findViewById(R.id.new_text_item);
            cancel = newItem.findViewById(R.id.new_button_cancel);
            add = newItem.findViewById(R.id.new_button_add);

            cancel.setOnClickListener(this);
            add.setOnClickListener(this);
            listLayout.addView(newItem);
        }
        else
        {
            if(list.getPerm() == 'r')
            {
                settings.setVisibility(View.GONE);
            }
            else
            {
                addItem = getLayoutInflater().inflate(R.layout.button_add_item, listLayout, false);
                addItem.setOnClickListener(this);
                listLayout.addView(addItem);
                settings.setVisibility(View.VISIBLE);
            }

            if(list.getPerm() == 'w')
            {
            }

            if(list.getPerm() == 'o')
            {
                //remove.setVisibility(View.VISIBLE);
                archive.setVisibility(View.VISIBLE);
                share.setVisibility(View.VISIBLE);
            }
            else
            {
                //remove.setVisibility(View.GONE);
                archive.setVisibility(View.GONE);
                share.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        System.out.println("Options selected");
        switch (item.getItemId())
        {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onClick(View view)
    {
        ListObj list = Data.getCurrentList();
        if(view.equals(fab))
        {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            NewListDialog f = new NewListDialog();
            f.show(ft, "dialog");
            IO.ready = false;
            (new NewListTask()).execute();
        }
        else if(list != null)
        {
            if(view.equals(remove))
            {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                RemoveListDialog f = new RemoveListDialog();
                Bundle bundle = new Bundle();
                bundle.putString("list", Data.getCurrentList().getName());
                f.setArguments(bundle);
                f.show(ft, "dialog");
                (new NewListTask()).execute();
            }
            else if(view.equals(archive))
            {
                list.setArchived(true);
                (new ArchiveListTask()).execute();
                Toast.makeText(this, list.getName() + " archived!", Toast.LENGTH_SHORT).show();
            }
            else if(view.equals(share))
            {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ShareListDialog f = new ShareListDialog();
                Bundle bundle = new Bundle();
                bundle.putString("list", Data.getCurrentList().getName());
                f.setArguments(bundle);
                f.show(ft, "dialog");
            }
            else if(view.equals(settings))
            {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ListSettingsDialog f = new ListSettingsDialog();
                Bundle bundle = new Bundle();
                bundle.putString("list", Data.getCurrentList().getName());
                f.setArguments(bundle);
                f.show(ft, "dialog");
                IO.ready = false;
                (new ListSettingsTask()).execute();
            }
            else if(view.equals(add))
            {
                (new AddItemTask()).execute();
            }
            else if(view.equals(addItem))
            {
                editId = -1;
                isNewItem = true;
                buildList();
            }
            else if(view.equals(cancel))
            {
                isNewItem = false;
                buildList();
            }
            else
            {
                switch(view.getId())
                {
                    case R.id.edit_button_cancel:
                        editId = -1;
                        buildList();
                        break;
                    case R.id.edit_button_remove:
                        (new RemoveItemTask()).execute();
                        break;
                    case R.id.edit_button_save:
                        (new EditItemTask()).execute();
                        break;
                }
            }
        }
    }

    @Override
    public boolean onLongClick(View view)
    {
        if(Data.getCurrentList().getPerm() == 'r')
        {
            Toast.makeText(this, "You cannot edit items on this list", Toast.LENGTH_LONG).show();
        }
        else
        {
            for(ItemView item : items)
            {
                if(item.getLayout().equals(view))
                {
                    editId = item.getItem().getId();
                    buildList();
                    isNewItem = false;
                }
            }
        }
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
        buildList();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView)
    {
        listLayout.removeAllViews();
    }

    /*
        AsyncTasks
     */

    private class AddItemTask extends AsyncTask<Void, Void, Void>
    {
        protected Void doInBackground(Void... na)
        {
            DbConnection.addItem(text.getText().toString(), Data.getCurrentList());
            DbConnection.pullList(Data.getCurrentList().getId());
            return null;
        }

        protected void onPostExecute(Void na)
        {
            isNewItem = false;
            buildList();
        }
    }

    public class RemoveItemTask extends AsyncTask<Void, Void, Void>
    {
        protected Void doInBackground(Void... na)
        {
            DbConnection.removeItem(editItem.getItem());
            DbConnection.pullList(Data.getCurrentList().getId());
            return null;
        }

        protected void onPostExecute(Void na)
        {
            buildList();
        }
    }

    private class RefreshTask extends AsyncTask<Void, Void, Void>
    {
        protected Void doInBackground(Void... na)
        {
            DbConnection.pullLists();
            return null;
        }

        protected void onPostExecute(Void na)
        {
            setup();
        }
    }

    private class ArchiveListTask extends AsyncTask<Void, Void, Void>
    {
        protected Void doInBackground(Void... na)
        {
            DbConnection.archiveList(Data.getCurrentList());
            DbConnection.pullLists();
            return null;
        }

        protected void onPostExecute(Void na)
        {
            setup();
        }
    }

    private class EditItemTask extends AsyncTask<Void, Void, Void>
    {
        protected Void doInBackground(Void... na)
        {
            editItem.save();
            return null;
        }

        protected void onPostExecute(Void na)
        {
            editId = -1;
            buildList();
        }
    }

    private class NewListTask extends AsyncTask<Void, Void, Void>
    {
        protected Void doInBackground(Void... na)
        {
            while(!IO.ready);
            return null;
        }

        protected void onPostExecute(Void na)
        {
            setup();
        }
    }

    private class ListSettingsTask extends AsyncTask<Void, Void, Void>
    {
        protected Void doInBackground(Void... na)
        {
            while(!IO.ready);
            return null;
        }

        protected void onPostExecute(Void na)
        {
            setup();
        }
    }

    private class CheckLogin extends AsyncTask<Void, Void, Boolean>
    {
        protected Boolean doInBackground(Void... na)
        {
            int result = DbConnection.loginStatus();
            if(result == 1) //attempt login with cookies
            {
                DbConnection.pullLists();
                return true;
            }
            else if(result == 4)
            {
                DbConnection.queryCache();
                return true;
            }
            else if(!IO.getInstance().getString(IO.SERVER_USER_PREF).equals("") && !IO.getInstance().getString(IO.SERVER_PASS_PREF).equals("")) //if there are saved credentials
            {
                //attempt login with saved credentials
                result = DbConnection.login(IO.getInstance().getString(IO.SERVER_USER_PREF), IO.getInstance().getString(IO.SERVER_PASS_PREF));

                if(result == 1 || result == 4)
                {
                    if(result == 1)
                    {
                        DbConnection.queryCache();
                    }
                    DbConnection.pullLists();
                    return true;
                }
            }
            return false;
        }

        protected void onPostExecute(Boolean success)
        {
            if(success)
            {
                setup();
            }
            else
            {
                //if we make it this far request login
                Intent intent = new Intent(c, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }
}