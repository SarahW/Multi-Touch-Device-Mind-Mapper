/*
 * Copyright (C) 2010 Eric Harlow (inner classes from original source)
 * 
 * Copyright (C) 2011 Sarah Will (modifications)
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
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ericharlow.DragNDrop.DragListener;
import com.ericharlow.DragNDrop.DragNDropAdapter;
import com.ericharlow.DragNDrop.DragNDropListViewMod;
import com.ericharlow.DragNDrop.DropListener;
import com.ericharlow.DragNDrop.RemoveListener;

/**
 * <p>
 * The transfer activity. Includes a list of all previously created idea texts
 * which can then be transferred to the connected bluetooth server host via drag
 * and drop on a specified area of the activity.
 * </p>
 * 
 * <p>
 * The drag and drop list behaviour is adapted from the DragAndDrop project by
 * Eric Harlow, see https://github.com/ericharlow/TICEWidgets, see
 * DragNDropListActivity
 * </p>
 * 
 * <p>
 * Modified 2011-09<br>
 * - added specific transfer activity code<br>
 * - added database support for drag and drop list<br>
 * - added code for checking drop position<br>
 * - added code for transfer to table<br>
 * </p>
 * 
 * 
 * @author Eric Harlow (inner classes/listeners)
 * @author (Modified by) Sarah Will
 * 
 * @version 1.0
 * 
 */
@SuppressWarnings("synthetic-access")
public class TransferActivity extends Activity {

    // Debugging
    private static final String  TAG = "TransferActivity"; //$NON-NLS-1$
    private static final boolean D   = true;

    // Layout view
    /** The title text view */
    private TextView             mTitle;
    /** The table image that ideas can be dropped onto */
    private ImageView            tableImage;
    /** The list of ideas that allows drag and drop behaviour */
    private DragNDropListViewMod ideaListView;

    /* **********Object methods********** */
    /**
     * Initializes the GUI of the TransferActivity.
     * 
     */
    private void initializeGUI() {

        if (Debugging.D) {
            Log.d(TAG, "Entering initializeGUI()"); //$NON-NLS-1$
        }

        setContentView(R.layout.transfer_activity);

        // Get views
        this.tableImage = (ImageView) findViewById(R.id.table_image);

        this.ideaListView = (DragNDropListViewMod) findViewById(R.id.list_of_ideas_transfer);
        this.ideaListView.setEmptyView(findViewById(R.id.empty_list));

        // Add drag and drop listeners
        this.ideaListView.setDropListener(this.mDropListener);
        this.ideaListView.setRemoveListener(this.mRemoveListener);
        this.ideaListView.setDragListener(this.mDragListener);

        if (Debugging.D) {
            Log.d(TAG, "Leaving initializeGUI()"); //$NON-NLS-1$
        }

    }

    /**
     * Fills the listView with the idea text database entries.
     */
    public void populateIdeaList() {

        if (Debugging.D) {
            Log.d(TAG, "Entering populateToDoList()"); //$NON-NLS-1$
        }

        // Get cursor in reversed order (new entries at the top)
        Cursor dbCursor = TabBarActivity.myTabLayout.getdBAdapter()
                .getAllIdeaItemsReversedCursor();

        if (dbCursor != null) {
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

            // Notify adapter of changes
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
     * Updates the listView widget by refreshing the cursor and notifying the
     * cursor adapter if ideas have been removed by dropping them onto the table
     * image.
     */
    private void updateList() {

        if (Debugging.D) {
            Log.d(TAG, "Entering updateList()"); //$NON-NLS-1$
        }

        // Refresh cursor
        // TODO: (deprecated) blocking operation, new Thread?
        TabBarActivity.myTabLayout.getDbCursor().requery();

        // Notify
        TabBarActivity.myTabLayout.getSimpleCursorAdapter()
                .notifyDataSetChanged();
        if (Debugging.D) {
            Log.d(TAG, "Leaving updateList()"); //$NON-NLS-1$
        }
    }

    /**
     * Checks whether a screen position lies within the table image
     * 
     * @param x
     *            the x position
     * @param y
     *            the y position
     * @return true if the position lies within the table image
     */
    public boolean checkListViewItemPosForImageView(int x, int y) {

        if (Debugging.D) {
            Log.d(TAG, "checkPositionForTableImage(x=" + x + ", y=" + y + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

        int listItemDropX = x;
        int listItemDropY = y;

        // Transfer listView coordinates to global coordinates
        int topListY = this.ideaListView.getTop();
        int topListX = this.ideaListView.getLeft();
        // if (Debugging.D) {
        //     Log.d(TAG, "topListY: " + topListY); //$NON-NLS-1$
        //    Log.d(TAG, "topListX: " + topListX); //$NON-NLS-1$
        // }

        // Get positions for listView layout
        ViewGroup listViewLayout = (ViewGroup) findViewById(R.id.list_of_ideas_parent_layout);
        int listViewLayoutY = listViewLayout.getTop();
        int listViewLayoutX = listViewLayout.getLeft();
        // if (Debugging.D) {
        //     Log.d(TAG, "listViewLayoutX: " + listViewLayoutX); //$NON-NLS-1$
        //     Log.d(TAG, "listViewLayoutY: " + listViewLayoutX); //$NON-NLS-1$
        // }

        // Get positions for uppermost layout
        ViewGroup uppermostLayout = (ViewGroup) findViewById(R.id.frame_layout_top);
        int upperMostLayoutY = uppermostLayout.getTop();
        int upperMostLayoutX = uppermostLayout.getLeft();
        // if (Debugging.D) {
        //     Log.d(TAG, "upperMostLayoutY: " + upperMostLayoutY); //$NON-NLS-1$
        //     Log.d(TAG, "upperMostLayoutX: " + upperMostLayoutY); //$NON-NLS-1$
        // }

        // Correct given coordinates
        // listItemDropX += topListX + listViewLayoutX + upperMostLayoutX;
        listItemDropY += topListY + listViewLayoutY + upperMostLayoutY;

        // Get table image positions
        int topY = this.tableImage.getTop();
        int leftX = this.tableImage.getLeft();
        int bottomY = this.tableImage.getBottom();
        int rightX = this.tableImage.getRight();

        // Add layout offsets
        topY += upperMostLayoutY;
        leftX += upperMostLayoutX;
        bottomY += upperMostLayoutY;
        rightX += upperMostLayoutX;

        // if (Debugging.D) {
        //    Log.d(TAG, "listItemDropX:" + listItemDropX); //$NON-NLS-1$
        //     Log.d(TAG, "listItemDropY:" + listItemDropY); //$NON-NLS-1$

        //     Log.d(TAG, "TopY of ImageView:" + topY); //$NON-NLS-1$
        //     Log.d(TAG, "leftX of ImageView:" + leftX); //$NON-NLS-1$
        //     Log.d(TAG, "bottomY of ImageView:" + bottomY); //$NON-NLS-1$
        //     Log.d(TAG, "rightX of ImageView:" + rightX); //$NON-NLS-1$
        // }

        // Check if drop position lies within image area
        if (listItemDropX >= leftX && listItemDropX <= rightX
                && listItemDropY >= topY && listItemDropY <= bottomY) {
            if (Debugging.D) {
                Log.d(TAG, "Leaving checkPositionForTableImage(): true"); //$NON-NLS-1$
            }

            return true;
        }
        if (Debugging.D) {
            Log.d(TAG, "Leaving checkPositionForTableImage(): false"); //$NON-NLS-1$
        }
        return false;

    }

    /**
     * Sends a given string text to the connected bluetooth remote device (the
     * multi touch table)
     * 
     * @param text
     *            the text to be sent
     * 
     * @return true if text could be sent successfully
     */
    public boolean sendStringToRemoteDevice(String text) {

        if (Debugging.D) {
            Log.d(TAG, "Entering sendStringToTable(text=" + text + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // Get active bluetooth command service
        BluetoothCommandService commandService = TabBarActivity.myTabLayout
                .getmCommandService();
        if (commandService != null) {

            String title = TabBarActivity.myTabLayout.getmTitle().getText()
                    .toString();

            // Check if we are connected to a bt server via the title
            // which is always set when connection changes occur
            // TODO: ugly, maybe there is a cleaner way to check this
            if (title.contains(getString(R.string.title_connected_to))) {

                // Send text via output stream
                commandService.writeMessage(text);

                // Show toast
                String toastTxt = getString(R.string.toast_idea_transferred_pt1)
                        + text + getString(R.string.toast_idea_transferred_pt2);
                Toast.makeText(TransferActivity.this, toastTxt,
                        Toast.LENGTH_SHORT).show();

                // Delete string from database
                // TODO: grey out in list? Then list states would have to be
                // added to the database as well!
                TabBarActivity.myTabLayout.getdBAdapter()
                        .removeIdeaItem((text));

                // Notify list adapter of changes
                updateList();

                if (Debugging.D) {
                    Log.d(TAG, "Leaving sendStringToTable(): true"); //$NON-NLS-1$
                }
                return true;

            }
            // We are not connected to a remote device
            // Show toast
            String toastTxt = getString(R.string.toast_idea_not_transferred_pt1)
                    + " " //$NON-NLS-1$
                    + getString(R.string.toast_idea_not_transferred_pt2);
            Toast.makeText(TransferActivity.this, toastTxt, Toast.LENGTH_SHORT)
                    .show();

            if (Debugging.D) {
                Log.d(TAG, "Leaving sendStringToTable(): false"); //$NON-NLS-1$
            }
            return false;

        }
        // There is no bluetooth available
        // Show toast
        String toastTxt = getString(R.string.toast_idea_not_transferred_pt1)
                + " " //$NON-NLS-1$ 
                + getString(R.string.toast_idea_not_transferred_pt2);
        Toast.makeText(TransferActivity.this, toastTxt, Toast.LENGTH_SHORT)
                .show();

        if (Debugging.D) {
            Log.d(TAG, "Leaving sendStringToTable(): false"); //$NON-NLS-1$
        }
        return false;

    }

    /**
     * Shows a help dialog for the TransferActivity..
     * 
     */
    private void showHelpDialog() {
        Log.d(TAG, "Entering showHelpDialog()"); //$NON-NLS-1$

        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_help_title)
                .setMessage(R.string.dialog_help_message_transfer)
                .setPositiveButton(R.string.dialog_help_bt_ok,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                // TODO Auto-generated method stub

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
     *            the saved instance state
     * */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Debugging.D) {
            Log.d(TAG,
                    "Entering onCreate(savedInstanceState=" + savedInstanceState //$NON-NLS-1$
                            + ")"); //$NON-NLS-1$
        }

        initializeGUI();

    }

    /**
     * Called after onRestoreInstanceState(Bundle), onRestart(), or onPause().
     * Sets the window title and fills the idea list with the database entries.
     * 
     * 
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (Debugging.D) {
            Log.d(TAG, "Entering onResume()"); //$NON-NLS-1$
        }

        // Set the window feature custom title
        TabBarActivity.myTabLayout.getWindow().setFeatureInt(
                Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);

        // Set up the custom title
        this.mTitle = (TextView) TabBarActivity.myTabLayout
                .findViewById(R.id.title_left_text);
        this.mTitle.setText(R.string.title_transfer_activity);
        this.mTitle = (TextView) TabBarActivity.myTabLayout
                .findViewById(R.id.title_right_text);

        // Fill list with database entries
        populateIdeaList();

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

        if (Debugging.D) {
            Log.d(TAG, "Entering onPause()"); //$NON-NLS-1$
        }

        // Stop managing the db cursor
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
     * while this activity is running. As of now, nothing is done here.
     * 
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (Debugging.D) {
            Log.d(TAG, "Entering onConfigurationChanged(newConfig=" + newConfig //$NON-NLS-1$
                    + ")"); //$NON-NLS-1$
        }

        // Do nothing

    }

    /* **********Inner classes********** */
    /**
     * <p>
     * The drop listener.
     * </p>
     * 
     * <p>
     * author Eric Harlow
     * </p>
     */
    private DropListener   mDropListener   = new DropListener() {
                                               @Override
                                               public void onDrop(int from,
                                                       int to) {

                                                   ListAdapter adapter = TransferActivity.this.ideaListView
                                                           .getAdapter();

                                                   if (adapter instanceof DragNDropAdapter) {
                                                       ((DragNDropAdapter) adapter)
                                                               .onDrop(from, to);
                                                       TransferActivity.this.ideaListView
                                                               .invalidateViews();

                                                   }
                                               }
                                           };

    /**
     * <p>
     * The remove listener.
     * </p>
     * 
     * <p>
     * author Eric Harlow
     * </p>
     */
    private RemoveListener mRemoveListener = new RemoveListener() {
                                               @Override
                                               public void onRemove(int which) {
                                                   ListAdapter adapter = TransferActivity.this.ideaListView
                                                           .getAdapter();
                                                   if (adapter instanceof DragNDropAdapter) {
                                                       ((DragNDropAdapter) adapter)
                                                               .onRemove(which);
                                                       TransferActivity.this.ideaListView
                                                               .invalidateViews();
                                                   }
                                               }
                                           };

    /**
     * <p>
     * The drag listener.
     * </p>
     * 
     * <p>
     * Modified 2011-09<br>
     * - modified backgroundColor
     * </p>
     * 
     * <p>
     * author Eric Harlow
     * </p>
     * <p>
     * author (Modified by) Sarah Will
     * </p>
     */
    private DragListener   mDragListener   = new DragListener() {

                                               /**
                                                * The background color of a
                                                * dragged list item
                                                */
                                               int backgroundColor = 0xAA666666;
                                               /**
                                                * The member storing the
                                                * original default color of a
                                                * dragged list item
                                                */
                                               int defaultBackgroundColor;

                                               @Override
                                               public void onDrag(int x, int y,
                                                       ListView listView) {
                                                   // TODO Auto-generated
                                                   // method stub
                                               }

                                               @Override
                                               public void onStartDrag(
                                                       View itemView) {
                                                   itemView.setVisibility(View.INVISIBLE);
                                                   this.defaultBackgroundColor = itemView
                                                           .getDrawingCacheBackgroundColor();
                                                   itemView.setBackgroundColor(this.backgroundColor);
                                                   ImageView iv = (ImageView) itemView
                                                           .findViewById(R.id.ImageView01);
                                                   if (iv != null) {
                                                       iv.setVisibility(View.INVISIBLE);
                                                   }
                                               }

                                               @Override
                                               public void onStopDrag(
                                                       View itemView) {
                                                   itemView.setVisibility(View.VISIBLE);
                                                   itemView.setBackgroundColor(this.defaultBackgroundColor);
                                                   ImageView iv = (ImageView) itemView
                                                           .findViewById(R.id.ImageView01);

                                                   if (iv != null) {
                                                       iv.setVisibility(View.VISIBLE);
                                                   }
                                               }

                                           };

}
