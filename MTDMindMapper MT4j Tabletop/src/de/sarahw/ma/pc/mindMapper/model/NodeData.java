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
 * Represents the data of an IdeaNodeView that needs to be persisted.
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

public class NodeData implements Serializable {

    private static Logger     log              = Logger.getLogger(NodeData.class);

    /** The serial version UID -3479209053334365898L */
    private static final long serialVersionUID = -3479209053334365898L;

    /** The ID of the nodeData */
    // TODO: problematic because two NodeData objects can have the same ID ?
    private long              nodeDataID;

    /** The nodeConent contained in the nodeData */
    private NodeContent       nodeContent;

    /** The nodeMetaData of the nodeData */
    private NodeMetaData      nodeMetaData;

    /* ***********Constructors*********** */
    /**
     * Instantiates a new NodeData with NodeContent and NodeMetaData.
     * 
     * @param nodeContent
     *            the nodeContent of the idea
     * @param nodeMetaData
     *            the nodeMetaData of the idea
     */
    public NodeData(NodeContent nodeContent, NodeMetaData nodeMetaData) {
        super();

        log.debug("Executing NodeData(nodeContent=" + nodeContent + ", nodeMetaData=" + nodeMetaData + ")"); //$NON-NLS-2$ //$NON-NLS-1$ //$NON-NLS-3$
        setNodeContent(nodeContent);
        setNodeMetaData(nodeMetaData);
        setNodeDataID(this.hashCode());
    }

    /* ********Getters & Setters******** */
    /**
     * Returns the unique ID for the NodeData object.
     * 
     * @return the nodeDataID
     */
    public long getNodeDataID() {
        log.trace("Entering getNodeDataID()"); //$NON-NLS-1$
        log.trace("Leaving getNodeDataID(): " + this.nodeDataID); //$NON-NLS-1$
        return this.nodeDataID;
    }

    /**
     * Sets the unique ID for the NodeData object.
     * 
     * @param nodeDataID
     *            the nodeDataID to set
     */
    protected void setNodeDataID(long nodeDataID) {
        log.trace("Entering setNodeDataID(nodeDataID=" + nodeDataID + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        this.nodeDataID = nodeDataID;
        log.trace("Leaving setNodeDataID()"); //$NON-NLS-1$
    }

    /**
     * Returns the nodeContent of the nodeData.
     * 
     * @return the content of the nodeData.
     */
    protected NodeContent getNodeContent() {
        log.trace("Entering getNodeContent()"); //$NON-NLS-1$
        log.trace("Leaving getNodeContent(): "); //$NON-NLS-1$
        return this.nodeContent;
    }

    /**
     * Sets the NodeContentContainer of the NodeData object.
     * 
     * @param nodeContent
     *            the node content to set
     */
    protected void setNodeContent(NodeContent nodeContent) {
        log.trace("Entering getNodeContent(nodeContent=" + nodeContent + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        this.nodeContent = nodeContent;
        log.trace("Leaving setNodeContent()"); //$NON-NLS-1$
    }

    /**
     * Returns the NodeMetaData of the NodeData
     * 
     * @return the node meta data
     */
    protected NodeMetaData getNodeMetaData() {
        log.trace("Entering getNodeMetaData()"); //$NON-NLS-1$
        log.trace("Leaving getNodeMetaData(): "); //$NON-NLS-1$
        return this.nodeMetaData;
    }

    /**
     * Sets the nodeMetaData of the NodeData.
     * 
     * @param nodeMetaData
     *            the node meta data to set
     */
    protected void setNodeMetaData(NodeMetaData nodeMetaData) {
        log.trace("Entering setNodeMetaData(nodeMetaData=" + nodeMetaData + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        this.nodeMetaData = nodeMetaData;
        log.trace("Leaving setNodeMetaData()"); //$NON-NLS-1$
    }

    /* *************Delegates************** */
    /**
     * Returns the idea text of the NodeContentContainer.
     * 
     * @return getNodeContent().getIdeaText()
     * @see de.sarahw.ma.pc.mindMapper.model.NodeContent#getIdeaText()
     */
    protected String getIdeaText() {
        log.trace("Entering getIdeaText()"); //$NON-NLS-1$
        log.trace("Leaving getIdeaText(): "); //$NON-NLS-1$
        return getNodeContent().getIdeaText();
    }

    /**
     * Sets the idea text of the NodeContentContainer.
     * 
     * @param ideaText
     *            ideaText of the NodeContentContainer.
     * @return getNodeContent().setIdeaText(ideaText)
     * @see de.sarahw.ma.pc.mindMapper.model.NodeContent#setIdeaText(java.lang.String)
     */
    protected boolean setIdeaText(String ideaText) {
        log.trace("Entering setIdeaText(ideaText=" + ideaText + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        log.trace("Leaving setIdeaText()"); //$NON-NLS-1$
        return getNodeContent().setIdeaText(ideaText);

    }

    /**
     * Returns the idea rotation in degrees.
     * 
     * @return getNodeMetaData().getIdeaRotationInDegrees()
     * @see de.sarahw.ma.pc.mindMapper.model.NodeMetaData#getIdeaRotationInDegrees()
     */
    protected float getIdeaRotationInDegrees() {
        log.trace("Entering getIdeaRotationInDegrees()"); //$NON-NLS-1$
        log.trace("Leaving getIdeaRotationInDegrees(): "); //$NON-NLS-1$
        return getNodeMetaData().getIdeaRotationInDegrees();
    }

    /**
     * Sets the idea rotation in degrees.
     * 
     * @param ideaRotationInDegrees
     *            ideaRotationInDegrees of NodeMetaData
     * 
     * @return getNodeMetaData().setIdeaRotationInDegrees(ideaRotationInDegrees)
     * @see de.sarahw.ma.pc.mindMapper.model.NodeMetaData#setIdeaRotationInDegrees(float)
     */
    protected boolean setIdeaRotationInDegrees(float ideaRotationInDegrees) {
        log.trace("Entering setIdeaRotationInDegrees(ideaRotationInDegrees=" + ideaRotationInDegrees + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        log.trace("Leaving setIdeaRotationInDegrees()"); //$NON-NLS-1$

        return getNodeMetaData()
                .setIdeaRotationInDegrees(ideaRotationInDegrees);
    }

    /**
     * Returns the ideaOwner of NodeMetaData.
     * 
     * @return getNodeMetaData().getIdeaOwner()
     * @see de.sarahw.ma.pc.mindMapper.model.NodeMetaData#getIdeaOwner()
     */
    protected EIdeaNodeCreator getIdeaOwner() {
        log.trace("Entering getIdeaOwner()"); //$NON-NLS-1$
        log.trace("Leaving getIdeaOwner(): "); //$NON-NLS-1$
        return getNodeMetaData().getIdeaOwner();
    }

    /**
     * Sets the ideaOwner of NodeMetaData.
     * 
     * @param ideaOwner
     *            ideaOwner of NodeMetaData
     * @see NodeMetaData#setIdeaOwner(EIdeaNodeCreator)
     */
    protected void setIdeaOwner(EIdeaNodeCreator ideaOwner) {
        log.trace("Entering setIdeaOwner(ideaOwnerID=" + ideaOwner + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        getNodeMetaData().setIdeaOwner(ideaOwner);
        log.trace("Leaving setIdeaOwner()"); //$NON-NLS-1$
    }

    /**
     * Returns the ideaPositionX of NodeMetaData.
     * 
     * @return getNodeMetaData().getPositionX()
     * @see de.sarahw.ma.pc.mindMapper.model.NodeMetaData#getPositionX()
     */
    protected float getIdeaPositionX() {
        log.trace("Entering getIdeaPositionX()"); //$NON-NLS-1$
        log.trace("Leaving getIdeaPositionX(): "); //$NON-NLS-1$
        return getNodeMetaData().getPositionX();
    }

    /**
     * Sets the ideaPosition of NodeMetaData.
     * 
     * @param x
     *            positionX of NodeMetaData
     * @see NodeMetaData#setPositionX(float)
     */
    protected boolean setIdeaPositionX(float x) {
        log.trace("Entering setIdeaPositionX(x=" + x + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        log.trace("Leaving setIdeaPositionX()"); //$NON-NLS-1$
        return getNodeMetaData().setPositionX(x);

    }

    /**
     * Returns the ideaPositionY of NodeMetaData.
     * 
     * @return getNodeMetaData().getPositionY()
     * @see de.sarahw.ma.pc.mindMapper.model.NodeMetaData#getPositionY()
     */
    protected float getIdeaPositionY() {
        log.trace("Entering getIdeaPositionY()"); //$NON-NLS-1$
        log.trace("Leaving getIdeaPositionY(): "); //$NON-NLS-1$
        return getNodeMetaData().getPositionY();
    }

    /**
     * Sets the y position of NodeMetaData.
     * 
     * @param y
     *            positionY of NodeMetaData
     * 
     * @return getNodeMetaData().setPositionY(y)
     * @see de.sarahw.ma.pc.mindMapper.model.NodeMetaData#setPositionY(float)
     */
    protected boolean setIdeaPositionY(float y) {
        log.trace("Entering setIdeaPositionY(y=" + y + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        log.trace("Leaving setIdeaPositionY()"); //$NON-NLS-1$
        return getNodeMetaData().setPositionY(y);

    }

    /**
     * Gets the ideaState of NodeMetaData.
     * 
     * @return getNodeMetaData().getIdeaState()
     * @see de.sarahw.ma.pc.mindMapper.model.NodeMetaData#getIdeaState()
     */
    protected EIdeaState getIdeaState() {
        log.trace("Entering getIdeaState()"); //$NON-NLS-1$
        log.trace("Leaving getIdeaState(): "); //$NON-NLS-1$
        return getNodeMetaData().getIdeaState();
    }

    /**
     * Sets the ideaState of NodeMetaData.
     * 
     * @param ideaState
     *            ideaState of NodeMetaData
     * @see de.sarahw.ma.pc.mindMapper.model.NodeMetaData#setIdeaState(de.sarahw.ma.pc.mindMapper.model.EIdeaState)
     */
    protected void setIdeaState(EIdeaState ideaState) {
        log.trace("Entering setIdeaState(ideaState=" + ideaState + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        getNodeMetaData().setIdeaState(ideaState);
        log.trace("Leaving setIdeaState()"); //$NON-NLS-1$
    }

    /* ********Overridden methods******** */
    /**
     * Returns a String representation of the NodeData.
     * 
     * @return String representation of the NodeData
     */
    @Override
    public String toString() {
        return "NodeData [nodeDataID=" + getNodeDataID() + ", nodeContent=" //$NON-NLS-1$ //$NON-NLS-2$
                + getNodeContent()
                + ", nodeMetaData=" + getNodeMetaData() + "]"; //$NON-NLS-1$//$NON-NLS-2$
    }

    /**
     * Returns a hash code value for the NodeData.
     * 
     * @return a hash code value for this NodeData.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((getNodeContent() == null) ? 0 : getNodeContent().hashCode());
        result = prime * result
                + (int) (getNodeDataID() ^ (getNodeDataID() >>> 32));
        result = prime
                * result
                + ((getNodeMetaData() == null) ? 0 : getNodeMetaData()
                        .hashCode());
        return result;
    }

    /**
     * Compares the specified object with this NodeData for equality. Returns
     * true if and only if the specified object is also a NodeData with the same
     * elements.
     * 
     * @return true if the specified object is equal to this NodeData.
     */
    @Override
    public boolean equals(Object obj) {
        log.trace("Entering equals(obj= )"); //$NON-NLS-1$

        if (this == obj) {

            log.trace("Leaving equals(): true; same reference"); //$NON-NLS-1$

            return true;
        }
        if (obj == null) {
            log.trace("Leaving equals(): false; object is null"); //$NON-NLS-1$

            return false;
        }
        if (!(obj instanceof NodeData)) {
            log.trace("Leaving equals(): false; no istanceof NodeData"); //$NON-NLS-1$

            return false;
        }
        NodeData other = (NodeData) obj;
        if (getNodeContent() == null) {
            if (other.getNodeContent() != null) {
                log.trace("Leaving equals(): false; Different NodeContent, this one's is null"); //$NON-NLS-1$

                return false;
            }
        } else if (!getNodeContent().equals(other.getNodeContent())) {
            log.trace("Leaving equals(): false; Different NodeContent"); //$NON-NLS-1$

            return false;
        }
        if (getNodeDataID() != other.getNodeDataID()) {
            log.trace("Leaving equals(): false; Different NodeDataID"); //$NON-NLS-1$

            return false;
        }
        if (getNodeMetaData() == null) {
            if (other.getNodeMetaData() != null) {
                log.trace("Leaving equals(): false; Different NodeMetaData, this one's null"); //$NON-NLS-1$

                return false;
            }
        } else if (!getNodeMetaData().equals(other.getNodeMetaData())) {
            log.trace("Leaving equals(): false; Different NodeMetaData"); //$NON-NLS-1$

            return false;
        }
        log.trace("Leaving true(): true, same object"); //$NON-NLS-1$

        return true;
    }

}
