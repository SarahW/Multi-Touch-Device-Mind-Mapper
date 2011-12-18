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

package de.sarahw.ma.pc.mindMapper.view;

import org.apache.log4j.Logger;

import de.sarahw.ma.pc.mindMapper.model.AppModel;

/**
 * <p>
 * Application view class. Initializes the multi-touch graphical user interface.
 * </p>
 * <p>
 * Communication about model changes to this View are realized via the Observer
 * pattern.
 * </p>
 * 
 * @author Sarah Will
 * 
 * @version 1.0
 * 
 */
public class AppView {

    private static Logger  log = Logger.getLogger(AppView.class);

    /* *** Application *** */
    /** The application model instance */
    private AppModel       modelReference;

    /** The application view instance */
    private static AppView instance;

    /* ***********Constructors*********** */
    /**
     * Private constructor. Instantiates a new AppView
     * 
     */
    private AppView() {

        log.debug("Executing AppView()"); //$NON-NLS-1$

        // Startup MT-GUI with the loading scene
        StartMindMapView.initializeStartLoadingView(this);

    }

    /* ********Getters & Setters******** */
    /**
     * Returns an instance of AppView. >> Singleton-Pattern
     * 
     * @return instance of AppView
     */
    public synchronized static AppView getInstance() {
        if (instance == null) {
            instance = new AppView();
        }
        return instance;
    }

    /**
     * Returns the reference to the model.
     * 
     * @return the modelReference
     */
    protected AppModel getModelReference() {
        return this.modelReference;
    }

}
