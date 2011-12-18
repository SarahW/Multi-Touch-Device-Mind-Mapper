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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;

/**
 * <p>
 * Used to serialize a MindMap object.
 * </p>
 * 
 * @author Sarah Will
 * 
 * @version 1.0
 * 
 */
public class MindMapSerializer {

    private static Logger      log                 = Logger.getLogger(MindMapSerializer.class);

    /** The file extension for all save files of the application */
    public static final String FILE_EXTENSION      = ".mindMap";                               //$NON-NLS-1$

    /** The maximum length of file name strings */
    public static final int    MAX_FILENAME_LENGTH = 50;

    /* ***********Constructors*********** */
    /**
     * Private constructor. Doesn't allow instances of this class.
     */
    private MindMapSerializer() {
        //
    }

    /* **********Class methods********** */
    /**
     * Serializes a MindMap object.
     * 
     * @param mindMap
     *            the MindMap object to serialize
     * @return true, if serialization was successful
     */
    public static boolean writeMindMapToDisc(MindMap mindMap) {

        log.debug("Entering writeMindMapToDisc(mindMap=" + mindMap + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        if (mindMap != null) {

            // Get mindMap title
            String mindMapTitle = mindMap.getMindMapTitle();

            // If title length is not greater than max file name length
            if (mindMapTitle.length() > 0
                    && mindMapTitle.length() <= MAX_FILENAME_LENGTH) {

                // Create file
                File result = new File(MindMapCollection.FILE_PATH
                        + File.separator + mindMapTitle + FILE_EXTENSION);

                // Create file and object output stream
                FileOutputStream fos = null;
                ObjectOutputStream out = null;

                // Try writing the object file
                try {
                    fos = new FileOutputStream(result);
                    out = new ObjectOutputStream(fos);
                    out.writeObject(mindMap);
                    out.close();

                } catch (IOException ex) {
                    log.error("Leaving writeMindMapToDisc(): false; on error, serialization not successful:" + //$NON-NLS-1$ 
                            ex.getMessage());
                    ex.printStackTrace();
                    return false;
                }

                log.debug("Leaving writeMindMapToDisc(): true"); //$NON-NLS-1$ 
                return true;

            }
            log.error("Leaving writeMindMapToDisc(): false, invalid filename length"); //$NON-NLS-1$
            return false;
        }

        log.error("Leaving writeMindMapToDisc(): false, invalid null input"); //$NON-NLS-1$
        return false;
    }

}
