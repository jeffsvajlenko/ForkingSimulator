/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.gtk;

import java.awt.Color;
import java.util.Arrays;
import javax.swing.plaf.ColorUIResource;

/**
 * @author  Shannon Hickey
 * @version %I% %G%
 */
class XColors
{

    private static class XColor implements Comparable
    {
        String name;

        int red;
        int green;
        int blue;

        XColor(String name, int red, int green, int blue)
        {
            this.name = name;
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        Color toColor()
        {
            return new ColorUIResource(red, green, blue);
        }

        public int compareTo(Object o)
        {
            XColor other = (XColor)o;

            return name.compareTo(other.name);
        }
    }

    private static XColor key = new XColor("", -1, -1, -1);

    static Color lookupColor(String name)
    {
        key.name = name.toLowerCase();

        int pos = Arrays.binarySearch(colors, key);

        if (pos < 0)
        {
            return null;
        }

        return colors[pos].toColor();
    }

    private static final XColor[] colors =
    {
        new XColor("alice blue", 240, 248, 255),
        new XColor("aliceblue", 240, 248, 255),
        new XColor("antique white", 250, 235, 215),
        new XColor("antiquewhite", 250, 235, 215),
        new XColor("antiquewhite1", 255, 239, 219),
        new XColor("antiquewhite2", 238, 223, 204),
        new XColor("antiquewhite3", 205, 192, 176),
        new XColor("antiquewhite4", 139, 131, 120),
        new XColor("aquamarine", 127, 255, 212),
        new XColor("aquamarine1", 127, 255, 212),
        new XColor("aquamarine2", 118, 238, 198),
        new XColor("aquamarine3", 102, 205, 170),
        new XColor("aquamarine4", 69, 139, 116),
        new XColor("azure", 240, 255, 255),
        new XColor("azure1", 240, 255, 255),
        new XColor("azure2", 224, 238, 238),
        new XColor("azure3", 193, 205, 205),
        new XColor("azure4", 131, 139, 139),
        new XColor("beige", 245, 245, 220),
        new XColor("bisque", 255, 228, 196),
        new XColor("bisque1", 255, 228, 196),
        new XColor("bisque2", 238, 213, 183),
        new XColor("bisque3", 205, 183, 158),
        new XColor("bisque4", 139, 125, 107),
        new XColor("black", 0, 0, 0),
        new XColor("blanched almond", 255, 235, 205),
        new XColor("blanchedalmond", 255, 235, 205),
        new XColor("blue", 0, 0, 255),
        new XColor("blue violet", 138, 43, 226),
        new XColor("blue1", 0, 0, 255),
        new XColor("blue2", 0, 0, 238),
        new XColor("blue3", 0, 0, 205),
        new XColor("blue4", 0, 0, 139),
        new XColor("blueviolet", 138, 43, 226),
        new XColor("brown", 165, 42, 42),
        new XColor("brown1", 255, 64, 64),
        new XColor("brown2", 238, 59, 59),
        new XColor("brown3", 205, 51, 51),
        new XColor("brown4", 139, 35, 35),
        new XColor("burlywood", 222, 184, 135),
        new XColor("burlywood1", 255, 211, 155),
        new XColor("burlywood2", 238, 197, 145),
        new XColor("burlywood3", 205, 170, 125),
        new XColor("burlywood4", 139, 115, 85),
        new XColor("cadet blue", 95, 158, 160),
        new XColor("cadetblue", 95, 158, 160),
        new XColor("cadetblue1", 152, 245, 255),
        new XColor("cadetblue2", 142, 229, 238),
        new XColor("cadetblue3", 122, 197, 205),
        new XColor("cadetblue4", 83, 134, 139),
        new XColor("chartreuse", 127, 255, 0),
        new XColor("chartreuse1", 127, 255, 0),
        new XColor("chartreuse2", 118, 238, 0),
        new XColor("chartreuse3", 102, 205, 0),
        new XColor("chartreuse4", 69, 139, 0),
        new XColor("chocolate", 210, 105, 30),
        new XColor("chocolate1", 255, 127, 36),
        new XColor("chocolate2", 238, 118, 33),
        new XColor("chocolate3", 205, 102, 29),
        new XColor("chocolate4", 139, 69, 19),
        new XColor("coral", 255, 127, 80),
        new XColor("coral1", 255, 114, 86),
        new XColor("coral2", 238, 106, 80),
        new XColor("coral3", 205, 91, 69),
        new XColor("coral4", 139, 62, 47),
        new XColor("cornflower blue", 100, 149, 237),
        new XColor("cornflowerblue", 100, 149, 237),
        new XColor("cornsilk", 255, 248, 220),
        new XColor("cornsilk1", 255, 248, 220),
        new XColor("cornsilk2", 238, 232, 205),
        new XColor("cornsilk3", 205, 200, 177),
        new XColor("cornsilk4", 139, 136, 120),
        new XColor("cyan", 0, 255, 255),
        new XColor("cyan1", 0, 255, 255),
        new XColor("cyan2", 0, 238, 238),
        new XColor("cyan3", 0, 205, 205),
        new XColor("cyan4", 0, 139, 139),
        new XColor("dark blue", 0, 0, 139),
        new XColor("dark cyan", 0, 139, 139),
        new XColor("dark goldenrod", 184, 134, 11),
        new XColor("dark gray", 169, 169, 169),
        new XColor("dark green", 0, 100, 0),
        new XColor("dark grey", 169, 169, 169),
        new XColor("dark khaki", 189, 183, 107),
        new XColor("dark magenta", 139, 0, 139),
        new XColor("dark olive green", 85, 107, 47),
        new XColor("dark orange", 255, 140, 0),
        new XColor("dark orchid", 153, 50, 204),
        new XColor("dark red", 139, 0, 0),
        new XColor("dark salmon", 233, 150, 122),
        new XColor("dark sea green", 143, 188, 143),
        new XColor("dark slate blue", 72, 61, 139),
        new XColor("dark slate gray", 47, 79, 79),
        new XColor("dark slate grey", 47, 79, 79),
        new XColor("dark turquoise", 0, 206, 209),
        new XColor("dark violet", 148, 0, 211),
        new XColor("darkblue", 0, 0, 139),
        new XColor("darkcyan", 0, 139, 139),
        new XColor("darkgoldenrod", 184, 134, 11),
        new XColor("darkgoldenrod1", 255, 185, 15),
        new XColor("darkgoldenrod2", 238, 173, 14),
        new XColor("darkgoldenrod3", 205, 149, 12),
        new XColor("darkgoldenrod4", 139, 101, 8),
        new XColor("darkgray", 169, 169, 169),
        new XColor("darkgreen", 0, 100, 0),
        new XColor("darkgrey", 169, 169, 169),
        new XColor("darkkhaki", 189, 183, 107),
        new XColor("darkmagenta", 139, 0, 139),
        new XColor("darkolivegreen", 85, 107, 47),
        new XColor("darkolivegreen1", 202, 255, 112),
        new XColor("darkolivegreen2", 188, 238, 104),
        new XColor("darkolivegreen3", 162, 205, 90),
        new XColor("darkolivegreen4", 110, 139, 61),
        new XColor("darkorange", 255, 140, 0),
        new XColor("darkorange1", 255, 127, 0),
        new XColor("darkorange2", 238, 118, 0),
        new XColor("darkorange3", 205, 102, 0),
        new XColor("darkorange4", 139, 69, 0),
        new XColor("darkorchid", 153, 50, 204),
        new XColor("darkorchid1", 191, 62, 255),
        new XColor("darkorchid2", 178, 58, 238),
        new XColor("darkorchid3", 154, 50, 205),
        new XColor("darkorchid4", 104, 34, 139),
        new XColor("darkred", 139, 0, 0),
        new XColor("darksalmon", 233, 150, 122),
        new XColor("darkseagreen", 143, 188, 143),
        new XColor("darkseagreen1", 193, 255, 193),
        new XColor("darkseagreen2", 180, 238, 180),
        new XColor("darkseagreen3", 155, 205, 155),
        new XColor("darkseagreen4", 105, 139, 105),
        new XColor("darkslateblue", 72, 61, 139),
        new XColor("darkslategray", 47, 79, 79),
        new XColor("darkslategray1", 151, 255, 255),
        new XColor("darkslategray2", 141, 238, 238),
        new XColor("darkslategray3", 121, 205, 205),
        new XColor("darkslategray4", 82, 139, 139),
        new XColor("darkslategrey", 47, 79, 79),
        new XColor("darkturquoise", 0, 206, 209),
        new XColor("darkviolet", 148, 0, 211),
        new XColor("deep pink", 255, 20, 147),
        new XColor("deep sky blue", 0, 191, 255),
        new XColor("deeppink", 255, 20, 147),
        new XColor("deeppink1", 255, 20, 147),
        new XColor("deeppink2", 238, 18, 137),
        new XColor("deeppink3", 205, 16, 118),
        new XColor("deeppink4", 139, 10, 80),
        new XColor("deepskyblue", 0, 191, 255),
        new XColor("deepskyblue1", 0, 191, 255),
        new XColor("deepskyblue2", 0, 178, 238),
        new XColor("deepskyblue3", 0, 154, 205),
        new XColor("deepskyblue4", 0, 104, 139),
        new XColor("dim gray", 105, 105, 105),
        new XColor("dim grey", 105, 105, 105),
        new XColor("dimgray", 105, 105, 105),
        new XColor("dimgrey", 105, 105, 105),
        new XColor("dodger blue", 30, 144, 255),
        new XColor("dodgerblue", 30, 144, 255),
        new XColor("dodgerblue1", 30, 144, 255),
        new XColor("dodgerblue2", 28, 134, 238),
        new XColor("dodgerblue3", 24, 116, 205),
        new XColor("dodgerblue4", 16, 78, 139),
        new XColor("firebrick", 178, 34, 34),
        new XColor("firebrick1", 255, 48, 48),
        new XColor("firebrick2", 238, 44, 44),
        new XColor("firebrick3", 205, 38, 38),
        new XColor("firebrick4", 139, 26, 26),
        new XColor("floral white", 255, 250, 240),
        new XColor("floralwhite", 255, 250, 240),
        new XColor("forest green", 34, 139, 34),
        new XColor("forestgreen", 34, 139, 34),
        new XColor("gainsboro", 220, 220, 220),
        new XColor("ghost white", 248, 248, 255),
        new XColor("ghostwhite", 248, 248, 255),
        new XColor("gold", 255, 215, 0),
        new XColor("gold1", 255, 215, 0),
        new XColor("gold2", 238, 201, 0),
        new XColor("gold3", 205, 173, 0),
        new XColor("gold4", 139, 117, 0),
        new XColor("goldenrod", 218, 165, 32),
        new XColor("goldenrod1", 255, 193, 37),
        new XColor("goldenrod2", 238, 180, 34),
        new XColor("goldenrod3", 205, 155, 29),
        new XColor("goldenrod4", 139, 105, 20),
        new XColor("gray", 190, 190, 190),
        new XColor("gray0", 0, 0, 0),
        new XColor("gray1", 3, 3, 3),
        new XColor("gray10", 26, 26, 26),
        new XColor("gray100", 255, 255, 255),
        new XColor("gray11", 28, 28, 28),
        new XColor("gray12", 31, 31, 31),
        new XColor("gray13", 33, 33, 33),
        new XColor("gray14", 36, 36, 36),
        new XColor("gray15", 38, 38, 38),
        new XColor("gray16", 41, 41, 41),
        new XColor("gray17", 43, 43, 43),
        new XColor("gray18", 46, 46, 46),
        new XColor("gray19", 48, 48, 48),
        new XColor("gray2", 5, 5, 5),
        new XColor("gray20", 51, 51, 51),
        new XColor("gray21", 54, 54, 54),
        new XColor("gray22", 56, 56, 56),
        new XColor("gray23", 59, 59, 59),
        new XColor("gray24", 61, 61, 61),
        new XColor("gray25", 64, 64, 64),
        new XColor("gray26", 66, 66, 66),
        new XColor("gray27", 69, 69, 69),
        new XColor("gray28", 71, 71, 71),
        new XColor("gray29", 74, 74, 74),
        new XColor("gray3", 8, 8, 8),
        new XColor("gray30", 77, 77, 77),
        new XColor("gray31", 79, 79, 79),
        new XColor("gray32", 82, 82, 82),
        new XColor("gray33", 84, 84, 84),
        new XColor("gray34", 87, 87, 87),
        new XColor("gray35", 89, 89, 89),
        new XColor("gray36", 92, 92, 92),
        new XColor("gray37", 94, 94, 94),
        new XColor("gray38", 97, 97, 97),
        new XColor("gray39", 99, 99, 99),
        new XColor("gray4", 10, 10, 10),
        new XColor("gray40", 102, 102, 102),
        new XColor("gray41", 105, 105, 105),
        new XColor("gray42", 107, 107, 107),
        new XColor("gray43", 110, 110, 110),
        new XColor("gray44", 112, 112, 112),
        new XColor("gray45", 115, 115, 115),
        new XColor("gray46", 117, 117, 117),
        new XColor("gray47", 120, 120, 120),
        new XColor("gray48", 122, 122, 122),
        new XColor("gray49", 125, 125, 125),
        new XColor("gray5", 13, 13, 13),
        new XColor("gray50", 127, 127, 127),
        new XColor("gray51", 130, 130, 130),
        new XColor("gray52", 133, 133, 133),
        new XColor("gray53", 135, 135, 135),
        new XColor("gray54", 138, 138, 138),
        new XColor("gray55", 140, 140, 140),
        new XColor("gray56", 143, 143, 143),
        new XColor("gray57", 145, 145, 145),
        new XColor("gray58", 148, 148, 148),
        new XColor("gray59", 150, 150, 150),
        new XColor("gray6", 15, 15, 15),
        new XColor("gray60", 153, 153, 153),
        new XColor("gray61", 156, 156, 156),
        new XColor("gray62", 158, 158, 158),
        new XColor("gray63", 161, 161, 161),
        new XColor("gray64", 163, 163, 163),
        new XColor("gray65", 166, 166, 166),
        new XColor("gray66", 168, 168, 168),
        new XColor("gray67", 171, 171, 171),
        new XColor("gray68", 173, 173, 173),
        new XColor("gray69", 176, 176, 176),
        new XColor("gray7", 18, 18, 18),
        new XColor("gray70", 179, 179, 179),
        new XColor("gray71", 181, 181, 181),
        new XColor("gray72", 184, 184, 184),
        new XColor("gray73", 186, 186, 186),
        new XColor("gray74", 189, 189, 189),
        new XColor("gray75", 191, 191, 191),
        new XColor("gray76", 194, 194, 194),
        new XColor("gray77", 196, 196, 196),
        new XColor("gray78", 199, 199, 199),
        new XColor("gray79", 201, 201, 201),
        new XColor("gray8", 20, 20, 20),
        new XColor("gray80", 204, 204, 204),
        new XColor("gray81", 207, 207, 207),
        new XColor("gray82", 209, 209, 209),
        new XColor("gray83", 212, 212, 212),
        new XColor("gray84", 214, 214, 214),
        new XColor("gray85", 217, 217, 217),
        new XColor("gray86", 219, 219, 219),
        new XColor("gray87", 222, 222, 222),
        new XColor("gray88", 224, 224, 224),
        new XColor("gray89", 227, 227, 227),
        new XColor("gray9", 23, 23, 23),
        new XColor("gray90", 229, 229, 229),
        new XColor("gray91", 232, 232, 232),
        new XColor("gray92", 235, 235, 235),
        new XColor("gray93", 237, 237, 237),
        new XColor("gray94", 240, 240, 240),
        new XColor("gray95", 242, 242, 242),
        new XColor("gray96", 245, 245, 245),
        new XColor("gray97", 247, 247, 247),
        new XColor("gray98", 250, 250, 250),
        new XColor("gray99", 252, 252, 252),
        new XColor("green", 0, 255, 0),
        new XColor("green yellow", 173, 255, 47),
        new XColor("green1", 0, 255, 0),
        new XColor("green2", 0, 238, 0),
        new XColor("green3", 0, 205, 0),
        new XColor("green4", 0, 139, 0),
        new XColor("greenyellow", 173, 255, 47),
        new XColor("grey", 190, 190, 190),
        new XColor("grey0", 0, 0, 0),
        new XColor("grey1", 3, 3, 3),
        new XColor("grey10", 26, 26, 26),
        new XColor("grey100", 255, 255, 255),
        new XColor("grey11", 28, 28, 28),
        new XColor("grey12", 31, 31, 31),
        new XColor("grey13", 33, 33, 33),
        new XColor("grey14", 36, 36, 36),
        new XColor("grey15", 38, 38, 38),
        new XColor("grey16", 41, 41, 41),
        new XColor("grey17", 43, 43, 43),
        new XColor("grey18", 46, 46, 46),
        new XColor("grey19", 48, 48, 48),
        new XColor("grey2", 5, 5, 5),
        new XColor("grey20", 51, 51, 51),
        new XColor("grey21", 54, 54, 54),
        new XColor("grey22", 56, 56, 56),
        new XColor("grey23", 59, 59, 59),
        new XColor("grey24", 61, 61, 61),
        new XColor("grey25", 64, 64, 64),
        new XColor("grey26", 66, 66, 66),
        new XColor("grey27", 69, 69, 69),
        new XColor("grey28", 71, 71, 71),
        new XColor("grey29", 74, 74, 74),
        new XColor("grey3", 8, 8, 8),
        new XColor("grey30", 77, 77, 77),
        new XColor("grey31", 79, 79, 79),
        new XColor("grey32", 82, 82, 82),
        new XColor("grey33", 84, 84, 84),
        new XColor("grey34", 87, 87, 87),
        new XColor("grey35", 89, 89, 89),
        new XColor("grey36", 92, 92, 92),
        new XColor("grey37", 94, 94, 94),
        new XColor("grey38", 97, 97, 97),
        new XColor("grey39", 99, 99, 99),
        new XColor("grey4", 10, 10, 10),
        new XColor("grey40", 102, 102, 102),
        new XColor("grey41", 105, 105, 105),
        new XColor("grey42", 107, 107, 107),
        new XColor("grey43", 110, 110, 110),
        new XColor("grey44", 112, 112, 112),
        new XColor("grey45", 115, 115, 115),
        new XColor("grey46", 117, 117, 117),
        new XColor("grey47", 120, 120, 120),
        new XColor("grey48", 122, 122, 122),
        new XColor("grey49", 125, 125, 125),
        new XColor("grey5", 13, 13, 13),
        new XColor("grey50", 127, 127, 127),
        new XColor("grey51", 130, 130, 130),
        new XColor("grey52", 133, 133, 133),
        new XColor("grey53", 135, 135, 135),
        new XColor("grey54", 138, 138, 138),
        new XColor("grey55", 140, 140, 140),
        new XColor("grey56", 143, 143, 143),
        new XColor("grey57", 145, 145, 145),
        new XColor("grey58", 148, 148, 148),
        new XColor("grey59", 150, 150, 150),
        new XColor("grey6", 15, 15, 15),
        new XColor("grey60", 153, 153, 153),
        new XColor("grey61", 156, 156, 156),
        new XColor("grey62", 158, 158, 158),
        new XColor("grey63", 161, 161, 161),
        new XColor("grey64", 163, 163, 163),
        new XColor("grey65", 166, 166, 166),
        new XColor("grey66", 168, 168, 168),
        new XColor("grey67", 171, 171, 171),
        new XColor("grey68", 173, 173, 173),
        new XColor("grey69", 176, 176, 176),
        new XColor("grey7", 18, 18, 18),
        new XColor("grey70", 179, 179, 179),
        new XColor("grey71", 181, 181, 181),
        new XColor("grey72", 184, 184, 184),
        new XColor("grey73", 186, 186, 186),
        new XColor("grey74", 189, 189, 189),
        new XColor("grey75", 191, 191, 191),
        new XColor("grey76", 194, 194, 194),
        new XColor("grey77", 196, 196, 196),
        new XColor("grey78", 199, 199, 199),
        new XColor("grey79", 201, 201, 201),
        new XColor("grey8", 20, 20, 20),
        new XColor("grey80", 204, 204, 204),
        new XColor("grey81", 207, 207, 207),
        new XColor("grey82", 209, 209, 209),
        new XColor("grey83", 212, 212, 212),
        new XColor("grey84", 214, 214, 214),
        new XColor("grey85", 217, 217, 217),
        new XColor("grey86", 219, 219, 219),
        new XColor("grey87", 222, 222, 222),
        new XColor("grey88", 224, 224, 224),
        new XColor("grey89", 227, 227, 227),
        new XColor("grey9", 23, 23, 23),
        new XColor("grey90", 229, 229, 229),
        new XColor("grey91", 232, 232, 232),
        new XColor("grey92", 235, 235, 235),
        new XColor("grey93", 237, 237, 237),
        new XColor("grey94", 240, 240, 240),
        new XColor("grey95", 242, 242, 242),
        new XColor("grey96", 245, 245, 245),
        new XColor("grey97", 247, 247, 247),
        new XColor("grey98", 250, 250, 250),
        new XColor("grey99", 252, 252, 252),
        new XColor("honeydew", 240, 255, 240),
        new XColor("honeydew1", 240, 255, 240),
        new XColor("honeydew2", 224, 238, 224),
        new XColor("honeydew3", 193, 205, 193),
        new XColor("honeydew4", 131, 139, 131),
        new XColor("hot pink", 255, 105, 180),
        new XColor("hotpink", 255, 105, 180),
        new XColor("hotpink1", 255, 110, 180),
        new XColor("hotpink2", 238, 106, 167),
        new XColor("hotpink3", 205, 96, 144),
        new XColor("hotpink4", 139, 58, 98),
        new XColor("indian red", 205, 92, 92),
        new XColor("indianred", 205, 92, 92),
        new XColor("indianred1", 255, 106, 106),
        new XColor("indianred2", 238, 99, 99),
        new XColor("indianred3", 205, 85, 85),
        new XColor("indianred4", 139, 58, 58),
        new XColor("ivory", 255, 255, 240),
        new XColor("ivory1", 255, 255, 240),
        new XColor("ivory2", 238, 238, 224),
        new XColor("ivory3", 205, 205, 193),
        new XColor("ivory4", 139, 139, 131),
        new XColor("khaki", 240, 230, 140),
        new XColor("khaki1", 255, 246, 143),
        new XColor("khaki2", 238, 230, 133),
        new XColor("khaki3", 205, 198, 115),
        new XColor("khaki4", 139, 134, 78),
        new XColor("lavender", 230, 230, 250),
        new XColor("lavender blush", 255, 240, 245),
        new XColor("lavenderblush", 255, 240, 245),
        new XColor("lavenderblush1", 255, 240, 245),
        new XColor("lavenderblush2", 238, 224, 229),
        new XColor("lavenderblush3", 205, 193, 197),
        new XColor("lavenderblush4", 139, 131, 134),
        new XColor("lawn green", 124, 252, 0),
        new XColor("lawngreen", 124, 252, 0),
        new XColor("lemon chiffon", 255, 250, 205),
        new XColor("lemonchiffon", 255, 250, 205),
        new XColor("lemonchiffon1", 255, 250, 205),
        new XColor("lemonchiffon2", 238, 233, 191),
        new XColor("lemonchiffon3", 205, 201, 165),
        new XColor("lemonchiffon4", 139, 137, 112),
        new XColor("light blue", 173, 216, 230),
        new XColor("light coral", 240, 128, 128),
        new XColor("light cyan", 224, 255, 255),
        new XColor("light goldenrod", 238, 221, 130),
        new XColor("light goldenrod yellow", 250, 250, 210),
        new XColor("light gray", 211, 211, 211),
        new XColor("light green", 144, 238, 144),
        new XColor("light grey", 211, 211, 211),
        new XColor("light pink", 255, 182, 193),
        new XColor("light salmon", 255, 160, 122),
        new XColor("light sea green", 32, 178, 170),
        new XColor("light sky blue", 135, 206, 250),
        new XColor("light slate blue", 132, 112, 255),
        new XColor("light slate gray", 119, 136, 153),
        new XColor("light slate grey", 119, 136, 153),
        new XColor("light steel blue", 176, 196, 222),
        new XColor("light yellow", 255, 255, 224),
        new XColor("lightblue", 173, 216, 230),
        new XColor("lightblue1", 191, 239, 255),
        new XColor("lightblue2", 178, 223, 238),
        new XColor("lightblue3", 154, 192, 205),
        new XColor("lightblue4", 104, 131, 139),
        new XColor("lightcoral", 240, 128, 128),
        new XColor("lightcyan", 224, 255, 255),
        new XColor("lightcyan1", 224, 255, 255),
        new XColor("lightcyan2", 209, 238, 238),
        new XColor("lightcyan3", 180, 205, 205),
        new XColor("lightcyan4", 122, 139, 139),
        new XColor("lightgoldenrod", 238, 221, 130),
        new XColor("lightgoldenrod1", 255, 236, 139),
        new XColor("lightgoldenrod2", 238, 220, 130),
        new XColor("lightgoldenrod3", 205, 190, 112),
        new XColor("lightgoldenrod4", 139, 129, 76),
        new XColor("lightgoldenrodyellow", 250, 250, 210),
        new XColor("lightgray", 211, 211, 211),
        new XColor("lightgreen", 144, 238, 144),
        new XColor("lightgrey", 211, 211, 211),
        new XColor("lightpink", 255, 182, 193),
        new XColor("lightpink1", 255, 174, 185),
        new XColor("lightpink2", 238, 162, 173),
        new XColor("lightpink3", 205, 140, 149),
        new XColor("lightpink4", 139, 95, 101),
        new XColor("lightsalmon", 255, 160, 122),
        new XColor("lightsalmon1", 255, 160, 122),
        new XColor("lightsalmon2", 238, 149, 114),
        new XColor("lightsalmon3", 205, 129, 98),
        new XColor("lightsalmon4", 139, 87, 66),
        new XColor("lightseagreen", 32, 178, 170),
        new XColor("lightskyblue", 135, 206, 250),
        new XColor("lightskyblue1", 176, 226, 255),
        new XColor("lightskyblue2", 164, 211, 238),
        new XColor("lightskyblue3", 141, 182, 205),
        new XColor("lightskyblue4", 96, 123, 139),
        new XColor("lightslateblue", 132, 112, 255),
        new XColor("lightslategray", 119, 136, 153),
        new XColor("lightslategrey", 119, 136, 153),
        new XColor("lightsteelblue", 176, 196, 222),
        new XColor("lightsteelblue1", 202, 225, 255),
        new XColor("lightsteelblue2", 188, 210, 238),
        new XColor("lightsteelblue3", 162, 181, 205),
        new XColor("lightsteelblue4", 110, 123, 139),
        new XColor("lightyellow", 255, 255, 224),
        new XColor("lightyellow1", 255, 255, 224),
        new XColor("lightyellow2", 238, 238, 209),
        new XColor("lightyellow3", 205, 205, 180),
        new XColor("lightyellow4", 139, 139, 122),
        new XColor("lime green", 50, 205, 50),
        new XColor("limegreen", 50, 205, 50),
        new XColor("linen", 250, 240, 230),
        new XColor("magenta", 255, 0, 255),
        new XColor("magenta1", 255, 0, 255),
        new XColor("magenta2", 238, 0, 238),
        new XColor("magenta3", 205, 0, 205),
        new XColor("magenta4", 139, 0, 139),
        new XColor("maroon", 176, 48, 96),
        new XColor("maroon1", 255, 52, 179),
        new XColor("maroon2", 238, 48, 167),
        new XColor("maroon3", 205, 41, 144),
        new XColor("maroon4", 139, 28, 98),
        new XColor("medium aquamarine", 102, 205, 170),
        new XColor("medium blue", 0, 0, 205),
        new XColor("medium orchid", 186, 85, 211),
        new XColor("medium purple", 147, 112, 219),
        new XColor("medium sea green", 60, 179, 113),
        new XColor("medium slate blue", 123, 104, 238),
        new XColor("medium spring green", 0, 250, 154),
        new XColor("medium turquoise", 72, 209, 204),
        new XColor("medium violet red", 199, 21, 133),
        new XColor("mediumaquamarine", 102, 205, 170),
        new XColor("mediumblue", 0, 0, 205),
        new XColor("mediumorchid", 186, 85, 211),
        new XColor("mediumorchid1", 224, 102, 255),
        new XColor("mediumorchid2", 209, 95, 238),
        new XColor("mediumorchid3", 180, 82, 205),
        new XColor("mediumorchid4", 122, 55, 139),
        new XColor("mediumpurple", 147, 112, 219),
        new XColor("mediumpurple1", 171, 130, 255),
        new XColor("mediumpurple2", 159, 121, 238),
        new XColor("mediumpurple3", 137, 104, 205),
        new XColor("mediumpurple4", 93, 71, 139),
        new XColor("mediumseagreen", 60, 179, 113),
        new XColor("mediumslateblue", 123, 104, 238),
        new XColor("mediumspringgreen", 0, 250, 154),
        new XColor("mediumturquoise", 72, 209, 204),
        new XColor("mediumvioletred", 199, 21, 133),
        new XColor("midnight blue", 25, 25, 112),
        new XColor("midnightblue", 25, 25, 112),
        new XColor("mint cream", 245, 255, 250),
        new XColor("mintcream", 245, 255, 250),
        new XColor("misty rose", 255, 228, 225),
        new XColor("mistyrose", 255, 228, 225),
        new XColor("mistyrose1", 255, 228, 225),
        new XColor("mistyrose2", 238, 213, 210),
        new XColor("mistyrose3", 205, 183, 181),
        new XColor("mistyrose4", 139, 125, 123),
        new XColor("moccasin", 255, 228, 181),
        new XColor("navajo white", 255, 222, 173),
        new XColor("navajowhite", 255, 222, 173),
        new XColor("navajowhite1", 255, 222, 173),
        new XColor("navajowhite2", 238, 207, 161),
        new XColor("navajowhite3", 205, 179, 139),
        new XColor("navajowhite4", 139, 121, 94),
        new XColor("navy", 0, 0, 128),
        new XColor("navy blue", 0, 0, 128),
        new XColor("navyblue", 0, 0, 128),
        new XColor("old lace", 253, 245, 230),
        new XColor("oldlace", 253, 245, 230),
        new XColor("olive drab", 107, 142, 35),
        new XColor("olivedrab", 107, 142, 35),
        new XColor("olivedrab1", 192, 255, 62),
        new XColor("olivedrab2", 179, 238, 58),
        new XColor("olivedrab3", 154, 205, 50),
        new XColor("olivedrab4", 105, 139, 34),
        new XColor("orange", 255, 165, 0),
        new XColor("orange red", 255, 69, 0),
        new XColor("orange1", 255, 165, 0),
        new XColor("orange2", 238, 154, 0),
        new XColor("orange3", 205, 133, 0),
        new XColor("orange4", 139, 90, 0),
        new XColor("orangered", 255, 69, 0),
        new XColor("orangered1", 255, 69, 0),
        new XColor("orangered2", 238, 64, 0),
        new XColor("orangered3", 205, 55, 0),
        new XColor("orangered4", 139, 37, 0),
        new XColor("orchid", 218, 112, 214),
        new XColor("orchid1", 255, 131, 250),
        new XColor("orchid2", 238, 122, 233),
        new XColor("orchid3", 205, 105, 201),
        new XColor("orchid4", 139, 71, 137),
        new XColor("pale goldenrod", 238, 232, 170),
        new XColor("pale green", 152, 251, 152),
        new XColor("pale turquoise", 175, 238, 238),
        new XColor("pale violet red", 219, 112, 147),
        new XColor("palegoldenrod", 238, 232, 170),
        new XColor("palegreen", 152, 251, 152),
        new XColor("palegreen1", 154, 255, 154),
        new XColor("palegreen2", 144, 238, 144),
        new XColor("palegreen3", 124, 205, 124),
        new XColor("palegreen4", 84, 139, 84),
        new XColor("paleturquoise", 175, 238, 238),
        new XColor("paleturquoise1", 187, 255, 255),
        new XColor("paleturquoise2", 174, 238, 238),
        new XColor("paleturquoise3", 150, 205, 205),
        new XColor("paleturquoise4", 102, 139, 139),
        new XColor("palevioletred", 219, 112, 147),
        new XColor("palevioletred1", 255, 130, 171),
        new XColor("palevioletred2", 238, 121, 159),
        new XColor("palevioletred3", 205, 104, 137),
        new XColor("palevioletred4", 139, 71, 93),
        new XColor("papaya whip", 255, 239, 213),
        new XColor("papayawhip", 255, 239, 213),
        new XColor("peach puff", 255, 218, 185),
        new XColor("peachpuff", 255, 218, 185),
        new XColor("peachpuff1", 255, 218, 185),
        new XColor("peachpuff2", 238, 203, 173),
        new XColor("peachpuff3", 205, 175, 149),
        new XColor("peachpuff4", 139, 119, 101),
        new XColor("peru", 205, 133, 63),
        new XColor("pink", 255, 192, 203),
        new XColor("pink1", 255, 181, 197),
        new XColor("pink2", 238, 169, 184),
        new XColor("pink3", 205, 145, 158),
        new XColor("pink4", 139, 99, 108),
        new XColor("plum", 221, 160, 221),
        new XColor("plum1", 255, 187, 255),
        new XColor("plum2", 238, 174, 238),
        new XColor("plum3", 205, 150, 205),
        new XColor("plum4", 139, 102, 139),
        new XColor("powder blue", 176, 224, 230),
        new XColor("powderblue", 176, 224, 230),
        new XColor("purple", 160, 32, 240),
        new XColor("purple1", 155, 48, 255),
        new XColor("purple2", 145, 44, 238),
        new XColor("purple3", 125, 38, 205),
        new XColor("purple4", 85, 26, 139),
        new XColor("red", 255, 0, 0),
        new XColor("red1", 255, 0, 0),
        new XColor("red2", 238, 0, 0),
        new XColor("red3", 205, 0, 0),
        new XColor("red4", 139, 0, 0),
        new XColor("rosy brown", 188, 143, 143),
        new XColor("rosybrown", 188, 143, 143),
        new XColor("rosybrown1", 255, 193, 193),
        new XColor("rosybrown2", 238, 180, 180),
        new XColor("rosybrown3", 205, 155, 155),
        new XColor("rosybrown4", 139, 105, 105),
        new XColor("royal blue", 65, 105, 225),
        new XColor("royalblue", 65, 105, 225),
        new XColor("royalblue1", 72, 118, 255),
        new XColor("royalblue2", 67, 110, 238),
        new XColor("royalblue3", 58, 95, 205),
        new XColor("royalblue4", 39, 64, 139),
        new XColor("saddle brown", 139, 69, 19),
        new XColor("saddlebrown", 139, 69, 19),
        new XColor("salmon", 250, 128, 114),
        new XColor("salmon1", 255, 140, 105),
        new XColor("salmon2", 238, 130, 98),
        new XColor("salmon3", 205, 112, 84),
        new XColor("salmon4", 139, 76, 57),
        new XColor("sandy brown", 244, 164, 96),
        new XColor("sandybrown", 244, 164, 96),
        new XColor("sea green", 46, 139, 87),
        new XColor("seagreen", 46, 139, 87),
        new XColor("seagreen1", 84, 255, 159),
        new XColor("seagreen2", 78, 238, 148),
        new XColor("seagreen3", 67, 205, 128),
        new XColor("seagreen4", 46, 139, 87),
        new XColor("seashell", 255, 245, 238),
        new XColor("seashell1", 255, 245, 238),
        new XColor("seashell2", 238, 229, 222),
        new XColor("seashell3", 205, 197, 191),
        new XColor("seashell4", 139, 134, 130),
        new XColor("sienna", 160, 82, 45),
        new XColor("sienna1", 255, 130, 71),
        new XColor("sienna2", 238, 121, 66),
        new XColor("sienna3", 205, 104, 57),
        new XColor("sienna4", 139, 71, 38),
        new XColor("sky blue", 135, 206, 235),
        new XColor("skyblue", 135, 206, 235),
        new XColor("skyblue1", 135, 206, 255),
        new XColor("skyblue2", 126, 192, 238),
        new XColor("skyblue3", 108, 166, 205),
        new XColor("skyblue4", 74, 112, 139),
        new XColor("slate blue", 106, 90, 205),
        new XColor("slate gray", 112, 128, 144),
        new XColor("slate grey", 112, 128, 144),
        new XColor("slateblue", 106, 90, 205),
        new XColor("slateblue1", 131, 111, 255),
        new XColor("slateblue2", 122, 103, 238),
        new XColor("slateblue3", 105, 89, 205),
        new XColor("slateblue4", 71, 60, 139),
        new XColor("slategray", 112, 128, 144),
        new XColor("slategray1", 198, 226, 255),
        new XColor("slategray2", 185, 211, 238),
        new XColor("slategray3", 159, 182, 205),
        new XColor("slategray4", 108, 123, 139),
        new XColor("slategrey", 112, 128, 144),
        new XColor("snow", 255, 250, 250),
        new XColor("snow1", 255, 250, 250),
        new XColor("snow2", 238, 233, 233),
        new XColor("snow3", 205, 201, 201),
        new XColor("snow4", 139, 137, 137),
        new XColor("spring green", 0, 255, 127),
        new XColor("springgreen", 0, 255, 127),
        new XColor("springgreen1", 0, 255, 127),
        new XColor("springgreen2", 0, 238, 118),
        new XColor("springgreen3", 0, 205, 102),
        new XColor("springgreen4", 0, 139, 69),
        new XColor("steel blue", 70, 130, 180),
        new XColor("steelblue", 70, 130, 180),
        new XColor("steelblue1", 99, 184, 255),
        new XColor("steelblue2", 92, 172, 238),
        new XColor("steelblue3", 79, 148, 205),
        new XColor("steelblue4", 54, 100, 139),
        new XColor("tan", 210, 180, 140),
        new XColor("tan1", 255, 165, 79),
        new XColor("tan2", 238, 154, 73),
        new XColor("tan3", 205, 133, 63),
        new XColor("tan4", 139, 90, 43),
        new XColor("thistle", 216, 191, 216),
        new XColor("thistle1", 255, 225, 255),
        new XColor("thistle2", 238, 210, 238),
        new XColor("thistle3", 205, 181, 205),
        new XColor("thistle4", 139, 123, 139),
        new XColor("tomato", 255, 99, 71),
        new XColor("tomato1", 255, 99, 71),
        new XColor("tomato2", 238, 92, 66),
        new XColor("tomato3", 205, 79, 57),
        new XColor("tomato4", 139, 54, 38),
        new XColor("turquoise", 64, 224, 208),
        new XColor("turquoise1", 0, 245, 255),
        new XColor("turquoise2", 0, 229, 238),
        new XColor("turquoise3", 0, 197, 205),
        new XColor("turquoise4", 0, 134, 139),
        new XColor("violet", 238, 130, 238),
        new XColor("violet red", 208, 32, 144),
        new XColor("violetred", 208, 32, 144),
        new XColor("violetred1", 255, 62, 150),
        new XColor("violetred2", 238, 58, 140),
        new XColor("violetred3", 205, 50, 120),
        new XColor("violetred4", 139, 34, 82),
        new XColor("wheat", 245, 222, 179),
        new XColor("wheat1", 255, 231, 186),
        new XColor("wheat2", 238, 216, 174),
        new XColor("wheat3", 205, 186, 150),
        new XColor("wheat4", 139, 126, 102),
        new XColor("white", 255, 255, 255),
        new XColor("white smoke", 245, 245, 245),
        new XColor("whitesmoke", 245, 245, 245),
        new XColor("yellow", 255, 255, 0),
        new XColor("yellow green", 154, 205, 50),
        new XColor("yellow1", 255, 255, 0),
        new XColor("yellow2", 238, 238, 0),
        new XColor("yellow3", 205, 205, 0),
        new XColor("yellow4", 139, 139, 0),
        new XColor("yellowgreen", 154, 205, 5)
    };

}
