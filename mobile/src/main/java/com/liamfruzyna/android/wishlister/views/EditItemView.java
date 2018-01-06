package com.liamfruzyna.android.wishlister.views;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.liamfruzyna.android.wishlister.R;
import com.liamfruzyna.android.wishlister.data.DbConnection;
import com.liamfruzyna.android.wishlister.data.IO;
import com.liamfruzyna.android.wishlister.data.Item;

/**
 * Created by mail929 on 1/4/18.
 */

public class EditItemView extends ItemView
{
	EditText text;

	public EditItemView(Item item, View listItem, View.OnClickListener listener)
	{
		super(item, listItem, true);
		fillItem(listener);
	}

	public void fillItem(View.OnClickListener listener)
	{
		text = listItem.findViewById(R.id.edit_text_item);
		text.setText(item.getItem());

		Button cancel = listItem.findViewById(R.id.edit_button_cancel);
		Button remove = listItem.findViewById(R.id.edit_button_remove);
		Button save = listItem.findViewById(R.id.edit_button_save);
		cancel.setOnClickListener(listener);
		remove.setOnClickListener(listener);
		save.setOnClickListener(listener);
	}

	public void save()
	{
		item.setItem(text.getText().toString());
		DbConnection.editItem(item);
	}
}