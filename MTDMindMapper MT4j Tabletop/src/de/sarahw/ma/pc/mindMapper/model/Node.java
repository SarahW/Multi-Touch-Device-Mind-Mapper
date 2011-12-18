/***********************************************************************
 * Copyright (c) 2006 Sujit Pal
 * 
 * Modifications Copyright (c) 2011 Sarah Will
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
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.apache.log4j.Logger;

/**
 * <p>
 * Represents a node of the Tree<T> class. The Node<T> is also a container, and
 * can be thought of as instrumentation to determine the location of the type T
 * in the Tree<T>.
 * </p>
 * 
 * <p>
 * Will be serialized upon object Serialization.
 * </p>
 * 
 * <p>
 * Adapted from
 * <A>http://sujitpal.blogspot.com/2006/05/java-data-structure-generic
 * -tree.html</A>
 * </p>
 * 
 * <p>
 * Modified 2011-08-05
 * </p>
 * 
 * @author Sujit Pal
 * @author (Modified by) Sarah Will
 * 
 * 
 * @version 1.0
 * 
 */

public class Node<T> extends Observable implements Serializable {

    private static Logger     log              = Logger.getLogger(Node.class);

    /** The serial version UID -7344436859045388490L */
    private static final long serialVersionUID = -7344436859045388490L;

    /** The data type T */
    private T                 data;

    /** The list of child nodes */
    private List<Node<T>>     children         = new ArrayList<Node<T>>();

    /* ***********Constructors*********** */
    /**
     * Default constructor. Instantiates a new Node.
     */
    protected Node() {
        super();
    }

    /**
     * Constructor. Instantiates a Node<T> with an instance of T.
     * 
     * @param data
     *            an instance of T.
     */
    protected Node(T data) {
        this();
        log.debug("Execute Node(data=" + data + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        setData(data);
    }

    /* ********Getters & Setters******** */
    /**
     * Returns the children of Node<T>. The Tree<T> is represented by a single
     * root Node<T> whose children are represented by a List<Node<T>>. Each of
     * these Node<T> elements in the List can have children. The getChildren()
     * method will return the children of a Node<T>.
     * 
     * @return the children of Node<T>
     */
    protected ArrayList<Node<T>> getChildren() {
        log.trace("Entering getChildren()"); //$NON-NLS-1$
        log.trace("Leaving getChildren(): list of children"); //$NON-NLS-1$
        return (ArrayList<Node<T>>) this.children;
    }

    /**
     * Returns the data of the Node.
     * 
     * @return the data
     */
    protected T getData() {
        log.trace("Entering getData()"); //$NON-NLS-1$
        log.trace("Leaving getData(): " + this.data); //$NON-NLS-1$
        return this.data;
    }

    /**
     * Sets the data of the Node.
     * 
     * @param data
     *            the data to set
     */
    protected void setData(T data) {
        log.trace("Entering setData(data=" + data + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        this.data = data;
        log.trace("Leaving setData()"); //$NON-NLS-1$
    }

    /* **********Object methods********** */
    /**
     * Returns the number of immediate children of this Node<T>.
     * 
     * @return the number of immediate children.
     */
    protected int getNumberOfChildren() {

        log.debug("Entering getNumberOfChildren()"); //$NON-NLS-1$
        if (this.children == null || this.children.size() == 0) {
            log.debug("Leaving getNumberOfChildren(): 0; no children"); //$NON-NLS-1$
            return 0;
        }
        log.debug("Leaving getNumberOfChildren(): " + this.children.size()); //$NON-NLS-1$
        return this.children.size();
    }

    /**
     * Adds a child to the list of children for this Node<T>.
     * 
     * @param child
     *            a Node<T> object to set.
     */
    protected boolean addChild(Node<T> child) {

        log.debug("Entering addChild(child=" + child + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        if (child != null) {
            if (this.children.add(child)) {
                log.debug("Leaving addChild(): true"); //$NON-NLS-1$
                return true;
            }
            log.debug("Leaving addChild(): false, child could not be added"); //$NON-NLS-1$
            return false;

        }
        log.debug("Leaving addChild(): false, invalid null input"); //$NON-NLS-1$
        return false;
    }

    /**
     * Removes the specified Node<T> child element.
     * 
     * @param child
     *            the child which is to delete.
     * 
     * @return true if removal was successful
     */
    protected boolean removeChild(Node<T> child) {

        log.debug("Entering removeChild(child=" + child + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        if (child != null) {
            if (this.children.remove(child)) {
                log.debug("Leaving removeChild(): true"); //$NON-NLS-1$
                return true;
            }
            log.debug("Leaving removeChild(): false, child could not be removed"); //$NON-NLS-1$
            return false;

        }
        log.debug("Leaving removeChild(): false, invalid null input"); //$NON-NLS-1$
        return false;
    }

    /* ********Overridden methods******** */
    /**
     * Returns a detailed String representation of the Node and its children.
     * 
     * @return detailed String representation of the Node and its children
     */
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("{").append(getData().toString()).append(",["); //$NON-NLS-1$//$NON-NLS-2$
        int i = 0;
        for (Node<T> node : getChildren()) {
            if (i > 0) {
                strBuilder.append(","); //$NON-NLS-1$
            }
            strBuilder.append(node.getData().toString());
            i++;
        }
        strBuilder.append("]").append("}"); //$NON-NLS-1$ //$NON-NLS-2$
        return strBuilder.toString();
    }

    /**
     * Returns a hash code value for the Node<T>.
     * 
     * @return a hash code value for this Node<T>.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((this.children == null) ? 0 : this.children.hashCode());
        result = prime * result
                + ((this.data == null) ? 0 : this.data.hashCode());
        return result;
    }

    /**
     * Compares the specified object with this Node<T> for equality. Returns
     * true if and only if the specified object is also a Node<T> with the same
     * elements.
     * 
     * @return true if the specified object is equal to this Node<T>.
     */
    @SuppressWarnings("unchecked")
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
        if (getClass() != obj.getClass()) {
            log.trace("Leaving equals(): false; no istanceof Node<T>"); //$NON-NLS-1$
            return false;
        }
        Node<T> other = (Node<T>) obj;
        if (this.children == null) {
            if (other.children != null) {
                log.trace("Leaving equals(): false; Different children list"); //$NON-NLS-1$
                return false;
            }
        } else if (!this.children.equals(other.children)) {
            log.trace("Leaving equals(): false; Different children list"); //$NON-NLS-1$
            return false;
        }
        if (this.data == null) {
            if (other.data != null) {
                log.trace("Leaving equals(): false; Different data; this data is null"); //$NON-NLS-1$
                return false;
            }
        } else if (!this.data.equals(other.data)) {
            log.trace("Leaving equals(): false; Different data"); //$NON-NLS-1$
            return false;
        }
        log.trace("Leaving equals(): true, same object"); //$NON-NLS-1$
        return true;
    }

}
