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

import java.util.ArrayList;

import org.apache.log4j.Logger;

import de.sarahw.ma.pc.mindMapper.view.IdeaNodeView;

/**
 * <p>
 * Non-generic subclass of Tree<NodeData>. Represents a Map consisting of a root
 * IdeaNodeView with at least one child IdeaNodeView. Map creation is handled
 * exclusively by the IdeaNodeView class by the methods for adding and removing
 * children.
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
 * @see IdeaNodeView
 * 
 */

public class Map extends Tree<NodeData> {

    private static Logger     log              = Logger.getLogger(Map.class);

    /** The serial version UID -6455981371831948355L */
    private static final long serialVersionUID = -6455981371831948355L;

    /** The total count of all created Maps */
    // TODO: On removal?
    private static int        mapCount         = 0;

    /** The ID of the Map (hashValue) */
    // TODO: problematic because two Maps can have the same ID ?
    private long              mapID;

    /* ***********Constructors*********** */
    /**
     * Private default constructor. Currently unused.
     */
    @SuppressWarnings("unused")
    private Map() {
        super();
        log.debug("Executing Map()"); //$NON-NLS-1$
    }

    /**
     * Instantiates a new Map with a root IdeaNodeView.
     * 
     * @param root
     *            the IdeaNodeView object which is the root node of the Map
     *            object
     */
    protected Map(IdeaNode root) {
        super(root);

        log.debug("Executing Map(root=" + root + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        // set map count
        mapCount++;

        // set unique object id
        setMapID(this.hashCode());

    }

    /* ********Getters & Setters******** */
    /**
     * Returns the current number of Map objects.
     * 
     * @return the mapCount
     */
    public static int getMapCount() {
        log.trace("Entering getMapCount()"); //$NON-NLS-1$ 
        log.trace("Leaving getMapCount(): " + mapCount); //$NON-NLS-1$ 
        return mapCount;

    }

    /**
     * Sets the number of Map objects.
     * 
     * @param mapCount
     *            the mapCount to set
     */
    protected static void setMapCount(int mapCount) {
        log.trace("Entering setMapCount(mapCount=" + mapCount + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        Map.mapCount = mapCount;
        log.trace("Leaving setMapCount()"); //$NON-NLS-1$ 
    }

    /**
     * Returns the unique ID for the Map object.
     * 
     * @return the mapID
     */
    public long getMapID() {
        log.trace("Entering getMapID()"); //$NON-NLS-1$ 
        log.trace("Leaving getMapID(): " + this.mapID); //$NON-NLS-1$ 
        return this.mapID;
    }

    /**
     * Sets the unique ID for the Map object.
     * 
     * @param mapID
     *            the mapID to set
     */
    protected void setMapID(long mapID) {
        log.trace("Entering setMapID(mapID=" + mapID + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        this.mapID = mapID;
        log.trace("Leaving setMapID()"); //$NON-NLS-1$
    }

    /* ****Superclass access methods**** */
    /**
     * Returns the root IdeaNodeView for the Map via the superclass
     * Tree<NodeData>.
     * 
     * @return the rootIdeaNode
     */
    public IdeaNode getRootNode() {
        log.trace("Entering getRootNode()"); //$NON-NLS-1$ 
        log.trace("Leaving getRootNode(): " + super.getRootElement()); //$NON-NLS-1$ 
        return (IdeaNode) super.getRootElement();

    }

    /**
     * Sets the root IdeaNodeView for the Map via the superclass Tree<NodeData>.
     * 
     * @param rootIdeaNode
     *            the rootIdeaNode to set
     * @return true, if the rootIdeaNode is not null
     */
    protected boolean setRootNode(IdeaNode rootIdeaNode) {

        log.trace("Entering setRootNode(rootIdeaNode=" + rootIdeaNode + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        if (rootIdeaNode != null) {
            if (super.setRootElement(rootIdeaNode)) {
                log.trace("Leaving setRootNode()"); //$NON-NLS-1$
                return true;
            }
            log.error("Root node " + rootIdeaNode + " could not be set!"); //$NON-NLS-1$//$NON-NLS-2$
            return false;

        }
        log.trace("Leaving setRootNode(), invalid null input"); //$NON-NLS-1$
        return false;
    }

    /**
     * Checks whether a certain Node<NodeData> is part of the Map.
     * 
     * @param element
     *            the Node<NodeData> to be searched
     * @return if the Node<NodeData> is part of the Map
     */
    public boolean containsIdeaNode(Node<NodeData> element) {

        log.debug("Entering containsIdeaNode()"); //$NON-NLS-1$

        log.debug("Leaving containsIdeaNode(): result of contains"); //$NON-NLS-1$
        return super.containsNode(element);
    }

    /* ********Overridden methods******** */
    /**
     * Returns the Map as a List of Node<NodeData> objects. The elements of the
     * List are generated from a pre-order traversal of the tree.
     * 
     * @return a ArrayList<Node<NodeData>> with all Nodes of the tree.
     */
    @Override
    public ArrayList<Node<NodeData>> toList() {

        log.debug("Entering toList()"); //$NON-NLS-1$

        log.debug("Leaving toList(): list of all tree nodes"); //$NON-NLS-1$
        return super.toList();

    }

    /**
     * Returns a simple String representation of the Map.
     * 
     * @return the simple String representation of the Map.
     */
    @Override
    public String toString() {
        return "Map [mapID=" + getMapID() + "]"; //$NON-NLS-1$//$NON-NLS-2$
    }

    /**
     * Returns a hash code value for the Map.
     * 
     * @return a hash code value for this Map.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result;
        return result;
    }

    /**
     * Compares the specified object with this Map for equality. Returns true if
     * and only if the specified object is also a Map with the same elements.
     * 
     * @return true if the specified object is equal to this Map.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof Map)) {
            return false;
        }

        return true;
    }

}
