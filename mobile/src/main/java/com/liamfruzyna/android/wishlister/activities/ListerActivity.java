package com.liamfruzyna.android.wishlister.activities;

import android.app.FragmentTransaction;
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

import com.liamfruzyna.android.wishlister.data.Data;
import com.liamfruzyna.android.wishlister.R;
import com.liamfruzyna.android.wishlister.data.DbConnection;
import com.liamfruzyna.android.wishlister.data.Item;
import com.liamfruzyna.android.wishlister.data.ListObj;
import com.liamfruzyna.android.wishlister.views.EditItemView;
import com.liamfruzyna.android.wishlister.views.ItemView;
import com.liamfruzyna.android.wishlister.views.NewListDialog;
import com.liamfruzyna.android.wishlister.views.RemoveListDialog;
import com.liamfruzyna.android.wishlister.views.ShareListDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mail929 on 2/24/17.
 */

public class ListerActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, AdapterView.OnItemSelectedListener
{
    //Views
    Spinner listSpinner;
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

        (new RefreshTask()).execute();
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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, Data.getNames());
        listSpinner.setAdapter(adapter);
        listSpinner.setSelection(Data.getCurrentListPos());
        listSpinner.setOnItemSelectedListener(this);

        if(Data.getNames().size() == 0)
        {
            listLayout.removeAllViews();
        }

        fab = findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_add_white);
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
                itemView.setOnLongClickListener(this);
                items.add(new ItemView(item, itemView));
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
            }
            else
            {
                addItem = getLayoutInflater().inflate(R.layout.button_add_item, listLayout, false);
                addItem.setOnClickListener(this);
                listLayout.addView(addItem);
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
        switch (item.getItemId()) {
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
        if(view.equals(addItem))
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
        else if(view.equals(add))
        {
            (new AddItemTask()).execute();
        }
        else if(view.equals(fab))
        {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            NewListDialog f = new NewListDialog();
            f.show(ft, "dialog");
            (new NewListTask()).execute();
        }
        else if(view.equals(remove))
        {
            if(Data.getCurrentList() != null)
            {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                RemoveListDialog f = new RemoveListDialog();
                Bundle bundle = new Bundle();
                bundle.putString("list", Data.getCurrentList().getName());
                f.setArguments(bundle);
                f.show(ft, "dialog");
                (new NewListTask()).execute();
            }
        }
        else if(view.equals(archive))
        {
            if(Data.getCurrentList() != null)
            {
                ListObj list = Data.getCurrentList();
                list.setArchived(true);
                (new ArchiveListTask()).execute();
                Toast.makeText(this, list.getName() + " archived!", Toast.LENGTH_SHORT).show();
            }
        }
        else if(view.equals(share))
        {
            if(Data.getCurrentList() != null)
            {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ShareListDialog f = new ShareListDialog();
                Bundle bundle = new Bundle();
                bundle.putString("list", Data.getCurrentList().getName());
                f.setArguments(bundle);
                f.show(ft, "dialog");
            }
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
                if(item.equals(view))
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

    private class RemoveItemTask extends AsyncTask<Void, Void, Void>
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
            //IO.getInstance().pullList(getList().getId());
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
            int len = Data.getLists().size();
            while(len == Data.getLists().size());
            return null;
        }

        protected void onPostExecute(Void na)
        {
            setup();
        }
    }
}