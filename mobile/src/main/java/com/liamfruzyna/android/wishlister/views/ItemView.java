package com.liamfruzyna.android.wishlister.views;

import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liamfruzyna.android.wishlister.R;
import com.liamfruzyna.android.wishlister.data.IO;
import com.liamfruzyna.android.wishlister.data.Item;

/**
 * Created by mail929 on 1/4/18.
 */

public class ItemView implements View.OnClickListener
{
	Item item;
	View listItem;

	boolean edit;

	public ItemView(Item item, View listItem)
	{
		this.item = item;
		this.listItem = listItem;
		fillItem();
		edit = false;
	}

	public ItemView(Item item, View listItem, boolean edit)
	{
		this.item = item;
		this.listItem = listItem;
		this.edit = edit;
	}

	public void fillItem()
	{
		CheckBox box = listItem.findViewById(R.id.item_check_done);
		TextView text = listItem.findViewById(R.id.item_text_item);
		box.setChecked(item.isDone());
		text.setText(item.getItem());
		box.setOnClickListener(this);
	}

	@Override
	public void onClick(View view)
	{
		CheckBox box = (CheckBox) view;
		item.setDone(box.isChecked());

		(new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				IO.getInstance().checkItem(item);
			}
		})).start();
	}

	@Override
	public boolean equals(Object object)
	{
		if(object instanceof View)
		{
			return listItem.equals(object);
		}
		return false;
	}

	public Item getItem()
	{
		return item;
	}
}
