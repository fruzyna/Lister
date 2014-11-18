package com.liamfruzyna.android.wishlister;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WLActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener
{
    static List<WishList> lists = new ArrayList<WishList>();
    static List<Item> items = new ArrayList<Item>();
    static int current = 0;

    static String dir = "";

    static ListView list;
    static Context c;

    public View.OnClickListener l = new View.OnClickListener()
    {

        @Override
        public void onClick(View v)
        {
            lists.add(new WishList(name.getText().toString(), new ArrayList<String>(Arrays.asList(tags.getText().toString().split(" ")))));
            popup.removeAllViews();
            Spinner spin = (Spinner) findViewById(R.id.spinner);
            //creates a list of events level and distance to fill out the spinner
            List<String> names = new ArrayList<String>();
            for (int i = 0; i < lists.size(); i++)
            {
                names.add(lists.get(i).name);
            }
            //setup spinner
            ArrayAdapter<String> sadapter = new ArrayAdapter<String>(c, R.layout.spinner_item, names);
            sadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spin.setAdapter(sadapter);
            current = spin.getSelectedItemPosition();
            updateList();
        }
    };
    static CustomAdapter adapter;
    static RelativeLayout tagcv;
    EditText name;
    EditText tags;
    LinearLayout popup;
    Spinner spin;

    public static void removeItem(int i)
    {
        lists.get(current).items.remove(i);
        updateList();
    }

    public static void updateList()
    {
        List<Item> temp = lists.get(current).items;
        items = new ArrayList<Item>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < temp.size(); i++)
        {
            if (!temp.get(i).done)
            {
                items.add(temp.get(i));
            }
        }
        for (int i = 0; i < temp.size(); i++)
        {
            if (temp.get(i).done)
            {
                items.add(temp.get(i));
            }
        }
        adapter = new CustomAdapter(c, R.layout.item, R.id.checkbox, items);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        sb.append("Tags: ");
        for (int i = 0; i < lists.get(current).tags.size(); i++)
        {
            sb.append(lists.get(current).tags.get(i) + " ");
        }
        TextView tv = new TextView(c);
        tv.setText(sb.toString());
        tagcv.removeAllViews();
        tagcv.addView(tv);
        IO.save(lists, dir);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wl);

        dir = getFilesDir().toString();

        try
        {
            lists = IO.load(dir);
        } catch (JSONException e)
        {
            e.printStackTrace();
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        }

        //init view widgets
        ImageButton settings = (ImageButton) findViewById(R.id.settings);
        final Fab fab = (Fab) findViewById(R.id.fab);
        tagcv = (RelativeLayout) findViewById(R.id.tag);
        list = (ListView) findViewById(R.id.listView);
        c = this;

        settings.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent goSettings = new Intent(WLActivity.this, SettingsActivity.class);
                WLActivity.this.startActivity(goSettings);
            }
        });

        //set up fab (Floating Action Button)
        fab.setFabDrawable(getResources().getDrawable(R.drawable.ic_add));
        fab.setFabColor(getResources().getColor(R.color.dark_main));
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final LinearLayout popup = (LinearLayout) findViewById(R.id.popup);
                popup.removeAllViews();
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View v = inflater.inflate(R.layout.new_item_item, null);
                popup.addView(v);
                Button add = (Button) v.findViewById(R.id.button);
                final EditText name = (EditText) v.findViewById(R.id.name);
                add.setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        lists.get(current).items.add(new Item(name.getText().toString(), false));
                        popup.removeAllViews();
                        updateList();
                    }
                });
            }
        });
        fab.setOnLongClickListener(new View.OnLongClickListener()
        {
            public boolean onLongClick(View v)
            {
                Toast.makeText(v.getContext(), "New Item", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        //makes sure that lists isn't null
        if (lists == null)
        {
            lists = new ArrayList<WishList>();
        }

        //sets up spinner
        spin = (Spinner) findViewById(R.id.spinner);
        if (lists.size() > 0)
        {
            //creates a list of events level and distance to fill out the spinner
            List<String> names = new ArrayList<String>();
            for (int i = 0; i < lists.size(); i++)
            {
                names.add(lists.get(i).name);
            }
            //setup spinner
            ArrayAdapter<String> sadapter = new ArrayAdapter<String>(this, R.layout.spinner_item, names);
            sadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spin.setAdapter(sadapter);
            current = spin.getSelectedItemPosition();
            updateList();
        } else
        {
            popup = (LinearLayout) findViewById(R.id.popup);
            popup.removeAllViews();
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.new_list_item, null);
            popup.addView(view);
            Button add = (Button) view.findViewById(R.id.button);
            name = (EditText) view.findViewById(R.id.name);
            tags = (EditText) view.findViewById(R.id.tags);
            add.setOnClickListener(l);
        }

        spin.setOnItemSelectedListener(this);

        ImageButton newlist = (ImageButton) findViewById(R.id.add);
        newlist.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                popup = (LinearLayout) findViewById(R.id.popup);
                popup.removeAllViews();
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.new_list_item, null);
                popup.addView(view);
                Button add = (Button) view.findViewById(R.id.button);
                name = (EditText) view.findViewById(R.id.name);
                tags = (EditText) view.findViewById(R.id.tags);
                add.setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        lists.add(new WishList(name.getText().toString(), new ArrayList<String>(Arrays.asList(tags.getText().toString().split(" ")))));
                        popup.removeAllViews();
                        //creates a list of events level and distance to fill out the spinner
                        List<String> names = new ArrayList<String>();
                        for (int i = 0; i < lists.size(); i++)
                        {
                            names.add(lists.get(i).name);
                        }
                        //setup spinner
                        ArrayAdapter<String> sadapter = new ArrayAdapter<String>(c, R.layout.spinner_item, names);
                        sadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spin.setAdapter(sadapter);
                        current = spin.getSelectedItemPosition();
                        updateList();
                    }
                });
                add.setOnLongClickListener(new View.OnLongClickListener()
                {
                    public boolean onLongClick(View v)
                    {
                        Toast.makeText(c, "New List", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
    {
        current = spin.getSelectedItemPosition();
        updateList();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {
    }

    @Override
    public void onBackPressed()
    {
        popup = (LinearLayout) findViewById(R.id.popup);
        popup.removeAllViews();
    }
}
