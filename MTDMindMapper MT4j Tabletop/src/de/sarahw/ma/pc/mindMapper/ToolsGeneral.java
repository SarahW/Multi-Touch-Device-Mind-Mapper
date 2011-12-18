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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * Class containing commonly used general utility methods.
 * 
 * 
 * @author Sarah Will
 * @version 1.0
 * 
 */
public class ToolsGeneral {

    protected static Logger log = Logger.getLogger(ToolsGeneral.class);

    /* ***********Constructors*********** */
    /**
     * Private constructor. Doesn't allow instances of this class.
     */
    private ToolsGeneral() {
        //
    }

    /* ***********Class methods*********** */

    /**
     * Checks whether a char or string is valid as part of a file name.
     * 
     * @return true, if string is valid
     */
    public static boolean isValid(String string) {

        log.debug("Entering isValid(string" + string + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        CharSequence inputStr = string;
        boolean isValid = false;

        // Define allowed characters (a to z, A to Z, 0 to 9, _ and -)
        String expressionValidChars = "^[a-z_A-Z0-9 \\x2D]*$"; //$NON-NLS-1$

        // Check if string matches the allowed characters
        Pattern pattern = Pattern.compile(expressionValidChars);
        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches()) {
            log.debug("Leaving isValid(): true"); //$NON-NLS-1$
            isValid = true;
        }
        log.debug("Leaving isValid(): false"); //$NON-NLS-1$
        return isValid;
    }
}
