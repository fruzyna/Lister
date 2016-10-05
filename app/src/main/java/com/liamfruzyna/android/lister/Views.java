package com.liamfruzyna.android.lister;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liamfruzyna.android.lister.Data.AutoList;
import com.liamfruzyna.android.lister.Data.Data;
import com.liamfruzyna.android.lister.Data.IO;
import com.liamfruzyna.android.lister.Data.Item;
import com.liamfruzyna.android.lister.Data.Util;
import com.liamfruzyna.android.lister.Fragments.DatesFragment;
import com.liamfruzyna.android.lister.Fragments.PeopleFragment;
import com.liamfruzyna.android.lister.Fragments.TagsFragment;
import com.liamfruzyna.android.lister.Fragments.WLFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by mail929 on 3/17/16.
 */
public class Views
{

    public static View createEditItem(final Activity c, LayoutInflater inflater, final int i, LinearLayout list, final WLFragment f)
    {
        View view = inflater.inflate(R.layout.checkbox_edit_item, list, false);

        final CheckBox cb = (CheckBox) view.findViewById(R.id.checkBox);
        final EditText name = (EditText) view.findViewById(R.id.itemName);
        Button remove = (Button) view.findViewById(R.id.remove);
        Button cancel = (Button) view.findViewById(R.id.cancel);
        Button append = (Button) view.findViewById(R.id.append);

        final Item item = Data.getItems().get(i);
        cb.setChecked(item.done);
        name.setText(item.item);

        //listen for checkbox to be checked
        cb.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                item.done = cb.isChecked();
                IO.saveList();
            }
        });

        final LinearLayout sug = (LinearLayout) c.findViewById(R.id.suggestions);


        name.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                sug.removeAllViews();
                final String text = editable.toString();
                final String last = text.split(" ")[text.split(" ").length - 1];
                if(last.contains("#") && last.indexOf("#") != last.length()-1)
                {
                    String start = last.replace("#", "");
                    List<String> tags = Data.getTags();
                    for(int i = 0; i < tags.size(); i++)
                    {
                        String tag = tags.get(i);
                        if(tag.toLowerCase().contains(start.toLowerCase()))
                        {
                            View child = c.getLayoutInflater().inflate(R.layout.tagsug_button, null);
                            final Button b = (Button) child.findViewById(R.id.button);
                            b.setText("#" + tag);
                            b.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View view)
                                {
                                    name.setText(text.replace(last, b.getText()));
                                    sug.removeAllViews();
                                }
                            });
                            sug.addView(child);
                        }
                    }
                }
                if(last.contains("@") && last.indexOf("@") != last.length()-1)
                {
                    String start = last.replace("@", "");
                    List<String> tags = Data.getPeopleTags();
                    for(int i = 0; i < tags.size(); i++)
                    {
                        String tag = tags.get(i);
                        if(tag.toLowerCase().contains(start.toLowerCase()))
                        {
                            View child = c.getLayoutInflater().inflate(R.layout.tagsug_button, null);
                            final Button b = (Button) child.findViewById(R.id.button);
                            b.setText("@" + tag);
                            b.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View view)
                                {
                                    name.setText(text.replace(last, b.getText()));
                                    sug.removeAllViews();
                                }
                            });
                            sug.addView(child);
                        }
                    }
                }
            }
        });

        if (Data.getCurrentList().auto)
        {
            remove.setVisibility(View.GONE);
        } else
        {
            remove.setVisibility(View.VISIBLE);
            remove.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //remove the item
                    f.edit = -1;
                    IO.log("EditItemDialog", "Removing " + item);
                    Data.getItems().remove(item);
                    Data.getCurrentList().items.remove(item);
                    View view = c.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    f.removeItemSnackbar(item);
                    IO.saveList();
                    f.updateList();
                }
            });
        }
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //transition back to just checkbox
                if(name.getText().toString().equals(""))
                {
                    IO.log("EditItemDialog", "Removing " + item);
                    Data.getItems().remove(item);
                    Data.getCurrentList().items.remove(item);
                    View view = c.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    f.removeItemSnackbar(item);
                    IO.saveList();
                }
                f.edit = -1;
                f.updateList();
            }
        });
        append.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                f.edit = -1;
                IO.log("EditItemDialog", "Updating " + item.item + " to " + name.getText().toString());
                item.item = name.getText().toString();
                View view = c.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                item.parseItem();
                IO.saveList();
                f.updateList();
            }
        });
        return view;
    }

    //creates the textview with a lists tags
    public static TextView createTags(Context c)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Tags: ");
        for (String list : Data.getListFromName(Data.getCurrentName()).tags)
        {
            sb.append(list + " ");
        }
        TextView tv = new TextView(c);
        tv.setText(sb.toString());
        return tv;
    }

    //creates the textview with a lists tags
    public static String createEditTags()
    {
        StringBuilder sb = new StringBuilder();
        for (String list : Data.getListFromName(Data.getCurrentName()).tags)
        {
            sb.append(list + " ");
        }
        return sb.toString();
    }

    //creates the textview with a lists criteria
    public static TextView createCriteria(Context con)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Criteria:");
        for (String c : ((AutoList) Data.getListFromName(Data.getCurrentName())).getCriteria())
        {
            sb.append("\n" + c);
        }
        TextView tv = new TextView(con);
        tv.setText(sb.toString());
        return tv;
    }


    //creates the item view that is displayed on screen
    public static View createItem(final Context c, final int i, LinearLayout list, final WLFragment f)
    {
        LayoutInflater inflater = LayoutInflater.from(c);
        View view = inflater.inflate(R.layout.checkbox_list_item, list, false);

        final Item item = Data.getItems().get(i);
        LinearLayout tags = (LinearLayout) view.findViewById(R.id.tags);


        //init checkbox and set text, checked status, and color
        final CheckBox cb = (CheckBox) view.findViewById(R.id.checkbox);
        int color = Color.parseColor(Data.getItems().get(i).color);

        //color item text based off date (late is red, day of is orange)
        SharedPreferences settings = f.getActivity().getSharedPreferences(IO.PREFS, 0);
        boolean highlight = settings.getBoolean(IO.HIGHLIGHT_DATE_PREF, true);
        if (highlight)
        {
            Date date = Data.getItems().get(i).date;
            Date today = Calendar.getInstance().getTime();
            if (date.getYear() == today.getYear() && date.getMonth() == today.getMonth() && date.getDate() == today.getDate() && !Data.getItems().get(i).done)
            {
                color = Color.parseColor("#FFA500");
            }
            else if (date.compareTo(today) < 0 && !Data.getItems().get(i).done)
            {
                color = Color.RED;
            }
        }

        for(int j = 0; j < item.item.split(" ").length; j++)
        {
            String word = item.item.split(" ")[j];
            if(word.length() > 0)
            {
                if (word.charAt(0) == '#')
                {
                    RelativeLayout tagView = (RelativeLayout) inflater.inflate(R.layout.tag_list_item, tags, false);
                    TextView tagText = (TextView) tagView.findViewById(R.id.tag);
                    tagText.setText(word);
                    tags.addView(tagView);

                    tagView.setClickable(true);
                    tagView.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            TextView tagText = (TextView) view.findViewById(R.id.tag);
                            Bundle bundle = new Bundle();
                            bundle.putString("tag", tagText.getText().toString().replace("#", ""));
                            //set Fragmentclass Arguments
                            Fragment frag = new TagsFragment();
                            frag.setArguments(bundle);
                            ((WLActivity) c).changeFragment(frag, "Tags");
                        }
                    });
                    tagView.setOnLongClickListener(new View.OnLongClickListener()
                    {
                        @Override
                        public boolean onLongClick(View v)
                        {
                            //transition to edit checkbox
                            f.edit = i;
                            f.updateList();
                            return true;
                        }
                    });
                }
                else if (word.charAt(0) == '@')
                {
                    RelativeLayout tagView = (RelativeLayout) inflater.inflate(R.layout.tag_list_item, tags, false);
                    TextView tagText = (TextView) tagView.findViewById(R.id.tag);
                    tagText.setText(word);
                    tags.addView(tagView);

                    tagView.setClickable(true);
                    tagView.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            TextView tagText = (TextView) view.findViewById(R.id.tag);
                            Bundle bundle = new Bundle();
                            bundle.putString("tag", tagText.getText().toString().replace("@", ""));
                            //set Fragmentclass Arguments
                            Fragment frag = new PeopleFragment();
                            frag.setArguments(bundle);
                            ((WLActivity) c).changeFragment(frag, "People");
                        }
                    });
                    tagView.setOnLongClickListener(new View.OnLongClickListener()
                    {
                        @Override
                        public boolean onLongClick(View v)
                        {
                            //transition to edit checkbox
                            f.edit = i;
                            f.updateList();
                            return true;
                        }
                    });
                }
                else if (word.contains("/"))
                {
                    if (item.formattedDate != "NONE")
                    {
                        RelativeLayout tagView = (RelativeLayout) inflater.inflate(R.layout.tag_list_item, tags, false);
                        TextView tagText = (TextView) tagView.findViewById(R.id.tag);
                        tagText.setText(item.formattedDate);
                        tags.addView(tagView);

                        tagView.setClickable(true);
                        tagView.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                TextView tagText = (TextView) view.findViewById(R.id.tag);
                                Bundle bundle = new Bundle();
                                bundle.putString("tag", DatesFragment.getDate(item.date));
                                //set Fragmentclass Arguments
                                Fragment frag = new DatesFragment();
                                frag.setArguments(bundle);
                                ((WLActivity) c).changeFragment(frag, "Dates");
                            }
                        });
                        tagView.setOnLongClickListener(new View.OnLongClickListener()
                        {
                            @Override
                            public boolean onLongClick(View v)
                            {
                                //transition to edit checkbox
                                f.edit = i;
                                f.updateList();
                                return true;
                            }
                        });
                    }
                }
                else
                {
                    RelativeLayout tagView = (RelativeLayout) inflater.inflate(R.layout.item_name, tags, false);
                    TextView tagText = (TextView) tagView.findViewById(R.id.textView);
                    tagText.setText(word);
                    tagText.setTextColor(color);
                    tags.addView(tagView);
                }

                if (j < item.item.split(" ").length - 1)
                {
                    RelativeLayout tagView = (RelativeLayout) inflater.inflate(R.layout.item_name, tags, false);
                    TextView tagText = (TextView) tagView.findViewById(R.id.textView);
                    tagText.setText(" ");
                    tags.addView(tagView);
                }
            }
        }
        /*
        //SpannableStringBuilder s = Util.colorTags(item.item, color);
        //cb.setText(s);
        String s = item.item.toString();
        for(String tag : item.tags)
        {
            RelativeLayout tagView = (RelativeLayout) inflater.inflate(R.layout.tag_list_item, tags, false);
            TextView tagText = (TextView) tagView.findViewById(R.id.tag);
            tagText.setText("#" + tag);
            tags.addView(tagView);
            s = s.replace("#" + tag, "");

            tagView.setClickable(true);
            tagView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    TextView tagText = (TextView) view.findViewById(R.id.tag);
                    Bundle bundle = new Bundle();
                    bundle.putString("tag", tagText.getText().toString().replace("#", ""));
                    //set Fragmentclass Arguments
                    Fragment frag = new TagsFragment();
                    frag.setArguments(bundle);
                    ((WLActivity) c).changeFragment(frag, "Tags");
                }
            });
        }
        for(String tag : item.people)
        {
            RelativeLayout tagView = (RelativeLayout) inflater.inflate(R.layout.tag_list_item, tags, false);
            TextView tagText = (TextView) tagView.findViewById(R.id.tag);
            tagText.setText("@" + tag);
            tags.addView(tagView);
            s = s.replace("@" + tag, "");

            tagView.setClickable(true);
            tagView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    TextView tagText = (TextView) view.findViewById(R.id.tag);
                    Bundle bundle = new Bundle();
                    bundle.putString("tag", tagText.getText().toString().replace("@", ""));
                    //set Fragmentclass Arguments
                    Fragment frag = new PeopleFragment();
                    frag.setArguments(bundle);
                    ((WLActivity) c).changeFragment(frag, "People");
                }
            });
        }
        if(item.formattedDate != "NONE")
        {
            RelativeLayout tagView = (RelativeLayout) inflater.inflate(R.layout.tag_list_item, tags, false);
            TextView tagText = (TextView) tagView.findViewById(R.id.tag);
            tagText.setText(item.formattedDate);
            tags.addView(tagView);
            s = s.replace(item.formattedDate, "");

            tagView.setClickable(true);
            tagView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    TextView tagText = (TextView) view.findViewById(R.id.tag);
                    Bundle bundle = new Bundle();
                    bundle.putString("tag", tagText.getText().toString());
                    //set Fragmentclass Arguments
                    Fragment frag = new DatesFragment();
                    frag.setArguments(bundle);
                    ((WLActivity) c).changeFragment(frag, "Dates");
                }
            });
        }


        IO.log("Views", "createItem", s);*/
        cb.setText("");
        cb.setTextColor(color);
        cb.setChecked(Data.getItems().get(i).done);

        //if item is done cross it out
        if (Data.getItems().get(i).done)
        {
            cb.setPaintFlags(cb.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else
        {
            cb.setPaintFlags(0);
        }

        cb.setClickable(false);

        //listen for checkbox to be checked
        view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Data.getItems().get(i).done = !Data.getItems().get(i).done;
                cb.setChecked(Data.getItems().get(i).done);
                //if it is checked cross it out
                if (cb.isChecked())
                {
                    cb.setPaintFlags(cb.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else
                {
                    cb.setPaintFlags(0);
                }
                IO.saveList();

            }
        });

        //listen for item to be long pressed
        view.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                //transition to edit checkbox
                f.edit = i;
                f.updateList();
                return true;
            }
        });
        return view;
    }
}
