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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Random;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit test class for {@link de.sarahw.ma.pc.mindMapper.model.NodeContent}
 * 
 * @author Sarah Will
 * 
 * @version 1.0
 * 
 */
@SuppressWarnings("nls")
public class TestNodeContent {

    private static Logger log = Logger.getLogger(TestNodeContent.class);

    /** Test nodeContent */
    private NodeContent   testNodeContent;

    /* *********Fixture methods********* */
    /**
     * Sets up the fixture. Instantiates a new NodeContent.
     */
    @Before
    public void setUp() {
        NodeContent testNodeContent = new NodeContent("Test");
        this.testNodeContent = testNodeContent;
    }

    /**
     * Tears down the fixture. Sets all members null. This method is called
     * after a test is executed.
     * 
     */
    @After
    public void tearDown() {
        this.testNodeContent = null;
    }

    /* ***********Test methods*********** */
    /**
     * Test method for
     * {@link de.sarahw.ma.pc.mindMapper.model.NodeContent#NodeContent(java.lang.String)}
     * .
     */
    @Test
    public void testMembersAfterInitialization() {
        // Check if NodeContentContainer text is correct
        assertTrue(
                "Error: NodeContentContainer text has been initialized incorrectly",
                this.testNodeContent.getIdeaText().equals("Test"));
    }

    /**
     * Test method for
     * {@link de.sarahw.ma.pc.mindMapper.model.NodeContent#setIdeaText(java.lang.String)}
     * .
     */
    @Test
    public void testSetIdeaText() {
        // Test normal-sized String
        this.testNodeContent.setIdeaText("Testversuch normaler String");
        assertTrue(
                "Error: NodeContentContainer text has not been set correctly",
                this.testNodeContent.getIdeaText() == "Testversuch normaler String");

        // Test String that has length of
        // NodeContentContainer.IDEA_TXT_LNGTH_MAX + 1
        StringBuilder stringBuilder = new StringBuilder();
        Random r = new Random();
        for (int i = 0, result = 0; i < NodeContent.IDEA_TXT_LNGTH_MAX + 1; i++) {
            result = r.nextInt(125 - 33) + 33;
            log.debug("Loop Nr." + i + " generated int:" + result);
            stringBuilder.append((char) result);
        }

        log.debug("String: " + stringBuilder.toString() + " Länge: "
                + stringBuilder.toString().length());
        this.testNodeContent.setIdeaText(stringBuilder.toString());

        assertTrue(
                "Error: the set String should be Error",
                this.testNodeContent
                        .getIdeaText()
                        .equals(Messages
                                .getString("NodeContentContainer.setIdeaText().Error")));

        // Test String that has length of
        // NodeContentContainer.IDEA_TXT_LNGTH_MAX - 1
        stringBuilder = new StringBuilder();
        r = new Random();
        for (int i = 0, result = 0; i < NodeContent.IDEA_TXT_LNGTH_MAX; i++) {
            result = r.nextInt(125 - 33) + 33;
            log.debug("Loop Nr." + i + " generated int:" + result);
            stringBuilder.append((char) result);
        }

        log.debug("String: " + stringBuilder.toString());
        this.testNodeContent.setIdeaText(stringBuilder.toString());

        assertTrue(
                "Error: the set String should be the previously built String with length == IDEA_TXT_LNGTH_MAX - 1",
                this.testNodeContent.getIdeaText().equals(
                        stringBuilder.toString()));

    }

    /**
     * Test method for
     * {@link de.sarahw.ma.pc.mindMapper.model.NodeContent#hashCode()}.
     */
    @Test
    public void testHashCode() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link de.sarahw.ma.pc.mindMapper.model.NodeContent#toString()}.
     */
    @Test
    public void testToString() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link de.sarahw.ma.pc.mindMapper.model.NodeContent#equals(java.lang.Object)}
     * .
     */
    @Test
    public void testEqualsObject() {
        fail("Not yet implemented");
    }

}
