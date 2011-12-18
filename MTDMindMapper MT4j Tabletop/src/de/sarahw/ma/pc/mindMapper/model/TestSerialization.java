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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit test for testing serialization and deserialization of a MindMap object.
 * 
 * Tested classes {@link de.sarahw.ma.pc.mindMapper.model.MindMapDeserializer},
 * {@link de.sarahw.ma.pc.mindMapper.model.MindMapSerializer}
 * 
 * @author Sarah Will
 * 
 * @version 1.0
 * 
 */
@SuppressWarnings("nls")
public class TestSerialization {

    private static Logger     log = Logger.getLogger(TestSerialization.class);

    /** Test mindMap */
    private MindMap           testMindMap;
    /** Test mindMapCollection */
    private MindMapCollection mindMapCollection;
    /** First test ideaNode */
    private IdeaNode          testIdeaNode1;
    /** Second test ideaNode */
    private IdeaNode          testIdeaNode2;
    /** Third test ideaNode */
    private IdeaNode          testIdeaNode3;
    /** Fourth test ideaNode */
    private IdeaNode          testIdeaNode4;

    /* *********Fixture methods********* */
    /**
     * Sets up the fixture. Instantiates a new MindMap in a MindMapCollection
     * and adds four IdeaNodes, two of them form a Map. This method is called
     * before a test is executed.
     */
    @Before
    public void setUp() {
        // Create a new MindMapCollection
        this.mindMapCollection = MindMapCollection.getInstance();

        this.testMindMap = this.mindMapCollection.getLoadedMindMap();

        // Create a new mindMap with a few ideaNodes and Maps

        // Create new test IdeaNodes and add to mindMap
        this.testIdeaNode1 = new IdeaNode(new NodeData(
                new NodeContent("Idee1"), //$NON-NLS-1$
                new NodeMetaData(112, 843, 23.0f,
                        EIdeaNodeCreator.BLUETOOTH_SERVER)));

        this.testIdeaNode2 = new IdeaNode(new NodeData(
                new NodeContent("Idee2"), //$NON-NLS-1$
                new NodeMetaData(66, 134, 44.0f,
                        EIdeaNodeCreator.MULTITOUCH_TABLE)));

        this.testIdeaNode3 = new IdeaNode(new NodeData(
                new NodeContent("Idee3"), //$NON-NLS-1$
                new NodeMetaData(45, 123, 1.0f,
                        EIdeaNodeCreator.MULTITOUCH_TABLE)));

        this.testIdeaNode4 = new IdeaNode(new NodeData(
                new NodeContent("Idee4"), //$NON-NLS-1$
                new NodeMetaData(22, 567, 34.0f,
                        EIdeaNodeCreator.BLUETOOTH_SERVER)));

        // Add fourthTestNode as child of thirdTestNode
        assertTrue(
                "Error while adding fourthTestNode as child to thirdTestNode",
                this.testIdeaNode3.addIdeaChild(this.testIdeaNode4,
                        this.testMindMap) == EAddChildIdeaNodeResultCase.C1_2_P_SINGLE_C_SINGLE_CREATE_MAP);

        // Add IdeaNodes to mindMap
        this.testMindMap.addIdeaNode(this.testIdeaNode1);
        this.testMindMap.addIdeaNode(this.testIdeaNode2);
        this.testMindMap.addIdeaNode(this.testIdeaNode3);
        this.testMindMap.addIdeaNode(this.testIdeaNode4);

    }

    /**
     * Tears down the fixture. Sets all members null. This method is called
     * after a test is executed.
     * 
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        this.testMindMap = null;
        this.testIdeaNode1 = null;
        this.testIdeaNode2 = null;
        this.testIdeaNode3 = null;
        this.testIdeaNode4 = null;
        this.mindMapCollection = null;

    }

    /* ***********Test methods*********** */
    /**
     * Test method for
     * {@link de.sarahw.ma.pc.mindMapper.model.MindMapSerializer#writeMindMapToDisc(MindMap)}
     * and
     * {@link de.sarahw.ma.pc.mindMapper.model.MindMapDeserializer#getMindMapFromDisc(File)}
     * .
     * 
     * Serializes a MindMap object and deserializes it again. Checks if both
     * object trees are the same.
     */
    @Test
    public void testSerializeAndDeserialize() {
        // Set MindMap title
        this.testMindMap.setMindMapTitle("zJUnitTestMindMap");

        // Serialize the mindMap object
        assertTrue("Error: mindMap not serialized correctly",
                MindMapSerializer.writeMindMapToDisc(this.testMindMap));

        // Update file and file name lists
        this.mindMapCollection.updateMindMapFilesList();

        ArrayList<File> fileList = this.mindMapCollection
                .getMindMapCollectionFiles();
        ArrayList<String> fileNameList = this.mindMapCollection
                .getMindMapFileNames();

        // Check if file list contains at least one element
        assertTrue("Error: file list should contain at least one element",
                fileList.size() > 0);

        log.debug("fileNameList: " + fileNameList);

        // Check if file list contains file JUnitTestMindMap.mindMap
        assertTrue(
                "Error: file list names should contain JUnitTestMindMap",
                (fileNameList.indexOf("zJUnitTestMindMap"
                        + MindMapSerializer.FILE_EXTENSION) != -1));

        // Get file for JUnitTestMindMap.mindMap
        File jUnitTestFile = null;
        for (File file : fileList) {
            if (file.getName().equals(
                    "zJUnitTestMindMap" + MindMapSerializer.FILE_EXTENSION)) {

                jUnitTestFile = file;
                break;
            }
        }

        // Check if file has been found
        assertNotNull(
                "Error: File with name JUnitTestMindMap.mindMap should have been found!",
                jUnitTestFile);

        // Load MindMap JUnitTestMindMap
        assertTrue("Error: mindMap not deserialized correctly",
                this.mindMapCollection.loadMindMap(jUnitTestFile));

        // Check if all objects have been deserialized correctly
        // Check if MindMap has four IdeaNodes
        assertTrue("Error: wrong number of IdeaNodes deserialized",
                this.mindMapCollection.getLoadedMindMap()
                        .getMindMapIdeaNodeList().size() == 4);

        // Check if MindMap has one Map
        assertTrue("Error: wrong number of Maps deserialized",
                this.mindMapCollection.getLoadedMindMap().getMindMapList()
                        .size() == 1);

        // Check if the Map's root element is testIdeaNode3
        assertTrue(
                "Error: the Map's root element should be testIdeaNode3",
                this.mindMapCollection.getLoadedMindMap().getMindMapList()
                        .get(0).getRootNode().equals(this.testIdeaNode3));

        // Get IdeaNodeView objects and write to ArrayList<IdeaNodeView>
        ArrayList<IdeaNode> deserializedIdeaNodeList = new ArrayList<IdeaNode>();
        deserializedIdeaNodeList.add(this.mindMapCollection.getLoadedMindMap()
                .getMindMapIdeaNodeList().get(0));
        deserializedIdeaNodeList.add(this.mindMapCollection.getLoadedMindMap()
                .getMindMapIdeaNodeList().get(1));
        deserializedIdeaNodeList.add(this.mindMapCollection.getLoadedMindMap()
                .getMindMapIdeaNodeList().get(2));
        deserializedIdeaNodeList.add(this.mindMapCollection.getLoadedMindMap()
                .getMindMapIdeaNodeList().get(3));

        // Find all ideaNodes
        assertTrue("Error: testIdeaNode1 not deserialized correctly",
                deserializedIdeaNodeList.contains(this.testIdeaNode1));
        assertTrue("Error: testIdeaNode2 not deserialized correctly",
                deserializedIdeaNodeList.contains(this.testIdeaNode2));
        assertTrue("Error: testIdeaNode3 not deserialized correctly",
                deserializedIdeaNodeList.contains(this.testIdeaNode3));
        assertTrue("Error: testIdeaNode4 not deserialized correctly",
                deserializedIdeaNodeList.contains(this.testIdeaNode4));

    }
}
