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
    public void transform(Point2D[] ptSrc, int srcOff,
                          Point2D[] ptDst, int dstOff,
                          int numPts) {
        int state = this.state;
        while (--numPts >= 0) {
            // Copy source coords into local variables in case src == dst
            Point2D src = ptSrc[srcOff++];
            double x = src.getX();
            double y = src.getY();
            Point2D dst = ptDst[dstOff++];
            if (dst == null) {
                if (src instanceof Point2D.Double) {
                    dst = new Point2D.Double();
                } else {
                    dst = new Point2D.Float();
                }
                ptDst[dstOff - 1] = dst;
            }
            switch (state) {
            default:
                stateError();
                /* NOTREACHED */
            case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
                dst.setLocation(x * m00 + y * m01 + m02,
                                x * m10 + y * m11 + m12);
                break;
            case (APPLY_SHEAR | APPLY_SCALE):
                dst.setLocation(x * m00 + y * m01, x * m10 + y * m11);
                break;
            case (APPLY_SHEAR | APPLY_TRANSLATE):
                dst.setLocation(y * m01 + m02, x * m10 + m12);
                break;
            case (APPLY_SHEAR):
                dst.setLocation(y * m01, x * m10);
                break;
            case (APPLY_SCALE | APPLY_TRANSLATE):
                dst.setLocation(x * m00 + m02, y * m11 + m12);
                break;
            case (APPLY_SCALE):
                dst.setLocation(x * m00, y * m11);
                break;
            case (APPLY_TRANSLATE):
                dst.setLocation(x + m02, y + m12);
                break;
            case (APPLY_IDENTITY):
                dst.setLocation(x, y);
                break;
            }
        }

        /* NOTREACHED */
    }

	public Set createSet(Set initSet) {
		return new HashSet(initSet);
	}
}
