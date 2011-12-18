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

import org.apache.log4j.Logger;

/**
 * <p>
 * Application model class. Initializes a new MindMapCollection. The application
 * model is completely independent of the and View component. Communication
 * about model changes to the View are realized via the Observer pattern.
 * </p>
 * 
 * @author Sarah Will
 * 
 * @version 1.0
 * 
 */

public class AppModel {

    private static Logger     log = Logger.getLogger(AppModel.class);

    /** The mindMapCollection instance. */
    private MindMapCollection mindMapCollection;

    /** The application model instance. */
    private static AppModel   instance;

    /* ***********Constructors*********** */
    /**
     * Default private constructor. Instantiates a new AppModel and initializes
     * the MindMapCollection.
     */
    private AppModel() {

        log.debug("Executing AppModel()"); //$NON-NLS-1$
        this.mindMapCollection = MindMapCollection.getInstance();

    }

    /* ********Getters & Setters******** */
    /**
     * Returns an instance of AppModel. >> Singleton-Pattern
     * 
     * @return instance of AppModel
     */
    public synchronized static AppModel getInstance() {
        if (instance == null) {
            instance = new AppModel();
        }
        return instance;
    }

    /**
     * Returns the active MindMapCollection instance.
     * 
     * @return the active MindMapCollection
     */
    public MindMapCollection getMindMapCollection() {
        log.trace("Entering getMindMapCollection()"); //$NON-NLS-1$
        log.trace("Leaving getMindMapCollection(): activeMindMapCollection"); //$NON-NLS-1$
        return this.mindMapCollection;
    }

    /**
     * Sets the active MindMapCollection instance.
     * 
     * @param mindMapCollection
     *            the mindMapCollection to set
     */
    protected void setMindMapCollection(MindMapCollection mindMapCollection) {
        log.trace("Entering setMindMapCollection(mindMapCollection=)"); //$NON-NLS-1$
        this.mindMapCollection = mindMapCollection;
        log.trace("Leaving setMindMapCollection()"); //$NON-NLS-1$
    }

    /* *************Delegates************** */
    /**
     * Returns the loadedMindMap MindMap in the MindMapCollection.
     * 
     * @return the loadedMindMap
     * 
     * @see de.sarahw.ma.pc.mindMapper.model.MindMapCollection#getLoadedMindMap()
     */
    public MindMap getLoadedMindMap() {
        log.trace("Entering getLoadedMindMap()"); //$NON-NLS-1$
        log.trace("Leaving getLoadedMindMap(): " + getMindMapCollection().getLoadedMindMap()); //$NON-NLS-1$   
        return getMindMapCollection().getLoadedMindMap();
    }

    /**
     * Returns the title of the loadedMindMap in the MindMapCollection.
     * 
     * @return the title of loadedMindMap
     * 
     * @see de.sarahw.ma.pc.mindMapper.model.MindMapCollection#getLoadedMindMapTitle()
     */
    public String getLoadedMindMapTitle() {
        log.trace("Entering getLoadedMindMapTitle()"); //$NON-NLS-1$
        log.trace("Leaving getLoadedMindMapTitle(): " + getMindMapCollection().getLoadedMindMapTitle()); //$NON-NLS-1$   
        return getMindMapCollection().getLoadedMindMapTitle();
    }

}
