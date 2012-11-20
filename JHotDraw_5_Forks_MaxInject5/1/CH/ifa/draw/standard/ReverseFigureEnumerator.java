/*
 * @(#)ReverseFigureEnumerator.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import CH.ifa.draw.util.ReverseListEnumerator;
import CH.ifa.draw.framework.*;

import java.util.Iterator;
import java.util.List;

/**
 * An Enumeration that enumerates a Collection of figures back (size-1) to front (0).
 *
 * @version <$CURRENT_VERSION$>
 */
public final class ReverseFigureEnumerator implements FigureEnumeration {
	private Iterator myIterator;
	private List myInitialList;

	public ReverseFigureEnumerator(List l) {
		myInitialList = l;
		reset();
	}

	/**
	 * Returns true if the enumeration contains more elements; false
	 * if its empty.
	 */
	public boolean hasNextFigure() {
		return myIterator.hasNext();
	}
    private static String mangleOverloadedMethod(String mangledName, Method m) {

        IDLTypesUtil idlTypesUtil = new IDLTypesUtil();

        // Start by appending the separator string
        String newMangledName = mangledName + OVERLOADED_TYPE_SEPARATOR;

        Class[] parameterTypes = m.getParameterTypes();

        for(int i = 0; i < parameterTypes.length; i++) {
            Class nextParamType = parameterTypes[i];

            if( i > 0 ) {
                newMangledName = newMangledName + OVERLOADED_TYPE_SEPARATOR;
            }
            IDLType idlType = classToIDLType(nextParamType);

            String moduleName = idlType.getModuleName();
            String memberName = idlType.getMemberName();

            String typeName = (moduleName.length() > 0) ?
                moduleName + UNDERSCORE + memberName : memberName;

            if( !idlTypesUtil.isPrimitive(nextParamType) &&
                (idlTypesUtil.getSpecialCaseIDLTypeMapping(nextParamType)
                 == null) &&
                isIDLKeyword(typeName) ) {
                typeName = mangleIDLKeywordClash(typeName);
            }

            typeName = mangleUnicodeChars(typeName);

            newMangledName = newMangledName + typeName;
        }

        return newMangledName;
    }

	/**
	 * Returns the next element casted as a figure of the enumeration. Calls to this
	 * method will enumerate successive elements.
	 * @exception java.util.NoSuchElementException If no more elements exist.
	 */
	public Figure nextFigure() {
		return (Figure)myIterator.next();
	}

	/**
	 * Reset the enumeration so it can be reused again. However, the
	 * underlying collection might have changed since the last usage
	 * so the elements and the order may vary when using an enumeration
	 * which has been reset.
	 */
	public void reset() {
		myIterator = new ReverseListEnumerator(myInitialList);
	}
}
