package com.liamfruzyna.android.wishlister;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import java.util.List;

/**
 * Created by mail929 on 11/8/14.
 */
public class CustomAdapter extends ArrayAdapter<Item>
{
    Context context;
    int layoutResourceId;
    List<Item> items;
    int resource;

    public CustomAdapter(Context context, int resource, int layoutResourceId, List<Item> items)
    {
        super(context, resource, layoutResourceId, items);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.items = items;
        this.resource = resource;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        //basic setup
        View view;
        if (convertView == null)
        {
            LayoutInflater infl = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = infl.inflate(R.layout.item, parent, false);
        }
        view = super.getView(position, convertView, parent);

        //init checkbox and set text
        final CheckBox cb = (CheckBox) view.findViewById(R.id.checkbox);
        cb.setText(items.get(position).item);
        cb.setChecked(items.get(position).done);
        if (items.get(position).done)
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
                items.get(position).done = cb.isChecked();
                if (cb.isChecked())
                {
                    cb.setPaintFlags(cb.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else
                {
                    cb.setPaintFlags(0);
                }
                IO.save(WLActivity.lists, WLActivity.dir);
            }
        });
        cb.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                items.remove(position);
                WLActivity.lists.get(WLActivity.current).items = items;
                cb.setTextColor(Color.parseColor("#FFFFFF"));
                IO.save(WLActivity.lists, WLActivity.dir);
                return true;
            }
        });
        return view;
    }
}
