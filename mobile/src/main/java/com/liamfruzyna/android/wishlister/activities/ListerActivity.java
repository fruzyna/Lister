package com.liamfruzyna.android.wishlister.activities;

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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.liamfruzyna.android.wishlister.data.Data;
import com.liamfruzyna.android.wishlister.R;
import com.liamfruzyna.android.wishlister.data.IO;
import com.liamfruzyna.android.wishlister.data.Item;
import com.liamfruzyna.android.wishlister.data.ListObj;
import com.liamfruzyna.android.wishlister.views.EditItemView;
import com.liamfruzyna.android.wishlister.views.ItemView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mail929 on 2/24/17.
 */

public class ListerActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, AdapterView.OnItemSelectedListener
{
    //Views
    Spinner listSpinner;
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

        setup();
    }

    public void setup()
    {
        isNewItem = false;

        listSpinner = findViewById(R.id.list_spinner_lists);
        listLayout = findViewById(R.id.list_layout_items);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Data.getNames());
        listSpinner.setAdapter(adapter);
        listSpinner.setOnItemSelectedListener(this);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }

    public void buildList()
    {
        ListObj list = getList();
        items = new ArrayList<>();
        listLayout.removeAllViews();

        for (Item item : list.getItems())
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

            if(getList().getPerm() == 'r')
            {
            }
            else
            {
                addItem = getLayoutInflater().inflate(R.layout.button_add_item, listLayout, false);
                addItem.setOnClickListener(this);
                listLayout.addView(addItem);
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

    public ListObj getList()
    {
        return Data.getLists().get(listSpinner.getSelectedItemPosition());
    }

    @Override
    public boolean onLongClick(View view)
    {
        if(getList().getPerm() == 'r')
        {
            Toast.makeText(this, "You cannot edit items on this list", Toast.LENGTH_LONG);
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
    public void onNothingSelected(AdapterView<?> adapterView){}

    private class AddItemTask extends AsyncTask<Void, Void, Void>
    {
        protected Void doInBackground(Void... na)
        {
            IO.getInstance().addItem(text.getText().toString(), getList());
            IO.getInstance().pullList(getList().getId());
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
            IO.getInstance().removeItem(editItem.getItem());
            IO.getInstance().pullList(getList().getId());
            return null;
        }

        protected void onPostExecute(Void na)
        {
            editId = -1;
            buildList();
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
}