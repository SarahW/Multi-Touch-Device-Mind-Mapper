/*
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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * <p>
 * The DBAdapter. Handles all database queries / requests via a DBOpenHelper.
 * </p>
 * 
 * @author Sarah Will
 * @version 1.0
 * 
 */
public class DBAdapter {

    // Debugging
    private static final String TAG              = "DBAdapter"; //$NON-NLS-1$

    // Database constants
    /** The name of the database */
    private static final String DATABASE_NAME    = "idealistdb"; //$NON-NLS-1$
    /** The table for the ideas */
    private static final String DATABASE_TABLE   = "idealist";  //$NON-NLS-1$
    /** The database version number */
    private static final int    DATABASE_VERSION = 1;
    /** The database key for the idea id */
    public static final String  KEY_ID           = "_id";       //$NON-NLS-1$
    /** The database key for the idea text */
    public static final String  KEY_IDEA         = "idea";      //$NON-NLS-1$

    // Members
    /** The sqlite database instance */
    private SQLiteDatabase      database;
    /** The context the dbAdapter is created from */
    private Context             context;
    /** The databaseOpenHelper instance */
    private DBOpenHelper        dbHelper;

    /**
     * Instantiates a new DBAdapter.
     * 
     * @param context
     *            the context
     */
    public DBAdapter(Context context) {
        this.context = context;
        this.dbHelper = new DBOpenHelper(this.context, DATABASE_NAME, null,
                DATABASE_VERSION);
    }

    /**
     * The DBOpenHelper class. Provides methods for creating and upgrading a
     * database.
     * 
     */
    class DBOpenHelper extends SQLiteOpenHelper {

        /** The database create statement string */
        private static final String DATABASE_CREATE = "CREATE TABLE " //$NON-NLS-1$
                                                            + DATABASE_TABLE
                                                            + " (" //$NON-NLS-1$
                                                            + KEY_ID
                                                            + " INTEGER PRIMARY KEY AUTOINCREMENT, " //$NON-NLS-1$
                                                            + KEY_IDEA
                                                            + " text not null)"; //$NON-NLS-1$

        /**
         * Instantiates a new DBOpenHelper.
         * 
         * @param context
         *            the context
         * @param name
         *            the database name
         * @param factory
         *            the factory
         * @param version
         *            the database version
         */
        public DBOpenHelper(Context context, String name,
                CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        /**
         * Called when the database is created for the first time.
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
            Log.i(TAG, "Created a database"); //$NON-NLS-1$

        }

        /**
         * Called when the database needs to be upgraded.
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (db != null) {
                db.execSQL("drop DATABASE_NAME"); //$NON-NLS-1$
                onCreate(db);
            }
        }

    }

    /**
     * Closes the database.
     */
    public void close() {
        this.database.close();
        if (Debugging.D) {
            Log.d(TAG, "Closed database"); //$NON-NLS-1$
        }

    }

    /**
     * Tries to open the database as writable, alternatively as readable only.
     * 
     * @throws SQLException
     */
    public void open() throws SQLException {
        try {
            this.database = this.dbHelper.getWritableDatabase();
            Log.i(TAG, "Opened database as writable"); //$NON-NLS-1$
        } catch (SQLException s) {
            this.database = this.dbHelper.getReadableDatabase();
            Log.w(TAG, "Opened database as readable"); //$NON-NLS-1$
        }
    }

    /**
     * Method to insert an idea item into the database.
     * 
     * @param item
     *            the idea string
     * @return the database row or -1 on error
     */
    public long insertIdeaItem(String item) {
        long row;
        ContentValues stringItem = new ContentValues();

        stringItem.put(KEY_IDEA, item);
        try {
            row = this.database.insert(DATABASE_TABLE, KEY_IDEA, stringItem);
            if (Debugging.D) {
                Log.d(TAG, "Inserted idea with text" + stringItem); //$NON-NLS-1$
            }
            return row;
        } catch (Exception e) {
            Log.e(TAG,
                    "Error in inserting idea" + stringItem + ";" + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
            return -1;
        }

    }

    /**
     * Removes an idea item from the database.
     * 
     * @param ideaText
     *            the String of the idea to be removed
     */
    public void removeIdeaItem(String ideaText) {
        if (Debugging.D) {
            Log.d(TAG, "Removed idea" + ideaText); //$NON-NLS-1$
        }
        this.database.execSQL("DELETE FROM " + DATABASE_TABLE + " WHERE " //$NON-NLS-1$//$NON-NLS-2$
                + KEY_IDEA + "='" + ideaText + "'"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Returns a cursor for the idea list database
     * 
     * @return the database cursor
     */
    public Cursor getAllIdeaItemsCursor() {
        return this.database.query(DATABASE_TABLE, new String[] { KEY_ID,
                KEY_IDEA }, null, null, null, null, KEY_ID);
    }

    /**
     * Returns a cursor for the idealist database in reversed order.
     * 
     * @return the database cursor
     */
    public Cursor getAllIdeaItemsReversedCursor() {
        return this.database.query(DATABASE_TABLE, new String[] { KEY_ID,
                KEY_IDEA }, null, null, null, null, KEY_ID + " DESC"); //$NON-NLS-1$
    }

    /**
     * Checks whether an idea string is already part of the database.
     * 
     * @param ideaItem
     *            the idea item String
     * @return true, if the specified item is in the database
     */
    public boolean checkIdeaItem(String ideaItem) {

        String where = KEY_IDEA + "=" + "'" + ideaItem + "'"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        Cursor mCursor = this.database.query(DATABASE_TABLE, new String[] {
                KEY_ID, KEY_IDEA }, where, null, null, null, null, null);
        if (mCursor != null) {

            // Get the number of result rows
            int rows = mCursor.getCount();

            if (rows > 0) {
                return true;
            }
            return false;

        }
        return false;
    }

}
