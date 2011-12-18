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

package de.sarahw.ma.pc.mindMapper;

import de.sarahw.ma.pc.mindMapper.view.AppView;

/**
 * Main class of the MindMapper application. Initializes a model, a bluetooth
 * server and a view instance.
 * 
 * @author Sarah Will
 * @version 1.0
 * 
 */
public class MindMapperMain {

    /**
     * Main method of the MindMapper application.
     * 
     * @param args
     *            arguments (not used)
     */
    public static void main(String[] args) {

        // Initialize loading screen
        // The model and the bluetooth server will
        // be initialized after that
        AppView appView = AppView.getInstance();

    }
}
