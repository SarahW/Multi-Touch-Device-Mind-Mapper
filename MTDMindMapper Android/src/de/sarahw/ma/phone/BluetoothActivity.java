/*
 * Copyright (C) 2009 The Android Open Source Project (original source)
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

import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.commonsware.cwac.merge.MergeAdapter;

/**
 * <p>
 * The BluetoothActivity. It lists any paired devices and devices detected in
 * the area after discovery as well as the connected device. When a device (not
 * connected) is chosen by the user, a connection is established and on success
 * the MAC address of the device is sent back to the TabBarActivity via a
 * Broadcast. The connection process may be canceled via a button press. After a
 * connection has been established, the connected device is displayed at the top
 * of the activity. On tapping this device name the connection can be canceled
 * again.
 * </p>
 * 
 * <p>
 * Device list code adapted from
 * https://github.com/luugiathuy/Remote-Bluetooth-Android
 * (DeviceListActivity.java)
 * </p>
 * 
 * <p>
 * Modified 2011-09-18
 * </p>
 * 
 * @author Luu Gia Thuy (DeviceListActivity.java)
 * @author Sarah Will (BluetoothActivity.java modifications)
 * 
 * @version 1.0
 * 
 */
@SuppressWarnings("synthetic-access")
public class BluetoothActivity extends Activity {

    // Debugging
    private static final String   TAG                  = "BluetoothActivity";   //$NON-NLS-1$

    // Return Intent extra
    /** The intent extra sent when getting the device address */
    protected static String       EXTRA_DEVICE_ADDRESS = "device_address";      //$NON-NLS-1$
    /** The intent extra sent when getting full the device address */
    protected static final String DEVICE_ADDRESS_FULL  = "full_device_address"; //$NON-NLS-1$
    /** The intent extra sent selecting a device out of the list */
    protected static String       DEVICE_LIST_SELECTED = "device_selected";     //$NON-NLS-1$
    /** The intent extra sent when connecting is canceled */
    protected static String       CONNECTING_CANCELED  = "connecting_cancelled"; //$NON-NLS-1$

    // Member fields
    /** The bluetooth adapter instance */
    private BluetoothAdapter      mBtAdapter;
    /** The array adapter for the connected devices list view */
    private ArrayAdapter<String>  mConnectedDeviceArrayAdapter;
    /** The array adapter for the paired devices list view */
    private ArrayAdapter<String>  mPairedDevicesArrayAdapter;
    /** The array adapter for the new devices list view */
    private ArrayAdapter<String>  mNewDevicesArrayAdapter;
    /**
     * The merger adapter that holds the lists and titles for paired and new
     * devices
     */
    private MergeAdapter          adapter;

    // Layout view
    /** The title text view */
    private TextView              mTitle;

    /** The listView that holds all lists and the list title views */
    private ListView              mergeList;

    /** The text view that informs the user that the device is connecting */
    private TextView              connectionInfo;
    /** The progress bar indicating that the device is connecting */
    private ProgressBar           connectionProgress;
    /** The button that allows canceling the connection process */
    private Button                connectionCancelButton;

    /** The title text view for the list with the connected device */
    private TextView              connectedDeviceListTitle;
    /** The list containing the connected device */
    private ListView              connectedDeviceList;
    /**
     * The name and mac address of the connected device, empty when not
     * connected
     */
    private String                connectedDevice      = "";                    //$NON-NLS-1$

    /** The title text view for the paired devices list */
    private TextView              pairedDevicesTitle;
    /** The list of paired devices */
    private ListView              pairedDevicesList;

    /** The title text view for the newly found devices */
    private TextView              newDevicesListTitle;
    /** The list of newly found devices */
    private ListView              newDevicesList;

    /** The button that allows scanning for new devices */
    private Button                scanButton;
    /** The progress bar indicating that a device scan is in progress */
    private ProgressBar           scanProgress;

    // Theme and widget storage values
    /**
     * The ColorStateList storing the theme colors for the textView items in a
     * listView
     */
    private ColorStateList        themeColors;
    /** The text size of the textView items in a listView */
    private float                 listTextSize         = 0.0f;

    /* **********Object methods********** */

    private void initializeGUI() {

        if (Debugging.D) {
            Log.d(TAG, "Entering initializeGUI()"); //$NON-NLS-1$
        }
        // Set layout
        setContentView(R.layout.btdevice_list);

        // Get view references
        this.connectionProgress = (ProgressBar) findViewById(R.id.progress_bar_connecting);
        this.connectionInfo = (TextView) findViewById(R.id.info_connecting);

        this.connectedDeviceList = (ListView) findViewById(R.id.connected_devices);
        this.connectedDeviceListTitle = (TextView) findViewById(R.id.title_connected_devices);

        this.mergeList = (ListView) findViewById(R.id.list_view);

        this.scanProgress = (ProgressBar) findViewById(R.id.progress_bar);

        // Find and set up the ListView for connected devices
        // Set adapter for the connected device list
        this.mConnectedDeviceArrayAdapter = new ArrayAdapter<String>(this,
                R.layout.device_name);
        this.connectedDeviceList.setAdapter(this.mConnectedDeviceArrayAdapter);
        this.connectedDeviceList
                .setOnItemClickListener(this.mConnectedDeviceClickListener);

        // Create new merge adapter
        // to add views and lists to
        this.adapter = new MergeAdapter();

        // Create paired devices title and add to merge adapter
        this.pairedDevicesTitle = (TextView) buildPairedTitle();
        this.adapter.addView(this.pairedDevicesTitle);

        // Create list for paired devices and add to merge adapter
        this.pairedDevicesList = buildPairedList();
        this.mPairedDevicesArrayAdapter = buildPairedListAdapter();
        this.pairedDevicesList.setAdapter(this.mPairedDevicesArrayAdapter);
        this.adapter.addAdapter(this.mPairedDevicesArrayAdapter);

        // Create new devices title and add to merge adapter
        this.newDevicesListTitle = (TextView) buildNewDevicesTitle();
        this.adapter.addView(this.newDevicesListTitle);

        // Create list for new devices and add to merge adapter
        this.newDevicesList = buildNewDevicesList();
        this.mNewDevicesArrayAdapter = buildNewDevicesListAdapter();
        this.newDevicesList.setAdapter(this.mNewDevicesArrayAdapter);
        this.adapter.addAdapter(this.mNewDevicesArrayAdapter);

        // Set the adapter and the click listener for the merge list
        this.mergeList.setAdapter(this.adapter);
        this.mergeList.setOnItemClickListener(this.mDeviceClickListener);

        // Initialize the button to cancel the connection progress
        this.connectionCancelButton = (Button) findViewById(R.id.button_cancel_connection);
        this.connectionCancelButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                BluetoothActivity.this.connectionCancelButton.setEnabled(false);
                cancelConnection();
                setConnectionInfoViewText(getResources().getString(
                        R.string.info_bt_cancel_connecting));

            }
        });

        // Initialize the button to start the discovery process
        this.scanButton = (Button) findViewById(R.id.button_scan);
        this.scanButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                BluetoothActivity.this.scanButton.setEnabled(false);
                doDiscovery();

            }
        });

        // Broadcast receiver
        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(this.mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(this.mReceiver, filter);

        // Register for broadcasts when connection has been established
        filter = new IntentFilter(TabBarActivity.BT_CONNECTED);
        this.registerReceiver(this.connectionReceiver, filter);

        // Register for broadcasts when connection is being established
        filter = new IntentFilter(TabBarActivity.BT_CONNECTING);
        this.registerReceiver(this.connectionReceiver, filter);

        // Register for broadcasts when bt connection is listening or has no
        // state
        filter = new IntentFilter(TabBarActivity.BT_NONE_OR_LISTENING);
        this.registerReceiver(this.connectionReceiver, filter);

        // Get the local Bluetooth adapter
        this.mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        if (Debugging.D) {
            Log.d(TAG, "Leaving initializeGUI()"); //$NON-NLS-1$
        }

    }

    /**
     * Builds a title textView for the paired devices list.
     * 
     * @return the title text view
     */
    private View buildPairedTitle() {

        if (Debugging.D) {
            Log.d(TAG, "Entering buildPairedTitle()"); //$NON-NLS-1$
        }

        // Create title text view
        TextView result = buildTitle(
                getResources().getString(R.string.title_paired_devices),
                View.VISIBLE);

        if (Debugging.D) {
            Log.d(TAG, "Leaving buildPairedTitle(): " + result); //$NON-NLS-1$
        }

        return result;
    }

    /**
     * Builds a new title for the new devices list.
     * 
     * @return the title text view
     */
    private View buildNewDevicesTitle() {

        if (Debugging.D) {
            Log.d(TAG, "Entering buildNewDevicesTitle()"); //$NON-NLS-1$
        }

        // Create title text view
        TextView result = buildTitle(
                getResources().getString(R.string.title_other_devices),
                View.GONE);

        if (Debugging.D) {
            Log.d(TAG, "Leaving buildNewDevicesTitle(): " + result); //$NON-NLS-1$
        }

        return result;
    }

    /**
     * Builds a title text view with the given text and visibility flag.
     * 
     * @param text
     *            the title text
     * @param visibility
     *            the default visibility (View.GONE || View.INVISIBLE ||
     *            View.VISIBLE)
     * @return the title textView
     */
    private TextView buildTitle(String text, int visibility) {

        if (Debugging.D) {
            Log.d(TAG, "Entering buildTitle(text=" + text + ", visibility=" //$NON-NLS-1$ //$NON-NLS-2$
                    + visibility + ")"); //$NON-NLS-1$
        }

        // Create new text view
        TextView result = new TextView(this);

        // Set text
        result.setText(text);

        // Set visibility
        result.setVisibility(visibility);

        // Set background color
        result.setBackgroundColor(getResources().getColor(R.color.normal_grey));

        // Set padding in dp
        int padding_in_dp = 5; // 5 dp
        final float scale = getResources().getDisplayMetrics().density;
        int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
        result.setPadding(padding_in_px, 0, 0, 0);

        if (Debugging.D) {
            Log.d(TAG, "Leaving buildTitle(): " + result); //$NON-NLS-1$
        }

        return result;
    }

    /**
     * Builds the paired devices listView.
     * 
     * @return the listView
     */
    private ListView buildPairedList() {

        if (Debugging.D) {
            Log.d(TAG, "Entering buildPairedList()"); //$NON-NLS-1$
        }

        // Build visible list view
        ListView pairedList = buildListView(View.VISIBLE);

        if (Debugging.D) {
            Log.d(TAG, "Leaving buildPairedList(): " + pairedList); //$NON-NLS-1$
        }
        return pairedList;

    }

    /**
     * Builds the new devices listView.
     * 
     * @return the listView
     */
    private ListView buildNewDevicesList() {

        if (Debugging.D) {
            Log.d(TAG, "Entering buildNewDevicesList()"); //$NON-NLS-1$
        }

        // Build invisible/gone listView
        ListView newDevicesList = buildListView(View.GONE);

        if (Debugging.D) {
            Log.d(TAG, "Leaving buildNewDevicesList(): " + newDevicesList); //$NON-NLS-1$
        }
        return newDevicesList;
    }

    /**
     * Builds a listView with the given visibility.
     * 
     * @param visibility
     *            the visibility of the listView (View.GONE || View.INVISIBLE ||
     *            View.VISIBLE)
     * @return the listView
     */
    private ListView buildListView(int visibility) {

        if (Debugging.D) {
            Log.d(TAG, "Entering buildListView(visibility=" + visibility + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // Create new list
        ListView result = new ListView(this);

        result.setStackFromBottom(false);

        result.setVisibility(visibility);

        if (Debugging.D) {
            Log.d(TAG, "Leaving buildListView(): " + result); //$NON-NLS-1$
        }

        return result;

    }

    /**
     * Creates the list adapter for the new devices list
     * 
     * @return the list adapter
     */
    private ArrayAdapter<String> buildNewDevicesListAdapter() {

        if (Debugging.D) {
            Log.d(TAG, "Entering buildNewDevicesListAdapter()"); //$NON-NLS-1$ 
        }
        // Create a new string array adapter
        ArrayAdapter<String> newDeviceListAdapter = buildAdapter();

        if (Debugging.D) {
            Log.d(TAG,
                    "Leaving buildNewDevicesListAdapter(): " + newDeviceListAdapter); //$NON-NLS-1$ 
        }
        return newDeviceListAdapter;
    }

    /**
     * Creates the list adapter for the paired devices list.
     * 
     * @return the list adapter
     */
    private ArrayAdapter<String> buildPairedListAdapter() {

        if (Debugging.D) {
            Log.d(TAG, "Entering buildPairedListAdapter()"); //$NON-NLS-1$ 
        }
        // Create a new string array adapter
        ArrayAdapter<String> pairedListAdapter = buildAdapter();

        if (Debugging.D) {
            Log.d(TAG, "Leaving buildPairedListAdapter(): " + pairedListAdapter); //$NON-NLS-1$ 
        }

        return pairedListAdapter;
    }

    /**
     * Builds a new string array adapter for device names.
     * 
     * @return the string array adapter
     */
    private ArrayAdapter<String> buildAdapter() {

        if (Debugging.D) {
            Log.d(TAG, "Entering buildAdapter()"); //$NON-NLS-1$ 
        }

        // Create a new string array adapter for device names
        ArrayAdapter<String> stringAdapter = new ArrayAdapter<String>(this,
                R.layout.device_name);

        if (Debugging.D) {
            Log.d(TAG, "Leaving buildAdapter(): " + stringAdapter); //$NON-NLS-1$ 
        }
        return stringAdapter;

    }

    /**
     * Start device discover with the BluetoothAdapter.
     * 
     */
    private void doDiscovery() {
        if (Debugging.D) {
            Log.d(TAG, "Entering doDiscovery()"); //$NON-NLS-1$
        }

        // Indicate scanning via progress bar
        this.scanProgress.setVisibility(View.VISIBLE);

        // Turn on sub-title for new devices
        this.newDevicesListTitle.setVisibility(View.VISIBLE);

        // Empty new devices list
        this.mNewDevicesArrayAdapter.clear();

        // If we're already discovering, stop it
        if (this.mBtAdapter.isDiscovering()) {
            this.mBtAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        this.mBtAdapter.startDiscovery();

        if (Debugging.D) {
            Log.d(TAG, "Leaving doDiscovery()"); //$NON-NLS-1$
        }
    }

    /**
     * Cancels the discovery if the device is currently discovering
     * 
     */
    protected void cancelDiscovery() {

        if (Debugging.D) {
            Log.d(TAG, "Entering cancelDiscovery()"); //$NON-NLS-1$
        }
        // If discovering, cancel
        if (this.mBtAdapter.isDiscovering()) {
            this.mBtAdapter.cancelDiscovery();
        }

        if (Debugging.D) {
            Log.d(TAG, "Leaving cancelDiscovery()"); //$NON-NLS-1$
        }
    }

    /**
     * Cancels the current remote bluetooth connection.
     * 
     */
    protected void cancelConnection() {
        if (Debugging.D) {
            Log.d(TAG, "Entering cancelConnection()"); //$NON-NLS-1$
        }
        // Create and send the broadcast intent for canceling the connection
        Intent intent = new Intent();

        intent.setAction(BluetoothActivity.CONNECTING_CANCELED);

        sendBroadcast(intent);

        if (Debugging.D) {
            Log.d(TAG, "Leaving cancelConnection()"); //$NON-NLS-1$
        }

    }

    /**
     * Shows a help dialog for the Bluetooth Activity.
     * 
     */
    private void showHelpDialog() {
        if (Debugging.D) {
            Log.d(TAG, "Entering showHelpDialog()"); //$NON-NLS-1$
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_help_title)
                .setMessage(R.string.dialog_help_message_bluetooth)
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

    /**
     * Sets the lists of the activity enabled or disabled, which in turn changes
     * their text color, as well
     * 
     * @param enabled
     *            the enabled flag
     */
    protected void setListsEnabled(boolean enabled) {

        if (Debugging.D) {
            Log.d(TAG, "Entering setListsEnabled(" + enabled + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // En/Disable lists
        this.newDevicesList.setEnabled(enabled);
        this.pairedDevicesList.setEnabled(enabled);
        this.connectedDeviceList.setEnabled(enabled);
        this.mergeList.setEnabled(enabled);

        // Set the list title colors for the connected device list title
        setListTitlesEnabledColors(enabled, this.connectedDeviceListTitle);

        // Set the color of all list items of the connected device
        // and the merge list en/disabled
        setListItemsEnabledColor(enabled, this.connectedDeviceList);
        setListItemsEnabledColor(enabled, this.mergeList);

        if (Debugging.D) {
            Log.d(TAG, "Leaving setListsEnabled()"); //$NON-NLS-1$
        }

    }

    /**
     * Sets the text color for a list title (text views) depending on the
     * enabled flag.
     * 
     * @param enabled
     *            enabled flag
     * @param titleTextView
     *            the titleTextView
     */
    private void setListTitlesEnabledColors(boolean enabled,
            TextView titleTextView) {
        if (Debugging.D) {
            Log.d(TAG, "Entering setListTitlesEnabledColors(enabled=" + enabled //$NON-NLS-1$
                    + ", titleTextView =" + titleTextView + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // If enabled flag true set stored default text color
        if (enabled) {
            titleTextView.setTextColor(this.themeColors);

        } else {
            // Else set text dark grey
            titleTextView.setTextColor(getResources().getColor(
                    R.color.dark_grey));
        }

        if (Debugging.D) {
            Log.d(TAG, "Leaving setListTitlesEnabledColors()"); //$NON-NLS-1$
        }

    }

    /**
     * Sets the text color for all list view depending on the enabled flag.
     * 
     * @param enabled
     *            enabled flag
     * @param list
     *            the list
     */
    private void setListItemsEnabledColor(boolean enabled, ListView list) {

        if (Debugging.D) {
            Log.d(TAG, "Entering setListItemsEnabledColor(enabled=" + enabled //$NON-NLS-1$
                    + ", list =" + list + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // Get the number of items in the list
        int itemCount = list.getAdapter().getCount();

        Object item = null;

        // Set the text color for every item in the list
        // depending on the enabled flag
        for (int i = 0; i < itemCount; i++) {

            item = list.getChildAt(i);

            if (item != null && item instanceof TextView) {

                if (enabled) {
                    // If enabled flag true set stored default text color
                    ((TextView) item).setTextColor(this.themeColors);

                } else {
                    // Else set text dark grey
                    ((TextView) item).setTextColor(getResources().getColor(
                            R.color.dark_grey));
                }

            }

        }

        if (Debugging.D) {
            Log.d(TAG, "Leaving setListItemsEnabledColor()"); //$NON-NLS-1$
        }

    }

    /**
     * Gets different values from the listViews, e.g. values required for
     * setting and re-setting colors of list items
     */
    protected void getListTextViewValues() {

        if (Debugging.D) {
            Log.d(TAG, "Entering getListTextViewValues()"); //$NON-NLS-1$ 
        }

        // Get the first visible position of the list
        int firstPosition = this.connectedDeviceList.getFirstVisiblePosition();

        // Get the list item
        View item = this.connectedDeviceList.getChildAt(firstPosition);

        if (item instanceof TextView) {

            TextView listTextViewItem = (TextView) item;

            // Get the theme colors from the list item
            this.themeColors = listTextViewItem.getTextColors();

            // Get the text size from the list item
            this.listTextSize = listTextViewItem.getTextSize();
        }

        if (Debugging.D) {
            Log.d(TAG, "Entering getListTextViewValues()"); //$NON-NLS-1$ 
        }

    }

    /**
     * Sets the visibility of the cancel button.
     * 
     * @param visibility
     *            the visibility value (View.GONE || View.INVISIBLE ||
     *            View.VISIBLE)
     */
    protected void setCancelButtonVisible(int visibility) {

        if (Debugging.D) {
            Log.d(TAG, "Entering setCancelButtonVisible(" + visibility + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (visibility == View.VISIBLE || visibility == View.GONE
                || visibility == View.INVISIBLE) {

            // Set visibility if valid
            this.connectionCancelButton.setVisibility(visibility);

        }

        if (Debugging.D) {
            Log.d(TAG, "Leaving setCancelButtonVisible()"); //$NON-NLS-1$
        }

    }

    /**
     * Sets the visibility of the connection info text view
     * 
     * @param visibility
     *            the visibility value (View.GONE || View.INVISIBLE ||
     *            View.VISIBLE)
     */
    protected void setConnectionInfoVisible(int visibility) {
        if (Debugging.D) {
            Log.d(TAG, "Entering setConnectionInfoVisible(" + visibility + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (visibility == View.VISIBLE || visibility == View.GONE
                || visibility == View.INVISIBLE) {

            // Set visibility if valid
            this.connectionInfo.setVisibility(visibility);
            this.connectionProgress.setVisibility(visibility);

        }
        if (Debugging.D) {
            Log.d(TAG, "Leaving setConnectionInfoVisible()"); //$NON-NLS-1$
        }

    }

    /**
     * Sets the text size of the info text view displaying the connection
     * process
     */
    protected void setConnectionInfoViewTextSize() {
        if (Debugging.D) {
            Log.d(TAG, "Entering setConnectionInfoViewTextSize()"); //$NON-NLS-1$ 
        }

        // Set the connection info text size
        this.connectionInfo.setTextSize(this.listTextSize);

        if (Debugging.D) {
            Log.d(TAG, "Leaving setConnectionInfoViewTextSize()"); //$NON-NLS-1$ 
        }

    }

    /**
     * Sets the text of the info view displaying the connection process.
     * 
     * @param text
     */
    protected void setConnectionInfoViewText(String text) {
        if (Debugging.D) {
            Log.d(TAG, "Entering setConnectionInfoViewText()"); //$NON-NLS-1$
        }
        // Set the connection info text
        this.connectionInfo.setText(text);

        if (Debugging.D) {
            Log.d(TAG, "Leaving setConnectionInfoViewText()"); //$NON-NLS-1$ 
        }

    }

    /**
     * Sets the text of the item in the connectedDevice list via its adapter.
     * 
     * @param text
     *            the text of the list item
     */
    protected void setConnectedListText(String text) {

        if (Debugging.D) {
            Log.d(TAG, "Entering setConnectedListText()"); //$NON-NLS-1$ 
        }
        // Set connected device text in list
        this.mConnectedDeviceArrayAdapter.clear();
        this.mConnectedDeviceArrayAdapter.add(text);

        if (Debugging.D) {
            Log.d(TAG, "Leaving setConnectedListText()"); //$NON-NLS-1$ 
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

        if (Debugging.D) {
            Log.d(TAG,
                    "Entering onCreate(savedInstanceState=" + savedInstanceState //$NON-NLS-1$
                            + ")"); //$NON-NLS-1$
        }

        // Initialize GUI
        initializeGUI();

        if (Debugging.D) {
            Log.d(TAG, "Leaving onCreate()"); //$NON-NLS-1$
        }

    }

    /**
     * Called after onRestoreInstanceState(Bundle), onRestart(), or onPause().
     * Sets the window title, and (re-)fills the lists of the activity.
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
        this.mTitle.setText(R.string.title_bt_activity);
        this.mTitle = (TextView) TabBarActivity.myTabLayout
                .findViewById(R.id.title_right_text);

        // Get a set of currently paired devices
        if (this.mBtAdapter != null) {
            // Get the bonded devices
            Set<BluetoothDevice> pairedDevices = this.mBtAdapter
                    .getBondedDevices();

            // Clear the paired devices list
            this.mPairedDevicesArrayAdapter.clear();

            // If there are paired devices, add each one to the ArrayAdapter
            if (pairedDevices.size() > 0) {

                this.pairedDevicesTitle.setVisibility(View.VISIBLE);
                for (BluetoothDevice device : pairedDevices) {
                    this.mPairedDevicesArrayAdapter.add(device.getName() + "\n" //$NON-NLS-1$
                            + device.getAddress());
                }
            } else {
                String noDevices = getResources().getText(R.string.none_paired)
                        .toString();
                this.mPairedDevicesArrayAdapter.add(noDevices);
            }

            // Clear the connected devices list
            this.mConnectedDeviceArrayAdapter.clear();

            // Check if there was a connected device stored
            if (this.connectedDevice.length() != 0) {
                this.connectedDeviceListTitle.setVisibility(View.VISIBLE);

                this.mConnectedDeviceArrayAdapter.add(this.connectedDevice);

            } else {

                String noDevice = getResources().getText(
                        R.string.none_connected).toString();
                this.mConnectedDeviceArrayAdapter.add(noDevice);

            }

        } else {
            // Show toast
            Toast.makeText(
                    this,
                    getResources().getText(R.string.toast_bt_not_supported)
                            .toString(), Toast.LENGTH_LONG).show();

        }

        if (Debugging.D) {
            Log.d(TAG, "Leaving onResume()"); //$NON-NLS-1$
        }

    }

    /**
     * Called as part of the activity lifecycle when an activity is going into
     * the background, but has not (yet) been killed. Cancels discovery.
     * 
     */
    @Override
    protected void onPause() {
        super.onPause();

        if (Debugging.D) {
            Log.d(TAG, "Entering onPause()"); //$NON-NLS-1$
        }

        // Make sure we're not doing discovery anymore
        // when leaving the bluetooth activity
        if (this.mBtAdapter != null) {
            this.mBtAdapter.cancelDiscovery();
        }

        if (Debugging.D) {
            Log.d(TAG, "Leaving onPause()"); //$NON-NLS-1$
        }

    }

    /**
     * Called when activity is destroyed via finish() or via system memory clean
     * up. Unregisters the broadcast receiver.
     * 
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (Debugging.D) {
            Log.d(TAG, "Entering onDestroy()"); //$NON-NLS-1$
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(this.mReceiver);
        this.unregisterReceiver(this.connectionReceiver);

        if (Debugging.D) {
            Log.d(TAG, "Leaving onDestroy()"); //$NON-NLS-1$
        }

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

        if (Debugging.D) {
            Log.d(TAG, "Entering onConfigurationChanged(newConfig=" + newConfig //$NON-NLS-1$
                    + ")"); //$NON-NLS-1$
        }

        // Do nothing

        if (Debugging.D) {
            Log.d(TAG, "Leaving onConfigurationChanged()"); //$NON-NLS-1$
        }
    }

    /* **********Inner classes********** */
    /**
     * The on-click listener for all devices in the paired and new devices list.
     * Starts the connection process.
     */
    private OnItemClickListener     mDeviceClickListener          = new OnItemClickListener() {
                                                                      @Override
                                                                      public void onItemClick(
                                                                              AdapterView<?> av,
                                                                              View v,
                                                                              int arg2,
                                                                              long arg3) {
                                                                          // Cancel
                                                                          // discovery
                                                                          // because
                                                                          // it's
                                                                          // costly
                                                                          // and
                                                                          // we're
                                                                          // about
                                                                          // to
                                                                          // connect
                                                                          BluetoothActivity.this.mBtAdapter
                                                                                  .cancelDiscovery();

                                                                          // Get
                                                                          // the
                                                                          // device
                                                                          // MAC
                                                                          // address,
                                                                          // which
                                                                          // is
                                                                          // the
                                                                          // last
                                                                          // 17
                                                                          // chars
                                                                          // in
                                                                          // the
                                                                          // View
                                                                          String info = ((TextView) v)
                                                                                  .getText()
                                                                                  .toString();

                                                                          if (!info
                                                                                  .equals(getResources()
                                                                                          .getText(
                                                                                                  R.string.none_found)
                                                                                          .toString())) {

                                                                              String fullAddress = info;

                                                                              String address = info
                                                                                      .substring(info
                                                                                              .length() - 17);

                                                                              // Create
                                                                              // the
                                                                              // broadcast
                                                                              // Intent
                                                                              // and
                                                                              // include
                                                                              // the
                                                                              // MAC
                                                                              // address
                                                                              Intent intent = new Intent();

                                                                              intent.putExtra(
                                                                                      DEVICE_ADDRESS_FULL,
                                                                                      fullAddress);
                                                                              intent.putExtra(
                                                                                      EXTRA_DEVICE_ADDRESS,
                                                                                      address);

                                                                              intent.setAction(BluetoothActivity.DEVICE_LIST_SELECTED);

                                                                              sendBroadcast(intent);

                                                                          }
                                                                      }
                                                                  };

    /**
     * The on-click listener for the connected device "list". Disconnects the
     * device.
     */
    private OnItemClickListener     mConnectedDeviceClickListener = new OnItemClickListener() {
                                                                      @Override
                                                                      public void onItemClick(
                                                                              AdapterView<?> av,
                                                                              View v,
                                                                              int arg2,
                                                                              long arg3) {

                                                                          // Cancel
                                                                          // connection
                                                                          cancelConnection();

                                                                      }

                                                                  };

    /**
     * The BroadcastReceiver that listens for discovered devices and changes the
     * title when discovery is finished
     */
    private final BroadcastReceiver mReceiver                     = new BroadcastReceiver() {
                                                                      @Override
                                                                      public void onReceive(
                                                                              Context context,
                                                                              Intent intent) {
                                                                          String action = intent
                                                                                  .getAction();

                                                                          // When
                                                                          // discovery
                                                                          // finds
                                                                          // a
                                                                          // device
                                                                          if (BluetoothDevice.ACTION_FOUND
                                                                                  .equals(action)) {
                                                                              // Get
                                                                              // the
                                                                              // BluetoothDevice
                                                                              // object
                                                                              // from
                                                                              // the
                                                                              // Intent
                                                                              BluetoothDevice device = intent
                                                                                      .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                                                                              // If
                                                                              // it's
                                                                              // already
                                                                              // paired,
                                                                              // skip
                                                                              // it,
                                                                              // because
                                                                              // it's
                                                                              // been
                                                                              // listed
                                                                              // already
                                                                              if (device
                                                                                      .getBondState() != BluetoothDevice.BOND_BONDED) {

                                                                                  BluetoothActivity.this.mNewDevicesArrayAdapter
                                                                                          .add(device
                                                                                                  .getName()
                                                                                                  + "\n" //$NON-NLS-1$
                                                                                                  + device.getAddress());

                                                                              }
                                                                              // When
                                                                              // discovery
                                                                              // is
                                                                              // finished,
                                                                              // change
                                                                              // the
                                                                              // Activity
                                                                              // title
                                                                          } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                                                                                  .equals(action)) {

                                                                              BluetoothActivity.this.scanButton
                                                                                      .setEnabled(true);

                                                                              BluetoothActivity.this.scanProgress
                                                                                      .setVisibility(View.GONE);

                                                                              // BluetoothActivity.this.mTitle
                                                                              // .setText(R.string.none_connected);
                                                                              if (BluetoothActivity.this.mNewDevicesArrayAdapter
                                                                                      .getCount() == 0) {
                                                                                  String noDevices = getResources()
                                                                                          .getText(
                                                                                                  R.string.none_found)
                                                                                          .toString();
                                                                                  BluetoothActivity.this.mNewDevicesArrayAdapter
                                                                                          .add(noDevices);
                                                                              }

                                                                          }
                                                                      }
                                                                  };
    /**
     * The BroadcastReceiver that listens for a change in connection status and
     * changes view visibilties and sets the info view text
     */
    private final BroadcastReceiver connectionReceiver            = new BroadcastReceiver() {

                                                                      @Override
                                                                      public void onReceive(
                                                                              Context context,
                                                                              Intent intent) {
                                                                          String action = intent
                                                                                  .getAction();

                                                                          if (TabBarActivity.BT_CONNECTING
                                                                                  .equals(action)) {

                                                                              if (Debugging.D) {
                                                                                  Log.d(TAG,
                                                                                          "Connecting!"); //$NON-NLS-1$
                                                                              }

                                                                              if (BluetoothActivity.this.themeColors == null
                                                                                      || BluetoothActivity.this.listTextSize == 0.0f) {
                                                                                  getListTextViewValues();

                                                                                  setConnectionInfoViewTextSize();

                                                                              }

                                                                              // Set
                                                                              // device
                                                                              // name
                                                                              // empty
                                                                              BluetoothActivity.this.connectedDevice = ""; //$NON-NLS-1$

                                                                              // Disable
                                                                              // the
                                                                              // list
                                                                              // view
                                                                              BluetoothActivity.this
                                                                                      .setListsEnabled(false);

                                                                              // Set
                                                                              // cancel
                                                                              // button
                                                                              // visible
                                                                              BluetoothActivity.this
                                                                                      .setCancelButtonVisible(View.VISIBLE);

                                                                              // Set
                                                                              // cancel
                                                                              // button
                                                                              // enabled
                                                                              BluetoothActivity.this.connectionCancelButton
                                                                                      .setEnabled(true);

                                                                              // Set
                                                                              // the
                                                                              // info
                                                                              // view
                                                                              // text
                                                                              setConnectionInfoViewText(getResources()
                                                                                      .getString(
                                                                                              R.string.info_bt_connecting));

                                                                              // Set
                                                                              // the
                                                                              // connection
                                                                              // info
                                                                              // field
                                                                              // and
                                                                              // progress
                                                                              // bar
                                                                              // visible
                                                                              BluetoothActivity.this
                                                                                      .setConnectionInfoVisible(View.VISIBLE);
                                                                          } else if (TabBarActivity.BT_CONNECTED
                                                                                  .equals(action)) {

                                                                              // Get
                                                                              // the
                                                                              // full
                                                                              // device
                                                                              // info
                                                                              // from
                                                                              // intent
                                                                              BluetoothActivity.this.connectedDevice = intent
                                                                                      .getExtras()
                                                                                      .getString(
                                                                                              TabBarActivity.DEVICE_INFO_FULL);

                                                                              if (Debugging.D) {
                                                                                  Log.d(TAG,
                                                                                          "Connected to " + BluetoothActivity.this.connectedDevice); //$NON-NLS-1$

                                                                              }

                                                                              // Set
                                                                              // connected
                                                                              // list
                                                                              // text
                                                                              setConnectedListText(BluetoothActivity.this.connectedDevice);

                                                                              // Enable
                                                                              // list
                                                                              // view
                                                                              BluetoothActivity.this
                                                                                      .setListsEnabled(true);

                                                                              // Set
                                                                              // cancel
                                                                              // button
                                                                              // invisible
                                                                              BluetoothActivity.this
                                                                                      .setCancelButtonVisible(View.GONE);

                                                                              // Set
                                                                              // the
                                                                              // connection
                                                                              // info
                                                                              // field
                                                                              // and
                                                                              // progress
                                                                              // bar
                                                                              // invisible
                                                                              BluetoothActivity.this
                                                                                      .setConnectionInfoVisible(View.GONE);

                                                                          } else if (TabBarActivity.BT_NONE_OR_LISTENING
                                                                                  .equals(action)) {

                                                                              if (Debugging.D) {
                                                                                  Log.d(TAG,
                                                                                          "State none or listening!"); //$NON-NLS-1$
                                                                              }

                                                                              // Set
                                                                              // device
                                                                              // name
                                                                              // empty
                                                                              BluetoothActivity.this.connectedDevice = ""; //$NON-NLS-1$

                                                                              // Set
                                                                              // connecting
                                                                              // info
                                                                              // text
                                                                              BluetoothActivity.this
                                                                                      .setConnectedListText(BluetoothActivity.this
                                                                                              .getResources()
                                                                                              .getString(
                                                                                                      R.string.none_connected));

                                                                              // Enable
                                                                              // list
                                                                              // view
                                                                              BluetoothActivity.this
                                                                                      .setListsEnabled(true);

                                                                              // Set
                                                                              // cancel
                                                                              // button
                                                                              // invisible
                                                                              BluetoothActivity.this
                                                                                      .setCancelButtonVisible(View.GONE);

                                                                              // Set
                                                                              // the
                                                                              // connection
                                                                              // info
                                                                              // field
                                                                              // and
                                                                              // progress
                                                                              // bar
                                                                              // invisible
                                                                              BluetoothActivity.this
                                                                                      .setConnectionInfoVisible(View.GONE);

                                                                          }

                                                                      }
                                                                  };

}
