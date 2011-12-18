/*
 * Copyright (C) 2011 Sarah Will
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package de.sarahw.ma.phone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The IdeaNode text creation activity. Includes a edit field, a create button
 * and the current list of ideaNode texts.
 * 
 * 
 * @author Sarah Will
 * 
 * @version 1.0
 * 
 */
@SuppressWarnings("synthetic-access")
public class IdeaCreationActivity extends Activity {

    // Debugging
    private static final String TAG = "IdeaCreationActivity"; //$NON-NLS-1$

    // Layout view
    /** The title text view */
    private TextView            mTitle;
    /** The edit text view to enter ideas */
    private EditText            textEntry;
    /** The button to store ideas */
    private Button              buttonSend;
    /** The list view holding all stored ideas */
    private ListView            ideaListView;

    /* **********Object methods********** */
    /**
     * Initializes the GUI for the IdeaCreationActivity.
     * 
     */
    private void initializeGUI() {

        setContentView(R.layout.idea_creation_activity);

        this.textEntry = (EditText) findViewById(R.id.text_message);

        this.textEntry.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

                String changedString = s.toString();

                // SQL error workaround:
                // If the string contains an apostrophe,
                // remove it and show a toast
                if (changedString.contains("'")) { //$NON-NLS-1$

                    changedString = changedString.replaceAll("'", ""); //$NON-NLS-1$//$NON-NLS-2$

                    s.clear();

                    s.append(changedString);

                    // Show toast
                    Toast.makeText(IdeaCreationActivity.this,
                            getString(R.string.toast_apostrophe_not_allowed),
                            Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
                // Do nothing
            }
        });

        // Find and set up the ListView for created ideas
        this.ideaListView = (ListView) findViewById(R.id.list_of_ideas_creation);

        // this.ideaListView.setAdapter(this.createdIdeasArrayAdapter);
        this.ideaListView.setEmptyView(findViewById(R.id.empty_list));

        // Behavior onLongClick: remove item
        this.ideaListView
                .setOnItemLongClickListener(new OnItemLongClickListener() {

                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent,
                            View v, int position, long id) {

                        String selectedIdeaText = ((TextView) v).getText()
                                .toString();

                        // delete current list item

                        TabBarActivity.myTabLayout.getdBAdapter()
                                .removeIdeaItem((selectedIdeaText));

                        // notify of changes
                        updateList();

                        // show toast
                        String toastTxt = getString(R.string.toast_idea_deleted_pt1)
                                + selectedIdeaText
                                + getString(R.string.toast_idea_deleted_pt2);
                        Toast.makeText(IdeaCreationActivity.this, toastTxt,
                                Toast.LENGTH_SHORT).show();

                        return true;
                    }
                });

        this.buttonSend = (Button) findViewById(R.id.button_send);
        this.buttonSend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String entryFieldTxt = IdeaCreationActivity.this.textEntry
                        .getText().toString();

                if (!(entryFieldTxt.length() == 0)) {

                    // Check if item is already part of the list
                    if (!(TabBarActivity.myTabLayout.getdBAdapter()
                            .checkIdeaItem(entryFieldTxt))) {

                        // if(IdeaCreationActivity.this.ideaListView.getAdapter().)
                        TabBarActivity.myTabLayout.getdBAdapter()
                                .getAllIdeaItemsReversedCursor();

                        // Add idea to DB
                        TabBarActivity.myTabLayout.getdBAdapter()
                                .insertIdeaItem(entryFieldTxt);

                        // Notify of changes
                        updateList();

                        // Show toast
                        String toastTxt = getString(R.string.toast_new_idea_created_pt1)
                                + entryFieldTxt
                                + getString(R.string.toast_new_idea_created_pt2);
                        Toast.makeText(IdeaCreationActivity.this, toastTxt,
                                Toast.LENGTH_SHORT).show();

                    } else {
                        // Show toast
                        String toastTxt = getString(R.string.toast_duplicate_idea_pt1)
                                + entryFieldTxt
                                + getString(R.string.toast_duplicate_idea_pt2);
                        Toast.makeText(IdeaCreationActivity.this, toastTxt,
                                Toast.LENGTH_SHORT).show();
                    }

                }

                IdeaCreationActivity.this.textEntry.setText(""); //$NON-NLS-1$

            }

        });

    }

    /**
     * Fills the listView with the idea text database entries.
     */
    public void populateIdeaList() {

        if (Debugging.D) {
            Log.d(TAG, "Entering populateToDoList()"); //$NON-NLS-1$
        }

        Cursor dbCursor = TabBarActivity.myTabLayout.getdBAdapter()
                .getAllIdeaItemsReversedCursor();

        if (dbCursor != null) {
            if (Debugging.D) {
                Log.d(TAG, "Cursor is valid"); //$NON-NLS-1$
            }
            TabBarActivity.myTabLayout.setDbCursor(dbCursor);

            startManagingCursor(TabBarActivity.myTabLayout.getDbCursor());

            String[] columnName = { DBAdapter.KEY_IDEA };
            int[] rowNumber = { android.R.id.text1 };

            // Register adapter
            TabBarActivity.myTabLayout
                    .setSimpleCursorAdapter(new SimpleCursorAdapter(this,
                            android.R.layout.simple_list_item_1,
                            TabBarActivity.myTabLayout.getDbCursor(),
                            columnName, rowNumber));

            this.ideaListView.setAdapter(TabBarActivity.myTabLayout
                    .getSimpleCursorAdapter());

            // notify
            TabBarActivity.myTabLayout.getSimpleCursorAdapter()
                    .notifyDataSetChanged();

        } else {
            Log.e(TAG, "Cursor for DB is null!"); //$NON-NLS-1$
        }

        if (Debugging.D) {
            Log.d(TAG, "Leaving populateToDoList()"); //$NON-NLS-1$
        }

    }

    /**
     * Updates the listView widget when ideas have been added or removed.
     */
    private void updateList() {

        if (Debugging.D) {
            Log.d(TAG, "Entering updateList()"); //$NON-NLS-1$
        }
        // refresh cursor
        TabBarActivity.myTabLayout.getDbCursor().requery();

        // notify
        TabBarActivity.myTabLayout.getSimpleCursorAdapter()
                .notifyDataSetChanged();

        if (Debugging.D) {
            Log.d(TAG, "Leaving updateList()"); //$NON-NLS-1$
        }
    }

    /**
     * Shows a help dialog for the IdeaCreationActivity.
     * 
     */
    private void showHelpDialog() {
        if (Debugging.D) {
            Log.d(TAG, "Entering showHelpDialog()"); //$NON-NLS-1$
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_help_title)
                .setMessage(R.string.dialog_help_message_creation)
                .setPositiveButton(R.string.dialog_help_bt_ok,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                // Do nothing
                            }
                        }).show();

        if (Debugging.D) {
            Log.d(TAG, "Leaving showHelpDialog()"); //$NON-NLS-1$
        }

    }

    /* ********Overridden methods******** */
    /**
     * Called when the activity is first created. Initializes the GUI.
     * 
     * @param savedInstanceState
     *            the saved instance state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeGUI();

    }

    /**
     * Called after onRestoreInstanceState(Bundle), onRestart(), or onPause().
     * Sets the window title and populates the idea list from the database.
     * 
     */
    @Override
    protected void onResume() {

        super.onResume();

        // Set window custom title layout
        TabBarActivity.myTabLayout.getWindow().setFeatureInt(
                Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);

        // Set up the custom title
        this.mTitle = (TextView) TabBarActivity.myTabLayout
                .findViewById(R.id.title_left_text);

        this.mTitle.setText(R.string.title_creation_activity);
        this.mTitle = (TextView) TabBarActivity.myTabLayout
                .findViewById(R.id.title_right_text);

        // Get database entries
        populateIdeaList();

        // Close the keyboard (not working!? probably too early)
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.textEntry.getApplicationWindowToken(),
                0);
    }

    /**
     * Called as part of the activity lifecycle when an activity is going into
     * the background, but has not (yet) been killed. Stops managing the
     * database cursor.
     * 
     */
    @Override
    protected void onPause() {
        super.onPause();

        // Stop managing the database cursor
        stopManagingCursor(TabBarActivity.myTabLayout.getDbCursor());
    }

    /**
     * Inflates an options menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Performs the action associated with the selected menu item.
     * 
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.help:
                showHelpDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Called by the system when the device configuration changes (see manifest)
     * while this activity is running. Does nothing as of now.
     * 
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // initializeGUI();
    }

}
