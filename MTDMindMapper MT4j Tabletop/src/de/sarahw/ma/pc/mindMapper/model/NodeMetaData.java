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
 * Represents the meta data of NodeData that needs to be persisted.
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

public class NodeMetaData implements Serializable {

    private static Logger      log                                 = Logger.getLogger(NodeMetaData.class);

    /** The serial version UID 3533513073529688380L */
    private static final long  serialVersionUID                    = 3533513073529688380L;

    /** The minimum rotation of an ideaNode */
    private static final float IDEA_ROTATION_MIN                   = -181.0f;

    /** The maximum rotation of an ideaNode */
    private static final float IDEA_ROTATION_MAX                   = 181.0f;

    /** The tolerance offset that an ideaNode position might have */
    private static final float IDEA_NODE_POSITION_OFFSET_TOLERANCE = -5.0f;

    /** The ideaNode creator */
    private EIdeaNodeCreator   ideaCreator;

    /** The rotation of an ideaNode in degrees */
    private float              ideaRotationInDegrees;

    /** The x position of an ideaNode */
    private float              positionX;

    /** The y position of an ideaNode */
    private float              positionY;

    /** The state of an ideaNode */
    private EIdeaState         ideaState;

    /* ***********Constructors*********** */
    /**
     * Instantiates a new NodeMetaData with position, angle, and owner.
     * 
     * @param x
     *            the x position of the idea
     * @param y
     *            the y position of the idea
     * @param angle
     *            the angle of the idea in relation to the equator of the screen
     * @param owner
     *            the owner of the idea represented by EIdeaNodeCreator
     */
    public NodeMetaData(float x, float y, float angle, EIdeaNodeCreator owner) {
        super();

        log.debug("Executing NodeMetaData(x=" + x + ", y=" + y + ", angle=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                + angle + ", owner=" + owner + ")"); //$NON-NLS-1$ //$NON-NLS-2$ 

        setPositionX(x);
        setPositionY(y);
        setIdeaRotationInDegrees(angle);
        setIdeaOwner(owner);
        setIdeaState(EIdeaState.ACTIVE);
    }

    /* ********Getters & Setters******** */
    /**
     * Returns the idea rotation in degrees.
     * 
     * @return the idea rotation in degrees
     */
    public float getIdeaRotationInDegrees() {
        log.trace("Entering getIdeaRotationInDegrees()"); //$NON-NLS-1$
        log.trace("Leaving getIdeaRotationInDegrees():" + this.ideaRotationInDegrees); //$NON-NLS-1$
        return this.ideaRotationInDegrees;
    }

    /**
     * Sets the idea rotation in degrees.
     * 
     * @param ideaRotationInDegrees
     *            the angle to set between -181.0 and 181.0 degrees
     */
    public boolean setIdeaRotationInDegrees(float ideaRotationInDegrees) {
        log.trace("Entering setIdeaRotationInDegrees(ideaRotationInDegrees=" + ideaRotationInDegrees + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        if (ideaRotationInDegrees >= IDEA_ROTATION_MIN
                && ideaRotationInDegrees <= IDEA_ROTATION_MAX) {
            this.ideaRotationInDegrees = ideaRotationInDegrees;
            log.trace("Leaving setIdeaAngleToEquator()"); //$NON-NLS-1$

        }
        if (ideaRotationInDegrees < IDEA_ROTATION_MIN) {
            this.ideaRotationInDegrees = IDEA_ROTATION_MIN;
            log.warn("Leaving setIdeaAngleToEquator(), input value too small/out of range, default value -181.0 set"); //$NON-NLS-1$

        }

        if (ideaRotationInDegrees > IDEA_ROTATION_MAX) {
            this.ideaRotationInDegrees = IDEA_ROTATION_MAX;
            log.warn("Leaving setIdeaAngleToEquator(), input value too large/out of range, default value 181.0 set"); //$NON-NLS-1$

        }
        return true;

    }

    /**
     * Returns the owner/creator of this idea.
     * 
     * @return the ideaOwner
     */
    public EIdeaNodeCreator getIdeaOwner() {
        log.trace("Entering getIdeaOwner()"); //$NON-NLS-1$
        log.trace("Leaving getIdeaOwner(): " + this.ideaCreator); //$NON-NLS-1$
        return this.ideaCreator;
    }

    /**
     * Sets the owner/creator ID of this idea.
     * 
     * @param ideaCreator
     *            the ideaOwner to set
     */
    public void setIdeaOwner(EIdeaNodeCreator ideaCreator) {
        log.trace("Entering setIdeaOwner(ideaCreator=" + ideaCreator + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        this.ideaCreator = ideaCreator;
        log.trace("Leaving setIdeaOwner()"); //$NON-NLS-1$
    }

    /**
     * Returns the x position of the idea.
     * 
     * @return the positionX
     */
    public float getPositionX() {
        log.trace("Entering getPositionX()"); //$NON-NLS-1$
        log.trace("Leaving getPositionX(): " + this.positionX); //$NON-NLS-1$

        return this.positionX;
    }

    /**
     * Sets the x Position of this NodeMetaData.
     * 
     * @param positionX
     *            the positionX to set
     * 
     * @return true if positionX is valid
     */
    public boolean setPositionX(float positionX) {

        log.trace("Entering setPositionX(positionX=" + positionX + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        if (positionX > IDEA_NODE_POSITION_OFFSET_TOLERANCE) {
            this.positionX = positionX;
            log.trace("Leaving setPositionX()"); //$NON-NLS-1$
            return true;
        }
        log.error("Leaving setPositionX(), wrong input parameters with negative coordinates"); //$NON-NLS-1$
        return false;

    }

    /**
     * Returns the y position of this NodeMetaData.
     * 
     * @return the positionY
     */
    public float getPositionY() {
        log.trace("Entering getPositionY()"); //$NON-NLS-1$
        log.trace("Leaving getPositionY(): " + this.positionY); //$NON-NLS-1$

        return this.positionY;
    }

    /**
     * Sets the y Position of this NodeMetaData.
     * 
     * @param positionY
     *            the positionY to set
     * @return true if positionY is valid
     */
    public boolean setPositionY(float positionY) {
        log.trace("Entering setPositionY(positionY=" + positionY + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        if (positionY > IDEA_NODE_POSITION_OFFSET_TOLERANCE) {
            this.positionY = positionY;
            log.trace("Leaving setPositionY()"); //$NON-NLS-1$
            return true;
        }
        log.error("Leaving setPositionY(), wrong input parameters with negative coordinates"); //$NON-NLS-1$
        return false;

    }

    /**
     * Returns the idea state. See eIdeaState enumeration definition for states.
     * 
     * @return the ideaState
     * @see EIdeaState
     */

    public EIdeaState getIdeaState() {
        log.trace("Entering getIdeaState()"); //$NON-NLS-1$
        log.trace("Leaving getIdeaState(): " + this.ideaState); //$NON-NLS-1$
        return this.ideaState;
    }

    /**
     * Sets the idea state. See eIdeaState enumeration definition for states.
     * 
     * @param ideaState
     *            the ideaState to set
     * @see EIdeaState
     */
    public void setIdeaState(EIdeaState ideaState) {

        log.trace("Entering setIdeaState(ideaState=" + ideaState + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        if (ideaState != null) {
            this.ideaState = ideaState;
            log.trace("Leaving setIdeaState()"); //$NON-NLS-1$
        }
        log.warn("Leaving setIdeaState(), invalid null input"); //$NON-NLS-1$
    }

    /* ********Overridden methods******** */
    /**
     * Returns a String representation of the NodeMetaData.
     * 
     * @return String representation of the NodeMetaData
     */
    @Override
    public String toString() {
        return "NodeMetaData [ideaCreator=" + getIdeaOwner() //$NON-NLS-1$
                + ", ideaRotationInDegrees=" + getIdeaRotationInDegrees() //$NON-NLS-1$
                + ", ideaPosition=(" + getPositionX() + ", " + getPositionY() + "), ideaState=" + getIdeaState() //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                + "]"; //$NON-NLS-1$
    }

    /**
     * Returns a hash code value for the NodeMetaData.
     * 
     * @return a hash code value for this NodeMetaData.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((this.ideaCreator == null) ? 0 : this.ideaCreator.hashCode());
        result = prime * result
                + Float.floatToIntBits(this.ideaRotationInDegrees);
        result = prime * result
                + ((this.ideaState == null) ? 0 : this.ideaState.hashCode());
        result = prime * result + Float.floatToIntBits(this.positionX);
        result = prime * result + Float.floatToIntBits(this.positionY);
        return result;
    }

    /**
     * Compares the specified object with this NodeMetaData for equality.
     * Returns true if and only if the specified object is also a NodeMetaData
     * with the same elements.
     * 
     * @param obj
     *            the object to be compared
     * 
     * @return true if the specified object is equal to this NodeMetaData.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof NodeMetaData)) {
            return false;
        }
        NodeMetaData other = (NodeMetaData) obj;
        if (this.ideaCreator != other.ideaCreator) {
            return false;
        }
        if (Float.floatToIntBits(this.ideaRotationInDegrees) != Float
                .floatToIntBits(other.ideaRotationInDegrees)) {
            return false;
        }
        if (this.ideaState != other.ideaState) {
            return false;
        }
        if (Float.floatToIntBits(this.positionX) != Float
                .floatToIntBits(other.positionX)) {
            return false;
        }
        if (Float.floatToIntBits(this.positionY) != Float
                .floatToIntBits(other.positionY)) {
            return false;
        }
        return true;
    }

}
