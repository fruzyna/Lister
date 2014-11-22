package com.liamfruzyna.android.lister;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

    static LinearLayout list;
    static Context c;
    static RelativeLayout tagcv;
    EditText name;
    EditText tags;
    static LinearLayout popup;
    static Spinner spin;

    public static void removeItem(int i)
    {
        lists.get(current).items.remove(i);
        updateList();
    }

    public List<String> getTags()
    {
        List<String> tags = new ArrayList<String>();
        for(int i = 0; i < lists.size(); i++)
        {
            for(int j = 0; j < lists.get(i).tags.size(); j++)
            {
                boolean found = false;
                for(int l = 0; l < tags.size(); l++)
                {
                    if(tags.get(l).equals(lists.get(i).tags.get(j)))
                    {
                        found = true;
                    }
                }
                if(!found)
                {
                    tags.add(lists.get(i).tags.get(j));
                }
            }
        }
        return tags;
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
        list.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(c);
        for (int i = 0; i < items.size(); i++)
        {
            final int j = i;
            View view = inflater.inflate(R.layout.item, list, false);
            //init checkbox and set text
            final CheckBox cb = (CheckBox) view.findViewById(R.id.checkbox);
            cb.setText(items.get(i).item);
            cb.setChecked(items.get(i).done);
            if (items.get(i).done)
            {
                cb.setPaintFlags(cb.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else
            {
                cb.setPaintFlags(0);
            }

            cb.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(items.size() > 0)
                    {
                        items.get(j).done = cb.isChecked();
                        if (cb.isChecked())
                        {
                            cb.setPaintFlags(cb.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        } else
                        {
                            cb.setPaintFlags(0);
                        }
                        IO.save(WLActivity.lists, WLActivity.dir);
                    }
                }
            });
            cb.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    boolean found = false;
                    for(int l = 0; l < items.size(); l++)
                    {
                        if(items.get(l).item.equals(cb.getText().toString()))
                        {
                            found = true;
                        }
                    }
                    if(found)
                    {
                        items.remove(j);
                        WLActivity.lists.get(WLActivity.current).items = items;
                        cb.setTextColor(Color.parseColor("#FFFFFF"));
                        IO.save(WLActivity.lists, WLActivity.dir);
                    }
                    else
                    {
                        items.add(new Item(cb.getText().toString(), cb.isChecked()));
                        WLActivity.lists.get(WLActivity.current).items = items;
                        cb.setTextColor(Color.parseColor("#000000"));
                        IO.save(WLActivity.lists, WLActivity.dir);
                    }
                    return false;
                }
            });
            list.addView(view);
        }
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
        list = (LinearLayout) findViewById(R.id.list);
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
        fab.setFabDrawable(getResources().getDrawable(R.drawable.ic_add_white));
        fab.setFabColor(getResources().getColor(R.color.fab));
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(lists.size() > 0)
                {
                    DialogFragment dialog = new NewItemDialog();
                    dialog.show(getFragmentManager(), "");
                }
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
            DialogFragment dialog = new NewListDialog();
            dialog.show(getFragmentManager(), "");
        }

        spin.setOnItemSelectedListener(this);

        ImageButton newlist = (ImageButton) findViewById(R.id.add);
        ImageButton removelist = (ImageButton) findViewById(R.id.remove);
        newlist.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                DialogFragment dialog = new NewListDialog();
                dialog.show(getFragmentManager(), "");
            }
        });
        removelist.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(lists.size() > 0)
                {
                    DialogFragment dialog = new RemoveListDialog();
                    dialog.show(getFragmentManager(), "");
                }
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


    public static class RemoveListDialog extends DialogFragment
    {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Are you sure you want to delete " + lists.get(current).name + "? You can never get it back.")
                    .setTitle("Delete List?")
                    .setPositiveButton("DELETE", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            lists.remove(current);
                            //setup spinner
                            List<String> names = new ArrayList<String>();
                            for (int i = 0; i < lists.size(); i++)
                            {
                                names.add(lists.get(i).name);
                            }
                            ArrayAdapter<String> sadapter = new ArrayAdapter<String>(c, R.layout.spinner_item, names);
                            sadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spin.setAdapter(sadapter);
                            current = spin.getSelectedItemPosition();
                            if (names.size() == 0)
                            {
                                IO.save(lists, dir);
                                list.removeAllViews();
                                tagcv.removeAllViews();
                            }
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            return builder.create();
        }
    }
    public static class NewListDialog extends DialogFragment
    {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
            final View v = inflater.inflate(R.layout.new_list_item, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Type the new list's name and click create to make a new list.")
                    .setTitle("New List")
                    .setView(v)
                    .setPositiveButton("CREATE", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            EditText name = (EditText) v.findViewById(R.id.name);
                            EditText tags = (EditText) v.findViewById(R.id.tags);
                            lists.add(new WishList(name.getText().toString(), new ArrayList<String>(Arrays.asList(tags.getText().toString().split(" ")))));
                            spin = (Spinner) getActivity().findViewById(R.id.spinner);
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
                            spin.setSelection(lists.size()-1);
                            current = spin.getSelectedItemPosition();
                            updateList();
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                        }
                    });
            return builder.create();
        }
    }
    public static class NewItemDialog extends DialogFragment
    {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
            final View v = inflater.inflate(R.layout.new_item_item, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Add a new item to " + lists.get(current).name)
                    .setTitle("New Item")
                    .setView(v)
                    .setPositiveButton("CREATE", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            EditText name = (EditText) v.findViewById(R.id.name);
                            lists.get(current).items.add(new Item(name.getText().toString(), false));
                            updateList();
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                        }
                    });
            return builder.create();
        }
    }
}
