package com.liamfruzyna.android.wishlister.activities;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.liamfruzyna.android.wishlister.dialogs.ArchiveListDialog;
import com.liamfruzyna.android.wishlister.data.AutoList;
import com.liamfruzyna.android.wishlister.data.Data;
import com.liamfruzyna.android.wishlister.dialogs.DeleteListDialog;
import com.liamfruzyna.android.wishlister.dialogs.ListViewDialog;
import com.liamfruzyna.android.wishlister.views.FlowLayout;
import com.liamfruzyna.android.wishlister.data.IO;
import com.liamfruzyna.android.wishlister.data.Item;
import com.liamfruzyna.android.wishlister.dialogs.ListSettingsDialog;
import com.liamfruzyna.android.wishlister.dialogs.NewListDialog;
import com.liamfruzyna.android.wishlister.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mail929 on 2/24/17.
 */

public class ListerActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener, View.OnLongClickListener, TextWatcher
{
    //Views
    Spinner listSpinner;
    LinearLayout listLayout;
    CheckBox showDone;
    View addItem;
    ImageButton archive;
    ImageButton delete;
    ImageButton listSettings;
    Button editTagsButton;
    FloatingActionButton fab;
    EditText itemEdit;
    LinearLayout suggestionsBox;

    //Tools?
    LayoutInflater inflater;

    //List Maps
    Map<Integer, View> listViews;
    Map<Integer, Item> listItems;

    //Edit Flags
    int editItem = -1;
    boolean editTags = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lister);

        loadActivity();
    }


    @Override
    public void onResume()
    {
        super.onResume();

        loadActivity();
    }

    /**
     * Used to reload the whole activity: refreshes spinner, list, and buttons
     */
    public void loadActivity()
    {
        if(Data.getLists().size() == 0)
        {
            Intent intent = new Intent(this, SplashActivity.class);
            startActivity(intent);
        }
        else
        {
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            setupSpinner();

            loadList();

            setupButtons();

            setupFab();
        }
    }

    /**
     * Used to rebuild the list: normally in cases of new list selected
     */
    public void loadList()
    {
        typeCheck();

        setupList();

        setupTags();
    }

    /**
     * Builds the tag view at the bottom of the screen
     */
    public void setupTags()
    {
        //Tag Container
        LinearLayout tagContainer = (LinearLayout) findViewById(R.id.tagsContainer);
        tagContainer.removeAllViews();

        //Draws the appropriate view if/if not the tags are to be edited
        if(editTags)
        {
            tagContainer.addView(createEditTagView());
        }
        else
        {
            tagContainer.addView(createTagView());
        }

    }

    /**
     * Builds the view to edit the list's tags
     * @return Produced view with edittext containing tags and control buttons
     */
    public View createEditTagView()
    {
        View tagView = inflater.inflate(R.layout.tags_edit_item, null);

        //Fills edittext with space separated list of tags
        final EditText tagsText = (EditText) tagView.findViewById(R.id.tags);
        tagsText.setText(makeTagText());

        //Append button to stop editing and save changes
        Button append = (Button) tagView.findViewById(R.id.append);
        append.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                saveTags(tagsText.getText().toString());

                //close edit view
                editTags = false;
                setupTags();
            }
        });

        //Cancel button to stop editing the tags without committing changes
        Button cancel = (Button) tagView.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //close edit view
                editTags = false;
                setupTags();
            }
        });

        return tagView;
    }

    /**
     * Adds given string of tags to list and saves
     * @param tagString Space separated list of tags
     */
    public void saveTags(String tagString)
    {
        String[] tagsArray = tagString.split(" ");
        List<String> tags = new ArrayList<String>();
        for(String tag : tagsArray)
        {
            tags.add(tag);
        }
        Data.getCurrentList().tags = tags;
        save();
    }

    /**
     * Builds the view to display the list's tags
     * @return Produced view containing textview with all tags
     */
    public View createTagView()
    {
        View tagView = inflater.inflate(R.layout.tags_list_item, null);

        //Setup for button to edit tags
        editTagsButton = (Button) tagView.findViewById(R.id.editTag);
        editTagsButton.setOnClickListener(this);

        fillTagView(tagView);

        return tagView;
    }

    /**
     * Fills the views containing the list of tags
     * @param tagView Root view to fill
     */
    public void fillTagView(View tagView)
    {
        HorizontalScrollView tagsText = (HorizontalScrollView) tagView.findViewById(R.id.tagscroll);

        //Creates textview
        TextView tags = new TextView(this);

        //Builds string for text view
        String text = makeTagText();

        //fills views
        tags.setText(text);
        tagsText.addView(tags);
    }

    /**
     * Produces String to place in tag view
     * @return Space separated list of tags
     */
    public String makeTagText()
    {
        List<String> tags = Data.getCurrentList().tags;
        String tagText = "";
        for(int i = 0; i < tags.size(); i++)
        {
            if(i != 0)
            {
                //adds space before all but the first item
                tagText += " ";
            }
            tagText += tags.get(i);
        }
        return tagText;
    }

    /**
     * Builds and fills out the list spinner (dropdown) on top of the view
     */
    public void setupSpinner()
    {
        //Fix selected item past item count
        if(Data.getCurrent() >= Data.getUnArchived().size())
        {
            saveCurrent(Data.getUnArchived().size() - 1);
        }

        listSpinner = ((Spinner) findViewById(R.id.spinner));
        listSpinner.setOnItemSelectedListener(this);

        //Fill out spinner
        ArrayAdapter<String> sadapter = new ArrayAdapter<>(this, R.layout.spinner_item, sortStringsAlpha(Data.getNames()));
        sadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listSpinner.setAdapter(sadapter);

        //Select current item
        int current = IO.getInstance().getInt(IO.CURRENT_LIST_PREF);
        listSpinner.setSelection(current);
        saveCurrent(current);
    }

    /**
     * Sorts a given lists of strings alphabetically, primarily for sorting lists
     * @param strings Strings to sort
     * @return Sorted strings
     */
    public List<String> sortStringsAlpha(List<String> strings)
    {
        Collections.sort(strings);
        return strings;
    }

    /**
     * Checks if the list is an autolist and does work accordingly
     */
    public void typeCheck()
    {
        //Init views
        TextView autoBeacon = (TextView) findViewById(R.id.auto);
        View criteria = findViewById(R.id.criteria);
        LinearLayout addItemBox = ((LinearLayout) findViewById(R.id.newitem));
        addItemBox.removeAllViews();

        if(Data.getCurrentList().auto)
        {
            //Refresh auto list
            ((AutoList) Data.getCurrentList()).findItems();

            //Make autolist only views visible
            autoBeacon.setVisibility(View.VISIBLE);
            criteria.setVisibility(View.VISIBLE);

            createCriteriaView();
        }
        else
        {
            //Hide autolist only views
            autoBeacon.setVisibility(View.INVISIBLE);
            criteria.setVisibility(View.GONE);

            //Show and setup add item button
            addItem = inflater.inflate(R.layout.button_add_item, null);
            addItem.setOnClickListener(this);
            addItemBox.addView(addItem);
        }
    }

    /**
     * Fills out few containing criteria items
     */
    public void createCriteriaView()
    {
        //Clear existing items
        LinearLayout criterionView = (LinearLayout) findViewById(R.id.criterion);
        criterionView.removeAllViews();

        //Add each item as a new textview below the last
        for(String criterion : ((AutoList) Data.getCurrentList()).getCriteria())
        {
            TextView criteriaText = new TextView(this);
            criteriaText.setText(criterion);
            criterionView.addView(criteriaText);
        }
    }

    /**
     * Generates the list
     */
    public void setupList()
    {
        //View init
        suggestionsBox = (LinearLayout) findViewById(R.id.suggestions);
        suggestionsBox.removeAllViews();
        listLayout = ((LinearLayout) findViewById(R.id.list));
        listLayout.removeAllViews();

        //Clear list maps
        listViews = new HashMap<>();
        listItems = new HashMap<>();

        //Add items to list
        addItems();
    }

    /**
     * Adds all list items to the list
     */
    public void addItems()
    {
        //Iterates through all items
        int i = 0;
        for(Item item : sortByChecked(sortByDate(Data.getItems())))
        {
            //Sets text color of item according to proximity of due date
            if(!item.done && IO.getInstance().getBoolean(IO.HIGHLIGHT_DATE_PREF, true))
            {
                Calendar today = Calendar.getInstance();
                today.set(Calendar.HOUR_OF_DAY, 0);
                today.set(Calendar.MINUTE, 0);
                today.set(Calendar.SECOND, 0);
                Calendar yesterday = Calendar.getInstance();
                yesterday.set(Calendar.HOUR_OF_DAY, 0);
                yesterday.set(Calendar.MINUTE, 0);
                yesterday.set(Calendar.SECOND, 0);
                yesterday.add(Calendar.DAY_OF_YEAR, -1);

                if(item.date.before(yesterday.getTime()))
                {
                    item.color = "#FF0000";
                }
                else if(item.date.before(today.getTime()))
                {
                    item.color = "#FFA500";
                }
            }

            //Adds item to map
            listItems.put(i, item);

            View box = inflater.inflate(R.layout.checkbox_list_item, null);

            //Checkbox setup
            CheckBox cb = ((CheckBox) box.findViewById(R.id.checkbox));
            cb.setChecked(item.done);
            cb.setOnClickListener(this);

            //Sets up item name view
            FlowLayout tags = ((FlowLayout) box.findViewById(R.id.tags));
            tags.setOnClickListener(this);
            tags.setOnLongClickListener(this);

            //Adds every word one by one
            for(String word : item.item.split(" "))
            {
                addWord(tags, item, word);
            }

            //Adds view to map
            listViews.put(i, box);

            //If the item is to be edited make that view instead, otherwise add the current one
            if(i == editItem)
            {
                listLayout.addView(createEditItem(item));
            }
            else
            {
                listLayout.addView(box);
            }

            i++;
        }
    }

    /**
     * Creates the view for an item that is being edited
     * @param item Item that is being edited
     * @return View with edittext and control buttons
     */
    public View createEditItem(Item item)
    {
        View editBox = inflater.inflate(R.layout.checkbox_edit_item, null);

        //Setup checkbox
        CheckBox editCb = ((CheckBox) editBox.findViewById(R.id.checkbox));
        editCb.setChecked(item.done);

        //Setup edittext
        itemEdit = ((EditText) editBox.findViewById(R.id.itemName));
        itemEdit.setText(item.item);
        itemEdit.addTextChangedListener(this);

        //Setup append button to save current edits and close
        Button append = ((Button) editBox.findViewById(R.id.append));
        append.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Modify item and save
                if(itemEdit.getText().toString().equals(""))
                {
                    //Remove if blank
                    Data.getItems().remove(listItems.get(editItem));
                }
                else
                {
                    listItems.get(editItem).item = itemEdit.getText().toString();
                }
                save();

                //Close box and reload
                editItem = -1;
                setupList();
            }
        });

        //Setup remove button to remove current item
        Button remove = ((Button) editBox.findViewById(R.id.remove));
        remove.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Remove item and save
                Data.getItems().remove(listItems.get(editItem));
                save();

                //Close box and reload
                editItem = -1;
                setupList();
            }
        });

        //Setup cancel button to close edit box without saving changes
        Button cancel = ((Button) editBox.findViewById(R.id.cancel));
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Delete if new item
                if(listItems.get(editItem).item.equals(""))
                {
                    Data.getItems().remove(listItems.get(editItem));
                }
                //Close box and reload
                editItem = -1;
                setupList();
            }
        });
        return editBox;
    }

    /**
     * Adds a word to a list item view
     * @param tags List item view
     * @param item Item word is source from
     * @param word Word to add
     */
    public void addWord(FlowLayout tags, Item item, final String word)
    {
        //Makes sure it is not an empty word
        if(word.length() != 0)
        {
            if(word.charAt(0) == '#')
            {
                //Surround hashtags with box
                View wordView = inflater.inflate(R.layout.tag_list_item, null);
                TextView wordTextView = ((TextView) wordView.findViewById(R.id.tag));

                wordView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        DialogFragment dialog = new ListViewDialog();
                        Bundle bundle = new Bundle();
                        bundle.putStringArrayList("ITEMS", Data.getTagItems(word));
                        dialog.setArguments(bundle);
                        dialog.show(getFragmentManager(), "");
                    }
                });

                wordTextView.setText(word);
                wordTextView.setTextColor(Color.parseColor(item.color));
                tags.addView(wordView);
            }
            else if(word.charAt(0) == '@')
            {
                //Surround people tags with box
                View wordView = inflater.inflate(R.layout.tag_list_item, null);
                TextView wordTextView = ((TextView) wordView.findViewById(R.id.tag));

                wordView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        DialogFragment dialog = new ListViewDialog();
                        Bundle bundle = new Bundle();
                        bundle.putStringArrayList("ITEMS", Data.getTagItems(word));
                        dialog.setArguments(bundle);
                        dialog.show(getFragmentManager(), "");
                    }
                });

                wordTextView.setText(word);
                wordTextView.setTextColor(Color.parseColor(item.color));
                tags.addView(wordView);
            }
            else if(isDate(word))
            {
                //Surround date with box
                View wordView = inflater.inflate(R.layout.tag_list_item, null);
                TextView wordTextView = ((TextView) wordView.findViewById(R.id.tag));

                wordView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        DialogFragment dialog = new ListViewDialog();
                        Bundle bundle = new Bundle();
                        bundle.putStringArrayList("ITEMS", Data.getTagItems(word));
                        dialog.setArguments(bundle);
                        dialog.show(getFragmentManager(), "");
                    }
                });

                Calendar itemDate = Calendar.getInstance();
                itemDate.setTime(item.date);

                String returnWord = word;

                if(IO.getInstance().getBoolean(IO.DATES_AS_DAY, true))
                {
                    Calendar week = Calendar.getInstance();
                    week.add(Calendar.DAY_OF_YEAR, 7);
                    Calendar yesterday = Calendar.getInstance();
                    yesterday.add(Calendar.DAY_OF_YEAR, -1);
                    Calendar tomorrow = Calendar.getInstance();
                    tomorrow.add(Calendar.DAY_OF_YEAR, 1);
                    if(itemDate.before(tomorrow) && itemDate.after(Calendar.getInstance()))
                    {
                        //if it's today say today
                        returnWord = "Tomorrow";
                    }
                    else if(itemDate.after(yesterday) && itemDate.before(Calendar.getInstance()))
                    {
                        //if it's today say today
                        returnWord = "Today";
                    }
                    else if(itemDate.after(yesterday) && itemDate.before(week))
                    {
                        //if it's within the next week say the day
                        switch (itemDate.get(Calendar.DAY_OF_WEEK))
                        {
                            case 0:
                                returnWord = "Saturday";
                                break;
                            case 1:
                                returnWord = "Sunday";
                                break;
                            case 2:
                                returnWord = "Monday";
                                break;
                            case 3:
                                returnWord = "Tuesday";
                                break;
                            case 4:
                                returnWord = "Wednesday";
                                break;
                            case 5:
                                returnWord = "Thursday";
                                break;
                            case 6:
                                returnWord = "Friday";
                                break;
                        }
                    }
                    else if(itemDate.after(week) && IO.getInstance().getBoolean(IO.DATES_AS_DAYS_UNTIL, false))
                    {
                        returnWord = (itemDate.get(Calendar.DAY_OF_YEAR) - Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) + " days";
                    }
                }
                else if(IO.getInstance().getBoolean(IO.DATES_AS_DAYS_UNTIL, false))
                {
                    returnWord = (itemDate.get(Calendar.DAY_OF_YEAR) - Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) + " days";
                }

                wordTextView.setText(returnWord);
                wordTextView.setTextColor(Color.parseColor(item.color));
                tags.addView(wordView);
            }
            else
            {
                //Place word in view
                TextView wordView = new TextView(this);
                wordView.setText(word);
                wordView.setTextColor(Color.parseColor(item.color));
                wordView.setPadding(0, 4, 0, 4);
                tags.addView(wordView);
            }
            //Add space after every word
            TextView spaceView = new TextView(this);
            spaceView.setText(" ");
            tags.addView(spaceView);
        }
    }

    /**
     * Sets up all buttons on screen
     */
    public void setupButtons()
    {
        //Show done items button
        showDone = ((CheckBox) findViewById(R.id.showDone));
        showDone.setChecked(Data.getCurrentList().showDone);
        showDone.setOnClickListener(this);

        //Archive list button
        archive = ((ImageButton) findViewById(R.id.archive));
        archive.setOnClickListener(this);

        //Delete list button
        delete = ((ImageButton) findViewById(R.id.remove));
        delete.setOnClickListener(this);

        //List settings button
        listSettings = ((ImageButton) findViewById(R.id.listSettings));
        listSettings.setOnClickListener(this);
    }

    /**
     * Sets up floating action button
     */
    public void setupFab()
    {
        //Add list floating action button
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_white));
        fab.setRippleColor(getResources().getColor(R.color.accent));
        fab.setOnClickListener(this);
    }

    /**
     * Determines if a given word is a date
     * @param word A single word parsed from a list
     * @return Whether or not the word is a date
     */
    public boolean isDate(String word)
    {
        //Date must have a /
        if(word.contains("/"))
        {
            String[] parts = word.split("/");
            for(String part : parts)
            {
                //Parts must be numbers
                try {
                    Integer.parseInt(part);
                } catch(NumberFormatException e) {
                    return false;
                }
            }
            if(parts.length == 2 || parts.length == 3)
            {
                //Numbers must be reasonable date numbers
                int month = Integer.parseInt(parts[0]);
                int day = Integer.parseInt(parts[1]);
                if(IO.getInstance().getBoolean(IO.US_DATE_FORMAT_PREF, true) && month > 0 && month < 13 && day > 0 && day < 32)
                {
                    return true;
                }
                else if(!IO.getInstance().getBoolean(IO.US_DATE_FORMAT_PREF, true) && month > 0 && month < 32 && day > 0 && day < 13)
                {
                    return true;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    @Override
    public void onClick(View v)
    {
        if(v == showDone)
        {
            //Set showDone, save, and reload
            Data.getCurrentList().showDone = showDone.isChecked();
            save();
            setupList();
        }
        else if(v == addItem)
        {
            List<Item> items = Data.getItems();
            editItem = 0;
            for(Item item : items)
            {
                if(!item.done)
                {
                    editItem++;
                }
            }
            items.add(new Item("", false));
            setupList();
        }
        else if(v == archive)
        {
            //Open archive confirmation
            DialogFragment dialog = new ArchiveListDialog();
            dialog.show(getFragmentManager(), "");
        }
        else if(v == delete)
        {
            //Open delete confirmation
            DialogFragment dialog = new DeleteListDialog();
            dialog.show(getFragmentManager(), "");
        }
        else if(v == listSettings)
        {
            //Open list settings menu
            DialogFragment dialog = new ListSettingsDialog();
            dialog.show(getFragmentManager(), "");
        }
        else if(v == editTagsButton)
        {
            //Set edittags and reload tags
            editTags = true;
            setupTags();
        }
        else if(v == fab)
        {
            //Open new list dialog
            DialogFragment dialog = new NewListDialog();
            dialog.show(getFragmentManager(), "");
        }
        else
        {
            for (Map.Entry<Integer, View> entry : listViews.entrySet())
            {
                CheckBox cb = ((CheckBox) entry.getValue().findViewById(R.id.checkbox));
                if(v == cb)
                {
                    listItems.get(entry.getKey()).done = cb.isChecked();
                    save();
                }
                else if (v == entry.getValue().findViewById(R.id.tags))
                {
                    cb.setChecked(!cb.isChecked());
                    listItems.get(entry.getKey()).done = cb.isChecked();
                    save();
                }
            }
        }
    }


    @Override
    public boolean onLongClick(View v)
    {
        //Parse all items
        for (Map.Entry<Integer, View> entry : listViews.entrySet())
        {
            if (v == entry.getValue().findViewById(R.id.tags))
            {
                //Set the item to the editItem and reload
                editItem = entry.getKey();
                setupList();
            }
        }
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        if(parent == listSpinner)
        {
            //Save the selected item and reload
            saveCurrent(position);
            loadList();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent){}

    /**
     * Set and save a new current list
     * @param current Number of current list
     */
    public void saveCurrent(int current)
    {
        //Set it in data manager
        Data.setCurrent(current);

        //Save it to shared prefs
        SharedPreferences.Editor edit = IO.getInstance().getEditor();
        edit.putInt(IO.CURRENT_LIST_PREF, current);
        edit.commit();
    }

    /**
     * Sorts a list of items by if they are checked
     * @param items Unsorted list of items
     * @return Sorted list of items
     */
    public List<Item> sortByChecked(List<Item> items)
    {
        if(Data.getCurrentList().sortChecked)
        {
            List<Item> sorted = new ArrayList<>();
            for(Item item : items)
            {
                //First add all unchecked items
                if(!item.done)
                {
                    sorted.add(item);
                }
            }
            if(Data.getCurrentList().showDone)
            {
                //If we are showing completed items
                for(Item item : items)
                {
                    //Second add all checked items
                    if(item.done)
                    {
                        sorted.add(item);
                    }
                }
            }
            return sorted;
        }
        return items;
    }

    /**
     * Sorts a list of items by date
     * @param items Unsorted list of items
     * @return Sorted list of items
     */
    public List<Item> sortByDate(List<Item> items)
    {
        if(Data.getCurrentList().sortDate)
        {
            List<Item> sorted = new ArrayList<>();
            for(Item item : items)
            {
                if(sorted.size() > 0)
                {
                    System.out.println("Sorted: " + sorted.size());
                    for(int i = 0; i < sorted.size(); i++)
                    {
                        Item sItem = sorted.get(i);
                        System.out.println(item.date.toString() + " vs " + sItem.date.toString());
                        if(item.date.before(sItem.date))
                        {
                            sorted.add(i, item);
                            break;
                        }
                        else if(i == sorted.size() - 1)
                        {
                            sorted.add(item);
                            break;
                        }
                    }
                }
                else
                {
                    sorted.add(item);
                }
            }
            return sorted;
        }
        return items;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
        String[] words = s.toString().split(" ");
        String currentWord = words[words.length - 1];
        suggestionsBox = (LinearLayout) findViewById(R.id.suggestions);
        suggestionsBox.removeAllViews();
        if(currentWord.length() > 0)
        {
            if(currentWord.charAt(0) == '#')
            {
                currentWord = currentWord.substring(1);
                for(final String tag : Data.getTags())
                {
                    if(tag.length() >= currentWord.length())
                    {
                        String reducedTag = tag.substring(0, currentWord.length());
                        System.out.println(currentWord + " vs " + reducedTag);
                        if(reducedTag.equalsIgnoreCase(currentWord))
                        {
                            Button sugBut = new Button(this);
                            sugBut.setText("#" + tag);
                            sugBut.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    String itemString = itemEdit.getText().toString();
                                    itemString = itemString.replace(itemString.substring(itemString.lastIndexOf(" ") + 1), "#" + tag + " ");
                                    itemEdit.setText(itemString);
                                    itemEdit.setSelection(itemEdit.getText().length() );
                                    suggestionsBox.removeAllViews();
                                }
                            });
                            suggestionsBox.addView(sugBut);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s)
    {

    }

    /**
     * Runs IO.saveAndSync() and puts up a Snackbar
     */
    public void save()
    {
        if(IO.getInstance().checkNetwork())
        {
        }
        IO.getInstance().saveAndSync();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
}