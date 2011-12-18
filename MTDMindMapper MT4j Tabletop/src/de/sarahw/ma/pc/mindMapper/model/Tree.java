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

import org.apache.log4j.Logger;

/**
 * <p>
 * Represents a Tree of Objects of a generic type T. The Tree is represented as
 * a single rootElement which points to a List<Node<T>> of children. There is no
 * restriction on the number of children that a particular node may have.
 * </p>
 * 
 * <p>
 * This Tree provides a method to serialize the Tree into a List by doing a
 * pre-order traversal. It has several methods to allow easy update of Nodes in
 * the Tree.
 * </p>
 * 
 * <p>
 * Will be serialized upon object Serialization.
 * </p>
 * 
 * <p>
 * Adapted from
 * http://sujitpal.blogspot.com/2006/05/java-data-structure-generic-tree.html
 * </p>
 * 
 * <p>
 * Modified 2011-08-05
 * </p>
 * 
 * 
 * @author Sujit Pal
 * @author (Modified by) Sarah Will
 * 
 * @version 1.0
 * 
 */

public class Tree<T> implements Serializable {

    private static Logger     log              = Logger.getLogger(Tree.class);

    /** The serial version UID -7722742452548688879L */
    private static final long serialVersionUID = -7722742452548688879L;

    /** The root node element of the tree */
    private Node<T>           rootElement;

    /* ***********Constructors*********** */
    /**
     * Private default constructor. Currently unused.
     */
    protected Tree() {
        super();
        log.debug("Executing Tree()"); //$NON-NLS-1$
    }

    /**
     * Constructor. Instantiates a new Tree with a root Node<T>.
     * 
     * @param rootElement
     *            the root element of the tree
     */
    protected Tree(Node<T> rootElement) {
        super();
        log.debug("Executing Tree(rootElement=" + rootElement + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        setRootElement(rootElement);
    }

    /* ********Getters & Setters******** */
    /**
     * Returns the root Node of the Tree.
     * 
     * @return the rootElement
     */
    protected Node<T> getRootElement() {
        log.trace("Entering getRootElement()"); //$NON-NLS-1$
        log.trace("Leaving getRootElement(): " + this.rootElement); //$NON-NLS-1$
        return this.rootElement;
    }

    /**
     * Sets the root element for the Tree.
     * 
     * @param rootElement
     *            the rootElement to set
     * 
     * @return true, if setting the root node was successful
     */
    protected boolean setRootElement(Node<T> rootElement) {
        log.trace("Entering setRootElement(rootElement=" + rootElement + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        if (rootElement != null) {
            this.rootElement = rootElement;
            log.trace("Leaving setRootElement(): true"); //$NON-NLS-1$
            return true;
        }
        log.trace("Leaving setRootElement(): false, invalid null input"); //$NON-NLS-1$
        return false;
    }

    /* **********Object methods********** */
    /**
     * Returns the Tree<T> as a List of Node<T> objects. The elements of the
     * List are generated from a pre-order traversal of the tree.
     * 
     * @return a ArrayList<Node<T>> with all Nodes of the tree.
     */
    protected ArrayList<Node<T>> toList() {

        log.debug("Entering toList()"); //$NON-NLS-1$
        ArrayList<Node<T>> list = new ArrayList<Node<T>>();
        walk(this.rootElement, list);

        log.debug("Leaving toList(): list of all tree nodes"); //$NON-NLS-1$
        return list;

    }

    /**
     * Walks the Tree in pre-order style. This is a recursive method, and is
     * called from the toList() method with the root element as the first
     * argument. It appends to the second argument, which is passed by reference
     * as it recurses down the tree.
     * 
     * @param element
     *            the starting element.
     * @param list
     *            the output of the walk.
     */
    private void walk(Node<T> element, List<Node<T>> list) {

        log.debug("Entering walk(element=" + element + ", list= )"); //$NON-NLS-1$ //$NON-NLS-2$
        list.add(element);
        for (Node<T> data : element.getChildren()) {
            walk(data, list);
        }
        log.debug("Leaving walk()"); //$NON-NLS-1$
    }

    /**
     * Checks if a certain node is part of the tree structure.
     * 
     * @param element
     *            the node to be searched in the tree
     * @return if the node is part of the tree
     */
    protected boolean containsNode(Node<T> element) {

        log.debug("Entering containsNode(element=" + element + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        ArrayList<Node<T>> listOfNodes = this.toList();

        if (listOfNodes.contains(element)) {
            log.debug("Leaving containsNode(): true"); //$NON-NLS-1$
            return true;
        }

        log.debug("Leaving containsNode(): false"); //$NON-NLS-1$
        return false;

    }

    /* ********Overridden methods******** */
    /**
     * Returns a String representation of the Tree. The elements are generated
     * from a pre-order traversal of the Tree.
     * 
     * @return the String representation of the Tree.
     */
    @Override
    public String toString() {
        return toList().toString();

    }

    /**
     * Returns a hash code value for the Tree<T>.
     * 
     * @return a hash code value for this Tree<T>.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((getRootElement() == null) ? 0 : getRootElement().hashCode());
        return result;
    }

    /**
     * Compares the specified object with this Tree<T> for equality. Returns
     * true if and only if the specified object is also a Tree<T> with the same
     * elements.
     * 
     * @return true if the specified object is equal to this Tree<T>.
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Tree)) {
            return false;
        }
        Tree<T> other = (Tree<T>) obj;
        if (getRootElement() == null) {
            if (other.getRootElement() != null) {
                return false;
            }
        } else if (!getRootElement().equals(other.getRootElement())) {
            return false;
        }
        return true;
    }

}
