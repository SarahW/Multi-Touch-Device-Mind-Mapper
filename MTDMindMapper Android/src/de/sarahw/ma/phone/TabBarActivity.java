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
import android.app.TabActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * <p>
 * The TabBarActivity class holding the application activities. Initializes the
 * necessary members for a bluetooth connection.
 * 
 * </p>
 * <p>
 * Bluetooth code adapted from
 * https://github.com/luugiathuy/Remote-Bluetooth-Android (RemoteBluetooth.java)
 * </p>
 * 
 * <p>
 * Modified 2011-09-18
 * </p>
 * 
 * @author Luu Gia Thuy (RemoteBluetooth.java)
 * @author Sarah Will (RemoteBluetooth.java modifications, additional code)
 * 
 */
@SuppressWarnings("synthetic-access")
public class TabBarActivity extends TabActivity {

    // Debugging
    private static final String     TAG                      = "TabBarActivity";  //$NON-NLS-1$
    private static final boolean    D                        = true;

    // Intent request code
    /** The intent request code used for querying the bluetooth adapter */
    private static final int        REQUEST_ENABLE_BT        = 1;

    // Message types sent from the BluetoothCommandService Handler
    /**
     * The message type indicating a bluetooth connection state change has
     * occurred
     */
    public static final int         MESSAGE_STATE_CHANGE     = 1;
    /**
     * The message type indicating that bytes have been read from the
     * inputStream (not used)
     */
    public static final int         MESSAGE_READ             = 2;
    /**
     * The message type indicating that bytes have been written to the
     * outputStream (not used)
     */
    public static final int         MESSAGE_WRITE            = 3;
    /** The message type indicating that the device name has been acquired */
    public static final int         MESSAGE_DEVICE_NAME      = 4;
    /** The message type indicating a (short) toast is to be shown */
    public static final int         MESSAGE_TOAST_SHORT      = 5;
    /** The message type indicating a (long) toast is to be shown */
    public static final int         MESSAGE_TOAST_LONG       = 6;

    // Key names received from the BluetoothCommandService Handler
    /**
     * The key name for the device name sent via the BluetoothCommandService
     * handler
     */
    public static final String      DEVICE_NAME              = "device_name";     //$NON-NLS-1$

    /** The key name for a toast sent via the BluetoothCommandService handler */
    public static final String      TOAST                    = "toast";           //$NON-NLS-1$

    // Views
    /** The index of the default tab on creation of the activity */
    private static final int        DEFAULT_TAB              = 1;

    // Broadcast Intent name
    /** The intent name sent when bt status is connected */
    public static String            BT_CONNECTED             = "bt connected";    //$NON-NLS-1$
    /** The intent name sent when bt status is connecting */
    public static String            BT_CONNECTING            = "bt connecting";   //$NON-NLS-1$
    /** The intent name sent when bt is listening */
    public static String            BT_NONE_OR_LISTENING     = "bt listening";    //$NON-NLS-1$
    /**
     * The intent full device name plus mac address of the connected device sent
     * on connection
     */
    protected static final String   DEVICE_INFO_FULL         = "full_device_info"; //$NON-NLS-1$

    // Static field for TabBarActivity reference used by other activities
    /** The reference ot the TabBarActivity to be used by other activities */
    public static TabBarActivity    myTabLayout;

    // Layout views
    /** The title text view */
    private TextView                mTitle;
    /** The progress bar indicating that a bluetooth connection is built up */
    private ProgressBar             titleProgressBar;

    // Bluetooth
    /** The name of the connected remote device */
    private String                  mConnectedDeviceName     = null;
    /** The full info for the connected remote device */
    private String                  mConnectedDeviceNameFull = null;
    /** The local bluetooth adapter */
    private BluetoothAdapter        mBluetoothAdapter        = null;
    /** The bluetoothCommandService instance */
    private BluetoothCommandService mCommandService          = null;

    // Database
    /** The database adapter */
    private DBAdapter               dBAdapter;
    /** The cursor holding database results */
    private Cursor                  dbCursor;
    /** The adapter for the database cursor results */
    private SimpleCursorAdapter     simpleCursorAdapter;

    /* ********Getters & Setters******** */
    /**
     * Returns the database adapter.
     * 
     * @return the dBAdapter
     */
    public DBAdapter getdBAdapter() {
        return this.dBAdapter;
    }

    /**
     * Returns the database cursor.
     * 
     * @return the dbCursor
     */
    public Cursor getDbCursor() {
        return this.dbCursor;
    }

    /**
     * Sets the database cursor.
     * 
     * @param dbCursor
     *            the dbCursor to set
     */
    public void setDbCursor(Cursor dbCursor) {
        if (dbCursor != null) {
            this.dbCursor = dbCursor;
        }
    }

    /**
     * Returns the simpleCursorAdapter
     * 
     * @return the simpleCursorAdapter
     */
    public SimpleCursorAdapter getSimpleCursorAdapter() {
        return this.simpleCursorAdapter;
    }

    /**
     * Sets the simpleCursorAdapter
     * 
     * @param simpleCursorAdapter
     *            the simpleCursorAdapter to set
     */
    public void setSimpleCursorAdapter(SimpleCursorAdapter simpleCursorAdapter) {
        if (simpleCursorAdapter != null) {
            this.simpleCursorAdapter = simpleCursorAdapter;
        }
    }

    /**
     * Returns the BluetoothCommandService started with this TabBarActivity.
     * 
     * @return the mCommandService
     */
    public BluetoothCommandService getmCommandService() {
        return this.mCommandService;
    }

    /**
     * Returns the title of the TabBarActivity
     * 
     * @return the mTitle
     */
    public TextView getmTitle() {
        return this.mTitle;
    }

    /* **********Object methods********** */
    /**
     * Initializes the database.
     * 
     */
    private void initializeDB() {

        if (Debugging.D) {
            Log.d(TAG, "Entering initializeDB()"); //$NON-NLS-1$
        }

        // New DB Adapter
        this.dBAdapter = new DBAdapter(this);
        this.dBAdapter.open();

        if (Debugging.D) {
            Log.d(TAG, "Leaving initializeDB()"); //$NON-NLS-1$
        }

    }

    /**
     * Initializes the GUI.
     * 
     * @param tabIndexAfterInit
     *            the tab index that shall be set after initialization
     */
    private void initializeGUI(int tabIndexAfterInit) {

        if (Debugging.D) {
            Log.d(TAG, "Entering initializeGUI()"); //$NON-NLS-1$
        }

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.tab_widget);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.custom_title);

        // Set up the custom title
        this.mTitle = (TextView) findViewById(R.id.title_left_text);
        this.mTitle.setText(R.string.app_name);
        this.mTitle = (TextView) findViewById(R.id.title_right_text);

        // Get progress bar reference
        this.titleProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        TabBarActivity.myTabLayout = this;// STORING

        // Broadcast receiver
        // Register for broadcasts when a device is selected for connection
        IntentFilter filter = new IntentFilter(
                BluetoothActivity.DEVICE_LIST_SELECTED);
        this.registerReceiver(this.bluetoothListBReceiver, filter);

        // Register for broadcasts when connecting was canceled by the user
        filter = new IntentFilter(BluetoothActivity.CONNECTING_CANCELED);
        this.registerReceiver(this.bluetoothListBReceiver, filter);

        // Tab Menu
        Resources res = getResources(); // Resource object to get Drawables
        final TabHost tabHost = getTabHost(); // The activity TabHost
        TabHost.TabSpec spec; // Resusable TabSpec for each tab
        Intent intent; // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, TransferActivity.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost
                .newTabSpec("transfer") //$NON-NLS-1$
                .setIndicator(
                        getResources().getText(R.string.tab_title_transfer)
                                .toString(),
                        res.getDrawable(R.drawable.ic_tab_transfer))
                .setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        intent = new Intent().setClass(this, IdeaCreationActivity.class);
        spec = tabHost
                .newTabSpec("creation") //$NON-NLS-1$
                .setIndicator(
                        getResources().getText(R.string.tab_title_creation)
                                .toString(),
                        res.getDrawable(R.drawable.ic_tab_creation))
                .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, BluetoothActivity.class);
        spec = tabHost
                .newTabSpec("bluetooth") //$NON-NLS-1$
                .setIndicator(
                        getResources().getText(R.string.tab_title_bt)
                                .toString(),
                        res.getDrawable(R.drawable.ic_tab_bt))
                .setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(tabIndexAfterInit);

        tabHost.setOnTabChangedListener(new OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(
                        tabHost.getApplicationWindowToken(), 0);
            }
        });

        if (Debugging.D) {
            Log.d(TAG, "Leaving initializeGUI()"); //$NON-NLS-1$
        }

    }

    /**
     * Initializes the BT Adapter member.
     * 
     */
    private void initializeBT() {

        if (Debugging.D) {
            Log.d(TAG, "Entering initializeBT()"); //$NON-NLS-1$
        }
        // Get local Bluetooth adapter
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (this.mBluetoothAdapter == null) {
            Toast.makeText(
                    this,
                    getResources().getText(R.string.toast_bt_not_supported)
                            .toString(), Toast.LENGTH_LONG).show();
            // OLD: close application
            // finish();
            return;
        }

        if (Debugging.D) {
            Log.d(TAG, "Leaving initializeBT()"); //$NON-NLS-1$
        }

    }

    /**
     * Initializes the BluetoothCommandService.
     * 
     */
    private void setupCommand() {

        if (Debugging.D) {
            Log.d(TAG, "Entering setupCommand()"); //$NON-NLS-1$
        }
        // Initialize the BluetoothCommandService to perform bluetooth
        // connections
        this.mCommandService = new BluetoothCommandService(this, this.mHandler);

        if (Debugging.D) {
            Log.d(TAG, "Leaving setupCommand()"); //$NON-NLS-1$
        }
    }

    /**
     * Get the current selected activity / tab content
     * 
     * @return the current activity
     */
    public Activity getCurrentSelectedActivity() {

        if (Debugging.D) {
            Log.d(TAG, "Entering getCurrentSelectedActivity()"); //$NON-NLS-1$
        }

        String tabTag = getTabHost().getCurrentTabTag();

        if (Debugging.D) {
            Log.d(TAG, "Leaving getCurrentSelectedActivity()"); //$NON-NLS-1$
        }
        return getLocalActivityManager().getActivity(tabTag);

    }

    /* ********Overridden methods******** */
    /**
     * Called when the activity is first created. Initializes the database, the
     * GUI and gets the local bluetooth adapter.
     * 
     * @param savedInstanceState
     *            the saved instance state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (Debugging.D) {
            Log.d(TAG, "Leaving onCreate(savedInstanceState=" + //$NON-NLS-1$
                    savedInstanceState + ")"); //$NON-NLS-1$
        }

        super.onCreate(savedInstanceState);

        initializeDB();

        initializeGUI(DEFAULT_TAB);

        initializeBT();

        if (Debugging.D) {
            Log.d(TAG, "Leaving onCreate()"); //$NON-NLS-1$
        }

    }

    /**
     * Called after onCreate() or onResume() when the activity had been stopped
     * but is now again being displayed to the user. Requests enabling the local
     * bluetooth adapter if necessary.
     * 
     */
    @Override
    protected void onStart() {
        super.onStart();

        if (Debugging.D) {
            Log.d(TAG, "Entering onStart()"); //$NON-NLS-1$
        }

        // If BT is not on, request that it be enabled.
        // setupCommand() will then be called during onActivityResult
        if (!this.mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        // otherwise set up the command service
        else {
            if (this.mCommandService == null)
                setupCommand();
        }

        if (Debugging.D) {
            Log.d(TAG, "Leaving onStart()"); //$NON-NLS-1$
        }
    }

    /**
     * Called after onRestoreInstanceState(Bundle), onRestart(), or onPause().
     * Starts the bluetooth command service if it has not been started yet.
     * 
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (Debugging.D) {
            Log.d(TAG, "Entering onResume()"); //$NON-NLS-1$
        }

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity
        // returns.
        if (this.mCommandService != null) {
            if (this.mCommandService.getState() == BluetoothCommandService.STATE_NONE) {
                this.mCommandService.start();
            }
        }

        if (Debugging.D) {
            Log.d(TAG, "Leaving onResume()"); //$NON-NLS-1$
        }

    }

    /**
     * Called when activity is destroyed via finish() or via system memory clean
     * up. Stops the bluetooth command service, unregisters the bluetooth list
     * receiver and closes the database connection.
     * 
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (Debugging.D) {
            Log.d(TAG, "Entering onDestroy()"); //$NON-NLS-1$
        }

        if (this.mCommandService != null)
            this.mCommandService.stop();

        // Unregister receivers
        this.unregisterReceiver(this.bluetoothListBReceiver);

        // Close cursor
        this.dbCursor.close();

        // Close DB connection
        this.dBAdapter.close();

        if (Debugging.D) {
            Log.d(TAG, "Leaving onDestroy()"); //$NON-NLS-1$
        }
    }

    /**
     * Called when an activity this activity launched exits, including the
     * requestCode assigned at the launch of the activity, the resultCode it
     * returned, and any additional data from it.
     * 
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (Debugging.D) {
            Log.d(TAG, "Entering onActivityResult(requestCode=" + requestCode //$NON-NLS-1$
                    + ", resultCode=" + resultCode + ", data=" + data + ")"); //$NON-NLS-1$  //$NON-NLS-2$  //$NON-NLS-3$
        }
        switch (requestCode) {

            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupCommand();
                } else {
                    // User did not enable Bluetooth or an error occured
                    Toast.makeText(this, R.string.toast_bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();

                    finish();
                }
        }

        if (Debugging.D) {
            Log.d(TAG, "Leaving onActivityResult()"); //$NON-NLS-1$  
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
            Log.d(TAG, "Entering onConfigurationChanged(newConfig=" + //$NON-NLS-1$  
                    newConfig + ")"); //$NON-NLS-1$  
        }
        // For now do nothing

        if (Debugging.D) {
            Log.d(TAG, "Leaving onConfigurationChanged()"); //$NON-NLS-1$  
        }
    }

    /* **********Inner classes********** */
    /**
     * The Handler that gets information back from the BluetoothCommandService.
     * Used to set the window title and progress bar depending on the connection
     * status as well as showing toasts.
     * 
     */
    private final Handler           mHandler               = new Handler() {
                                                               @Override
                                                               public void handleMessage(
                                                                       Message msg) {
                                                                   switch (msg.what) {
                                                                       case MESSAGE_STATE_CHANGE:
                                                                           switch (msg.arg1) {
                                                                               case BluetoothCommandService.STATE_CONNECTED:

                                                                                   // Set
                                                                                   // newly
                                                                                   // connected
                                                                                   // device
                                                                                   // in
                                                                                   // title
                                                                                   TabBarActivity.this.mTitle
                                                                                           .setText(R.string.title_connected_to);
                                                                                   TabBarActivity.this.mTitle
                                                                                           .append(TabBarActivity.this.mConnectedDeviceName);

                                                                                   TabBarActivity.this.titleProgressBar
                                                                                           .setVisibility(View.GONE);

                                                                                   // Send
                                                                                   // broadcast
                                                                                   // with
                                                                                   // full
                                                                                   // device
                                                                                   // name
                                                                                   Intent intentConnected = new Intent();

                                                                                   intentConnected
                                                                                           .putExtra(
                                                                                                   DEVICE_INFO_FULL,
                                                                                                   TabBarActivity.this.mConnectedDeviceNameFull);

                                                                                   intentConnected
                                                                                           .setAction(TabBarActivity.BT_CONNECTED);

                                                                                   TabBarActivity.this
                                                                                           .sendBroadcast(intentConnected);

                                                                                   break;
                                                                               case BluetoothCommandService.STATE_CONNECTING:

                                                                                   // Set
                                                                                   // "connecting"
                                                                                   // status
                                                                                   // in
                                                                                   // title
                                                                                   TabBarActivity.this.mTitle
                                                                                           .setText(R.string.title_connecting);

                                                                                   TabBarActivity.this.titleProgressBar
                                                                                           .setVisibility(View.VISIBLE);

                                                                                   // Send
                                                                                   // connecting
                                                                                   // broadcast
                                                                                   Intent intentConnecting = new Intent();

                                                                                   intentConnecting
                                                                                           .setAction(TabBarActivity.BT_CONNECTING);

                                                                                   TabBarActivity.this
                                                                                           .sendBroadcast(intentConnecting);
                                                                                   break;
                                                                               case BluetoothCommandService.STATE_LISTEN:
                                                                               case BluetoothCommandService.STATE_NONE:

                                                                                   // Set
                                                                                   // "not connected"
                                                                                   // in
                                                                                   // title
                                                                                   TabBarActivity.this.mTitle
                                                                                           .setText(R.string.title_not_connected);

                                                                                   TabBarActivity.this.titleProgressBar
                                                                                           .setVisibility(View.GONE);

                                                                                   // Send
                                                                                   // broadcast
                                                                                   Intent intentListeningOrNone = new Intent();

                                                                                   intentListeningOrNone
                                                                                           .setAction(TabBarActivity.BT_NONE_OR_LISTENING);

                                                                                   TabBarActivity.this
                                                                                           .sendBroadcast(intentListeningOrNone);

                                                                                   break;
                                                                           }
                                                                           break;
                                                                       case MESSAGE_DEVICE_NAME:
                                                                           // save
                                                                           // the
                                                                           // connected
                                                                           // device's
                                                                           // name
                                                                           TabBarActivity.this.mConnectedDeviceName = msg
                                                                                   .getData()
                                                                                   .getString(
                                                                                           DEVICE_NAME);
                                                                           Toast.makeText(
                                                                                   getApplicationContext(),
                                                                                   getResources()
                                                                                           .getText(
                                                                                                   R.string.toast_connected_to)
                                                                                           .toString()
                                                                                           + TabBarActivity.this.mConnectedDeviceName,
                                                                                   Toast.LENGTH_SHORT)
                                                                                   .show();
                                                                           break;
                                                                       case MESSAGE_TOAST_SHORT:
                                                                           Toast.makeText(
                                                                                   getApplicationContext(),
                                                                                   msg.getData()
                                                                                           .getString(
                                                                                                   TOAST),
                                                                                   Toast.LENGTH_SHORT)
                                                                                   .show();
                                                                           break;

                                                                       case MESSAGE_TOAST_LONG:
                                                                           Toast.makeText(
                                                                                   getApplicationContext(),
                                                                                   msg.getData()
                                                                                           .getString(
                                                                                                   TOAST),
                                                                                   Toast.LENGTH_LONG)
                                                                                   .show();
                                                                           break;
                                                                   }
                                                               }
                                                           };

    /**
     * The BroadcastReceiver that listens for connection actions. Used to
     * connect to a selecte remote device.
     * 
     */
    private final BroadcastReceiver bluetoothListBReceiver = new BroadcastReceiver() {

                                                               @Override
                                                               public void onReceive(
                                                                       Context context,
                                                                       Intent intent) {
                                                                   String action = intent
                                                                           .getAction();

                                                                   // When
                                                                   // discovery
                                                                   // finds a
                                                                   // device
                                                                   if (BluetoothActivity.DEVICE_LIST_SELECTED
                                                                           .equals(action)) {

                                                                       // Get
                                                                       // the
                                                                       // device
                                                                       // MAC
                                                                       // address
                                                                       String address = intent
                                                                               .getExtras()
                                                                               .getString(
                                                                                       BluetoothActivity.EXTRA_DEVICE_ADDRESS);

                                                                       // Get
                                                                       // the
                                                                       // full
                                                                       // info
                                                                       String fullInfo = intent
                                                                               .getExtras()
                                                                               .getString(
                                                                                       BluetoothActivity.DEVICE_ADDRESS_FULL);

                                                                       TabBarActivity.this.mConnectedDeviceNameFull = fullInfo;

                                                                       // Get
                                                                       // the
                                                                       // BLuetoothDevice
                                                                       // object
                                                                       BluetoothDevice device = TabBarActivity.this.mBluetoothAdapter
                                                                               .getRemoteDevice(address);
                                                                       // Attempt
                                                                       // to
                                                                       // connect
                                                                       // to the
                                                                       // device
                                                                       TabBarActivity.this.mCommandService
                                                                               .connect(device);

                                                                   } else if (BluetoothActivity.CONNECTING_CANCELED
                                                                           .equals(action)) {

                                                                       // Stop
                                                                       // the
                                                                       // connection
                                                                       // process
                                                                       if (TabBarActivity.this.mCommandService != null) {
                                                                           TabBarActivity.this.mCommandService
                                                                                   .stop();
                                                                       }

                                                                   }
                                                               }

                                                           };

}
