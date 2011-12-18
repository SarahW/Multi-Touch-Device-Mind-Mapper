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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.apache.log4j.Logger;

/**
 * <p>
 * Used to deserialize a MindMap object.
 * </p>
 * 
 * @author Sarah Will
 * 
 * @version 1.0
 * 
 */
public class MindMapDeserializer {

    private static Logger log = Logger.getLogger(MindMapDeserializer.class);

    /* ***********Constructors*********** */
    /**
     * Private constructor. Doesn't allow instances of this class.
     */
    private MindMapDeserializer() {
        //
    }

    /* **********Class methods********** */
    /**
     * Deserializes a MindMap object.
     * 
     * @param file
     *            the MindMap file
     * @return the loaded MindMap or null if deserialization failed
     */
    public static MindMap getMindMapFromDisc(File file) {

        log.debug("Entering getMindMapFromDisc(file=" + file + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        if (file != null) {

            MindMap loadedMindMap = null;
            FileInputStream fis = null;
            ObjectInputStream in = null;

            // Read MindMap object
            try {
                // Create new object input stream
                fis = new FileInputStream(file);
                in = new ObjectInputStream(fis);

                // Try to read object
                loadedMindMap = (MindMap) in.readObject();

                // Close object input stream
                in.close();
            } catch (IOException ex) {

                log.error("Leaving getMindMapFromDisc(): null; on error, deserialization not successful:" + //$NON-NLS-1$ 
                        ex.getMessage());
                ex.printStackTrace();
                return null;

            } catch (ClassNotFoundException ex) {

                log.error("Leaving getMindMapFromDisc(): null; on error, deserialization not successful" + //$NON-NLS-1$ 
                        ex.getMessage());
                ex.printStackTrace();
                return null;
            }

            log.debug("Leaving getMindMapFromDisc(): " + loadedMindMap); //$NON-NLS-1$ 
            return loadedMindMap;

        }
        log.error("Leaving getMindMapFromDisc(): null; on error, invalid null input"); //$NON-NLS-1$ 
        return null;

    }

}
