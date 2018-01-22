package com.liamfruzyna.android.wishlister.views;

import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.liamfruzyna.android.wishlister.R;
import com.liamfruzyna.android.wishlister.activities.ListerActivity;
import com.liamfruzyna.android.wishlister.data.Data;
import com.liamfruzyna.android.wishlister.data.DbConnection;
import com.liamfruzyna.android.wishlister.data.Item;

/**
 * Created by mail929 on 1/4/18.
 */

public class ItemView implements View.OnClickListener, SwipeLayout.SwipeListener
{
	Item item;
	View listItem;
	CheckBox box;
	SwipeLayout swipe;
	Button delete;

	boolean edit;
	boolean open;

	public ItemView(Item item, SwipeLayout swipe)
	{
		this.item = item;
		this.swipe = swipe;
		fillItem();
		edit = false;
		open = false;
	}

	public ItemView(Item item, View listItem, boolean edit)
	{
		this.item = item;
		this.listItem = listItem;
		this.edit = edit;
		open = false;
	}

	public void fillItem()
	{
        listItem = swipe.findViewById(R.id.list_item);
        delete = swipe.findViewById(R.id.list_delete_item);
        delete.setOnClickListener(this);
		box = listItem.findViewById(R.id.item_check_done);
		TextView text = listItem.findViewById(R.id.item_text_item);
		box.setChecked(item.isDone());
		text.setText(item.getItem());
		box.setClickable(false);
		listItem.setOnClickListener(this);
	}

	@Override
	public void onClick(View view)
	{
	    if(view.equals(listItem))
        {
            if(!open &&Data.getList(item.getParent()).getPerm() != 'r')
            {
                box.setChecked(!box.isChecked());
                item.setDone(box.isChecked());

                (new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        DbConnection.checkItem(item);
                    }
                })).start();
            }
        }
        else if(view.equals(delete))
        {
            (new RemoveItemTask()).execute();
        }
	}

    public class RemoveItemTask extends AsyncTask<Void, Void, Void>
    {
        protected Void doInBackground(Void... na)
        {
            DbConnection.removeItem(item);
            DbConnection.pullList(Data.getCurrentList().getId());
            return null;
        }

        protected void onPostExecute(Void na)
        {
            ((ListerActivity) swipe.getContext()).buildList();
        }
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

	public boolean isOpen()
	{
		return open;
	}

	public View getLayout()
    {
        return listItem;
    }

	@Override
	public void onStartOpen(SwipeLayout layout)
	{
		open = true;
	}

	@Override
	public void onOpen(SwipeLayout layout)
	{
		open = true;
	}

	@Override
	public void onStartClose(SwipeLayout layout)
	{
		open = false;
	}

	@Override
	public void onClose(SwipeLayout layout)
	{
		open = false;
	}

	@Override
	public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset)
	{

	}

	@Override
	public void onHandRelease(SwipeLayout layout, float xvel, float yvel)
	{

	}
}
