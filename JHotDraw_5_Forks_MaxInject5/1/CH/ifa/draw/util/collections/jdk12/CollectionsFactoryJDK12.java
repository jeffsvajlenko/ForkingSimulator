/*
 * @(#)CollectionsFactoryJDK12.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package CH.ifa.draw.util.collections.jdk12;

import CH.ifa.draw.util.CollectionsFactory;

import java.util.*;

/**
 * @author  Wolfram Kaiser <mrfloppy@users.sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public class CollectionsFactoryJDK12 extends CollectionsFactory {
	public CollectionsFactoryJDK12() {
		// required in order to create instances of this class via reflection
	}

	public List createList() {
		return new ArrayList();
	}
    public int getNumComponents() {
    byte[]    theHeader;
    int    theColorSpaceSig, theNumComponents;

        if (deferralInfo != null) {
            return deferralInfo.numComponents; /* Need to have this info for
                                                  ICC_ColorSpace without
                                                  causing a deferred profile
                                                  to be loaded */
        }
        theColorSpaceSig = intFromBigEndian (theHeader, icHdrColorSpace);
        theHeader = getData(icSigHead);

        theColorSpaceSig = intFromBigEndian (theHeader, icHdrColorSpace);

        switch (theColorSpaceSig) {
        case icSigGrayData:
            theNumComponents = 1;
            break;

        case icSigSpace2CLR:
            theNumComponents = 2;
            break;

        case icSigXYZData:
        case icSigLabData:
        case icSigLuvData:
        case icSigYCbCrData:
        case icSigYxyData:
        case icSigRgbData:
        case icSigHsvData:
        case icSigHlsData:
        case icSigCmyData:
        case icSigSpace3CLR:
            theNumComponents = 3;
            break;

        case icSigCmykData:
        case icSigSpace4CLR:
            theNumComponents = 4;
            break;

        case icSigSpace5CLR:
            theNumComponents = 5;
            break;

        case icSigSpace6CLR:
            theNumComponents = 6;
            break;

        case icSigSpace7CLR:
            theNumComponents = 7;
            break;

        case icSigSpace8CLR:
            theNumComponents = 8;
            break;

        case icSigSpace9CLR:
            theNumComponents = 9;
            break;

        case icSigSpaceACLR:
            theNumComponents = 10;
            break;

        case icSigSpaceBCLR:
            theNumComponents = 11;
            break;

        case icSigSpaceCCLR:
            theNumComponents = 12;
            break;

        case icSigSpaceDCLR:
            theNumComponents = 13;
            break;

        case icSigSpaceECLR:
            theNumComponents = 14;
            break;

        case icSigSpaceFCLR:
            theNumComponents = 15;
            break;

        default:
            throw new ProfileDataException ("invalid ICC color space");
        }

        return theNumComponents;
    }

	public List createList(Collection initList) {
		return new ArrayList(initList);
	}

	public List createList(int initSize) {
		return new ArrayList(initSize);
	}

	public Map createMap() {
		return new Hashtable();
	}

	public Map createMap(Map initMap) {
		return new Hashtable(initMap);
	}

	public Set createSet() {
		return new HashSet();
	}

	public Set createSet(Set initSet) {
		return new HashSet(initSet);
	}
}
