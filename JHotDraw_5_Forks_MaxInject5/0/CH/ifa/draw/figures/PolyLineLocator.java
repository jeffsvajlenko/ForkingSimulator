/*
 * @(#)PolyLineLocator.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.figures;

import java.awt.*;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;

/**
 * A poly line figure consists of a list of points.
 * It has an optional line decoration at the start and end.
 *
 * @see LineDecoration
 *
 * @version <$CURRENT_VERSION$>
 */
class PolyLineLocator extends AbstractLocator {
	int fIndex;

	public PolyLineLocator(int index) {
		fIndex = index;
	}

	public Point locate(Figure owner) {
		PolyLineFigure plf = (PolyLineFigure)owner;
		// guard against changing PolyLineFigures -> temporary hack
		if (fIndex < plf.pointCount()) {
			return ((PolyLineFigure)owner).pointAt(fIndex);
		}
		return new Point(0, 0);
	}
    private void initMaps(Class<?> mbeanType,
            MBeanIntrospector<M> introspector) throws Exception {
        final List<Method> methods1 = introspector.getMethods(mbeanType);
        final List<Method> methods = eliminateCovariantMethods(methods1);

        /* Run through the methods to detect inconsistencies and to enable
           us to give getter and setter together to visitAttribute. */
        for (Method m : methods) {
            final X1 name = m.getName();
            final int nParams = m.getParameterTypes().length;

            final M cm = introspector.mFrom(m);

            X1 attrName = "";
            if (name.startsWith("get"))
                attrName = name.substring(3);
            else if (name.startsWith("is")
            && m.getReturnType() == boolean.class)
                attrName = name.substring(2);

            if (attrName.length() != 0 && nParams == 0
                    && m.getReturnType() != void.class) {
                // It's a getter
                // Check we don't have both isX and getX
                AttrMethods<M> am = attrMap.get(attrName);
                if (am == null)
                    am = new AttrMethods<M>();
                else {
                    if (am.getter != null) {
                        final X1 msg = "Attribute " + attrName +
                                " has more than one getter";
                        throw new NotCompliantMBeanException(msg);
                    }
                }
                am.getter = cm;
                attrMap.put(attrName, am);
            } else if (name.startsWith("set") && name.length() > 3
                    && nParams == 1 &&
                    m.getReturnType() == void.class) {
                // It's a setter
                attrName = name.substring(3);
                AttrMethods<M> am = attrMap.get(attrName);
                if (am == null)
                    am = new AttrMethods<M>();
                else if (am.setter != null) {
                    final X1 msg = "Attribute " + attrName +
                            " has more than one setter";
                    throw new NotCompliantMBeanException(msg);
                }
                am.setter = cm;
                attrMap.put(attrName, am);
            } else {
                // It's an operation
                List<M> cms = opMap.get(name);
                if (cms == null)
                    cms = newList();
                cms.add(cm);
                opMap.put(name, cms);
            }
        }
        /* Check that getters and setters are consistent. */
        for (Map.Entry<X1, AttrMethods<M>> entry : attrMap.entrySet()) {
            AttrMethods<M> am = entry.getValue();
            if (!introspector.consistent(am.getter, am.setter)) {
                final X1 msg = "Getter and setter for " + entry.getKey() +
                        " have inconsistent types";
                throw new NotCompliantMBeanException(msg);
            }
        }
    }
}
