/***********************************************************************
 * mt4j Copyright (c) 2008 - 2009, C.Ruff, Fraunhofer-Gesellschaft All rights
 * reserved.
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

package de.sarahw.ma.pc.mindMapper.view;

import org.mt4j.util.MTColor;

/**
 * <p>
 * Class containing the commonly used colors in the MindMapper project.
 * </p>
 * 
 * @author Sarah Will
 * @version 1.0
 * 
 * 
 */
public class MindMapperColors {

    // private static Logger log = Logger.getLogger(MindMapperColors.class);

    /* *** Grey shades *** */

    /** Slightly transparent black */
    public static final MTColor BLACK_SLIGHT_TRANS           = new MTColor(0,
                                                                     0, 0, 150);

    /** Slightly transparent very dark grey */
    public static final MTColor VERY_DARK_GREY_SLIGHT_TRANS  = new MTColor(10,
                                                                     10, 10,
                                                                     200);

    /** Slightly transparent dark grey */
    public static final MTColor DARK_GREY_SLIGHT_TRANS       = new MTColor(77,
                                                                     77, 77,
                                                                     200);

    /** Dark grey */
    public static final MTColor DARK_GREY                    = new MTColor(77,
                                                                     77, 77,
                                                                     255);

    /** Semi-transparent dark grey */
    public static final MTColor DARK_GREY_SEMI_TRANS         = new MTColor(77,
                                                                     77, 77,
                                                                     100);
    /** Grey */
    public static final MTColor GREY                         = new MTColor(102,
                                                                     102, 102,
                                                                     255);

    /** Semi-transparent Grey */
    public static final MTColor GREY_SEMI_TRANS              = new MTColor(102,
                                                                     102, 102,
                                                                     100);

    /** Slightly transparent light grey */
    public static final MTColor LIGHT_GREY_SLIGHT_TRANS      = new MTColor(155,
                                                                     155, 155,
                                                                     200);

    /** Slightly transparent very light grey */
    public static final MTColor VERY_LIGHT_GREY_SLIGHT_TRANS = new MTColor(190,
                                                                     190, 190,
                                                                     192);

    /** Broken white */
    public static final MTColor BROKEN_WHITE                 = new MTColor(230,
                                                                     230, 230,
                                                                     255);

    /* *** Colors *** */

    /** Slightly transparent yellow */
    public static final MTColor YELLOW_SLIGHT_TRANS          = new MTColor(34,
                                                                     188, 25,
                                                                     192);
    /** Slightly transparent red */
    public static final MTColor RED_SLIGHT_TRANS             = new MTColor(194,
                                                                     21, 14,
                                                                     192);

    /** Slightly transparent green */
    public static final MTColor GREEN_SLIGHT_TRANS           = new MTColor(237,
                                                                     236, 34,
                                                                     192);

                                                                                 ;

    /* ***********Constructors*********** */
    /**
     * Private constructor. Doesn't allow instances of this class.
     */
    private MindMapperColors() {
        //
    }

}
