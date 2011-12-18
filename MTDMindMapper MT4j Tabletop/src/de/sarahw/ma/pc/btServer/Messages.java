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

package de.sarahw.ma.pc.btServer;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * <p>
 * Messages class handling the external string resources for the btServer
 * package.
 * </p>
 * 
 * @author Sarah Will
 * 
 * @version 1.0
 * 
 */
public class Messages {

    /** The resource bundle name */
    private static final String         BUNDLE_NAME     = "de.sarahw.ma.pc.btServer.messages"; //$NON-NLS-1$

    /** The resource bundle containing the externalized strings */
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
                                                                .getBundle(BUNDLE_NAME);

    /* ***********Constructors*********** */
    /**
     * Private default constructor.
     */
    private Messages() {
        super();
        // no operations necessary
    }

    /* ***********Class methods*********** */
    /**
     * Gets a String from the String resources file via the specified key.
     * 
     * @param key
     *            the key of the required String
     * 
     * @return String the String from the String resources
     */
    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
}
