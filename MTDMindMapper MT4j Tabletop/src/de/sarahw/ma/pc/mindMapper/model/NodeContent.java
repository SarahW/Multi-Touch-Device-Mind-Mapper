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

import java.io.Serializable;

import org.apache.log4j.Logger;

/**
 * <p>
 * Represents the content of an IdeaNodeView that needs to be persisted.
 * </p>
 * 
 * <p>
 * Will be serialized upon object Serialization.
 * </p>
 * 
 * @author Sarah Will
 * 
 * @version 1.0
 * 
 */

public class NodeContent implements Serializable {

    private static Logger     log                = Logger.getLogger(NodeContent.class);

    /** The serial version UID 4128271051809102002L */
    private static final long serialVersionUID   = 4128271051809102002L;

    /** The maximum length of an idea text */
    public static final int   IDEA_TXT_LNGTH_MAX = 70;

    /** The ideaNode text */
    private String            ideaText;

    /* ***********Constructors*********** */
    /**
     * Instantiates a new NodeContentContainer with the given text.
     * 
     * 
     * @param ideaText
     *            the text of the idea
     * 
     */
    public NodeContent(String ideaText) {
        super();

        log.debug("Executing NodeContentContainer(ideaText=" + ideaText + ")"); //$NON-NLS-1$ //$NON-NLS-2$ 
        setIdeaText(ideaText);
    }

    /* ********Getters & Setters******** */
    /**
     * Returns the idea text.
     * 
     * @return the ideaText
     */
    public String getIdeaText() {
        log.trace("Entering getIdeaText()"); //$NON-NLS-1$
        log.trace("Leaving getIdeaText(): " + this.ideaText); //$NON-NLS-1$
        return this.ideaText;
    }

    /**
     * Sets the idea text.
     * 
     * @param ideaText
     *            the ideaText to set
     * 
     * @return true, if ideaText is valid
     */
    public boolean setIdeaText(String ideaText) {

        log.trace("Entering setIdeaText(ideaText" + ideaText + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        if (ideaText.length() <= IDEA_TXT_LNGTH_MAX) {
            this.ideaText = ideaText;
            log.trace("Leaving setIdeaText()"); //$NON-NLS-1$

            return true;
        }
        // Text is too long
        this.ideaText = Messages
                .getString("NodeContentContainer.setIdeaText().Error"); //$NON-NLS-1$
        log.error(" new IdeaText exceeds IDEA_TXT_LNGTH_MAX (" + IDEA_TXT_LNGTH_MAX + "chars)"); //$NON-NLS-1$ //$NON-NLS-2$
        return false;

    }

    /* ********Overridden methods******** */
    /**
     * Returns a String representation of the NodeContentContainer.
     * 
     * @return String representation of the NodeContentContainer
     */
    @Override
    public String toString() {
        return "NodeContentContainer [ideaText=" + getIdeaText() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Returns a hash code value for the NodeContentContainer.
     * 
     * @return a hash code value for this NodeContentContainer.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((getIdeaText() == null) ? 0 : getIdeaText().hashCode());
        return result;
    }

    /**
     * Compares the specified object with this NodeContentContainer for
     * equality. Returns true if and only if the specified object is also a
     * NodeContentContainer with the same elements.
     * 
     * @return true if the specified object is equal to this
     *         NodeContentContainer.
     */
    @Override
    public boolean equals(Object obj) {

        log.debug("Entering equals(obj= )"); //$NON-NLS-1$

        if (this == obj) {
            log.debug("Leaving equals(): true; same reference"); //$NON-NLS-1$

            return true;
        }
        if (obj == null) {
            log.debug("Leaving equals(): false; object is null"); //$NON-NLS-1$

            return false;
        }
        if (!(obj instanceof NodeContent)) {
            log.debug("Leaving equals(): false; no istanceof NodeContent"); //$NON-NLS-1$

            return false;
        }
        NodeContent other = (NodeContent) obj;
        if (getIdeaText() == null) {
            if (other.getIdeaText() != null) {
                log.debug("Leaving equals(): false; Different idea text, this one's is null"); //$NON-NLS-1$

                return false;
            }
        } else if (!getIdeaText().equals(other.getIdeaText())) {
            log.debug("Leaving equals(): false; Different idea text"); //$NON-NLS-1$

            return false;
        }
        return true;
    }

}
