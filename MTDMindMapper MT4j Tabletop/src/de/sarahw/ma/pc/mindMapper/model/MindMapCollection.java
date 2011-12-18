/***********************************************************************
 * Copyright (c) 2011 Sarah Will
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 ***********************************************************************/

package de.sarahw.ma.pc.mindMapper.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.apache.log4j.Logger;

import de.sarahw.ma.pc.mindMapper.ObserverNotificationObject;

/**
 * <p>
 * Represents a collection of all individual MindMap files in the application.
 * </p>
 * 
 * <p>
 * Implements the Singleton-Pattern.
 * </p>
 * 
 * <p>
 * Contains methods for loading a MindMap from disc via object Deserialization.
 * </p>
 * 
 * @author Sarah Will
 * 
 * @version 1.0
 * 
 */

public class MindMapCollection extends Observable {

    private static Logger            log       = Logger.getLogger(MindMapCollection.class);

    /** The file path for the save files */
    public static final String       FILE_PATH = "." + File.separator + "save";            //$NON-NLS-1$ //$NON-NLS-2$

    /** The mindMapCollection instance */
    private static MindMapCollection instance;

    /** The currently loaded mindMap instance */
    private MindMap                  loadedMindMap;

    /** The list of mindMap files in the mindMapCollection */
    private List<File>               mindMapCollectionFiles;

    /** The list of mindMap names in the mindMapCollection */
    private List<String>             mindMapFileNames;                                     // files

    /* ***********Constructors*********** */
    /**
     * Default private constructor. Instantiates a new MindMapCollection.
     * 
     */
    private MindMapCollection() {
        super();
        log.debug("Executing MindMapCollection()"); //$NON-NLS-1$     

        // Initialize arrays and get saved files from disc
        updateMindMapFilesList();

        // Initialize empty mindMap
        setLoadedMindMap(new MindMap());

    }

    /* ********Getters & Setters******** */
    /**
     * Returns an instance of MindMapCollection. >> Singleton-Pattern
     * 
     * @return instance of MindMapCollection
     */
    public synchronized static MindMapCollection getInstance() {
        if (instance == null) {
            instance = new MindMapCollection();
        }
        return instance;
    }

    /**
     * Returns the loadedMindMap MindMap in the MindMapCollection.
     * 
     * @return the loadedMindMap
     */
    protected MindMap getLoadedMindMap() {
        log.trace("Entering getLoadedMindMap()"); //$NON-NLS-1$
        log.trace("Leaving getLoadedMindMap(): " + this.loadedMindMap); //$NON-NLS-1$   
        return this.loadedMindMap;
    }

    /**
     * Sets the loaded MindMap in the MindMapCollection.
     * 
     * @param loadedMindMap
     *            the loadedMindMap to set
     */
    protected void setLoadedMindMap(MindMap loadedMindMap) {
        log.trace("Entering setLoadedMindMap(loadedMindMap=" + loadedMindMap + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        if (loadedMindMap != null) {
            this.loadedMindMap = loadedMindMap;

            log.debug("Communicate changes to observer for setLoadedMindMap()"); //$NON-NLS-1$
            communicateChangesToObserver(new ObserverNotificationObject(
                    EMindMapCollectionChangeStatus.NEW_MIND_MAP_LOADED,
                    loadedMindMap));

            log.trace("Leaving setLoadedMindMap()"); //$NON-NLS-1$
        }
        log.trace("Leaving setLoadedMindMap(), invalid null input"); //$NON-NLS-1$
    }

    /**
     * Returns the list of individual MindMaps in the MindMapCollection.
     * 
     * @return the mindMapCollectionFiles
     */
    public ArrayList<File> getMindMapCollectionFiles() {
        log.trace("Entering getMindMapCollectionFiles()"); //$NON-NLS-1$
        log.trace("Leaving getMindMapCollectionFiles(): filelist"); //$NON-NLS-1$
        return (ArrayList<File>) this.mindMapCollectionFiles;

    }

    /**
     * Sets the list of individual MindMaps in the MindMapCollection.
     * 
     * @param mindMapCollectionFiles
     *            the mindMapCollectionFiles to set
     */
    protected void setMindMapCollectionFiles(List<File> mindMapCollectionFiles) {
        log.trace("Entering setMindMapCollection(mindMapCollectionFiles=)"); //$NON-NLS-1$

        this.mindMapCollectionFiles = mindMapCollectionFiles;

        log.trace("Leaving setMindMapCollection()"); //$NON-NLS-1$
    }

    /**
     * Returns the array list of the names of the individual MindMaps in the
     * MindMapCollection.
     * 
     * @return the mindMapFileNames as a ArrayList of Strings
     */
    public ArrayList<String> getMindMapFileNames() {
        log.trace("Entering getMindMapFileNames()"); //$NON-NLS-1$
        log.trace("Leaving getMindMapFileNames(): file name list"); //$NON-NLS-1$
        return (ArrayList<String>) this.mindMapFileNames;
    }

    /**
     * Sets the list of the names of the individual MindMaps in the
     * MindMapCollection.
     * 
     * @param mindMapFileNames
     *            the mindMapFileNames to set
     */
    protected void setMindMapFileNames(List<String> mindMapFileNames) {
        log.trace("Entering setMindMapFileNames(mindMapFileNames=)"); //$NON-NLS-1$
        this.mindMapFileNames = mindMapFileNames;
        log.trace("Leaving getMindMapFileNames()"); //$NON-NLS-1$
    }

    /* *************Delegates************** */
    /**
     * Returns the title of the loadedMindMap.
     * 
     * @return the title of the loadedMindMap
     * 
     * @see de.sarahw.ma.pc.mindMapper.model.MindMap#getMindMapTitle()
     */
    protected String getLoadedMindMapTitle() {
        log.trace("Entering getLoadedMindMapTitle()"); //$NON-NLS-1$
        log.trace("Leaving getLoadedMindMapTitle(): " + getLoadedMindMap().getMindMapTitle()); //$NON-NLS-1$   
        return getLoadedMindMap().getMindMapTitle();
    }

    /* **********Object methods********** */
    /**
     * Loads a MindMap object from the File list in the MindMapCollection via
     * deserialization and sets the active MindMap.
     * 
     * @param file
     *            file the File of the MindMap to be loaded
     * @return result of deserialization process
     */
    public boolean loadMindMap(File file) {

        log.debug("Entering loadMindMap(file=" + file + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        if (file != null) {
            MindMap loadedMap = MindMapDeserializer.getMindMapFromDisc(file);
            if (loadedMap == null) {
                log.debug("Leaving loadMindMap(): false; deserialization failed"); //$NON-NLS-1$
                return false;
            }

            setLoadedMindMap(loadedMap);
            log.debug("Leaving loadMindMap(): true"); //$NON-NLS-1$ 
            return true;
        }
        log.error("Leaving loadMindMap(): false, wrong null input"); //$NON-NLS-1$ 
        return false;

    }

    /**
     * Updates the current file list of the MindMapCollection.
     * 
     */
    public void updateMindMapFilesList() {

        log.debug("Entering updateMindMapFilesList()"); //$NON-NLS-1$

        File folder = new File(FILE_PATH);
        File[] listOfFiles = folder.listFiles();
        String fileName;

        // clear / (re-) initialize arrays
        this.mindMapCollectionFiles = new ArrayList<File>();
        this.mindMapFileNames = new ArrayList<String>();

        if (listOfFiles != null) {

            // Get paths and names for all .mindMap files in the directory
            int i, j;
            for (i = 0, j = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    fileName = listOfFiles[i].getName();

                    // TODO: Change to interface filenamefilter ??
                    // Check if files are .mindMap files, add to lists
                    if (fileName.endsWith(MindMapSerializer.FILE_EXTENSION)) {
                        this.mindMapFileNames.add(fileName);
                        this.mindMapCollectionFiles.add(listOfFiles[i]);
                        j++;

                    }

                }
            }
            log.debug(" Found " + j + " .mindMap files"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        log.debug("Leaving updateMindMapFilesList()"); //$NON-NLS-1$

    }

    /**
     * Communicates changes to all registered Observers.
     * 
     * @param object
     *            the observer notification object
     * 
     */
    private void communicateChangesToObserver(ObserverNotificationObject object) {

        log.debug("Entering communicateChangesToObserver()"); //$NON-NLS-1$

        setChanged();
        notifyObservers(object);

        log.debug("Leaving communicateChangesToObserver()"); //$NON-NLS-1$
    }

    /**
     * Gets a .mindMap file by its name
     * 
     * @param name
     *            the name of the searched File (without the .mindMap
     *            extension!)
     * @return the File if it is found, else null
     */
    public File getFileByName(String name) {

        log.debug("Entering getFileByName(name=" + name + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        if (name != null) {

            // Update file list
            this.updateMindMapFilesList();

            List<File> fileList = this.mindMapCollectionFiles;

            // Search for the file with the given name and return if found
            int i = 0;
            for (File file : fileList) {
                i++;
                log.trace("File Nr. " + i + " has name:" + file.getName()); //$NON-NLS-1$//$NON-NLS-2$

                if (file.getName().equals(
                        name + MindMapSerializer.FILE_EXTENSION)) {

                    log.debug("Leaving getFileByName()"); //$NON-NLS-1$
                    return file;
                }

            }
            log.warn("Leaving getFileByName(): null - file not found!"); //$NON-NLS-1$
            return null;

        }
        log.error("Leaving getFileByName(): null, invalid input parameter (null)"); //$NON-NLS-1$
        return null;

    }
}
