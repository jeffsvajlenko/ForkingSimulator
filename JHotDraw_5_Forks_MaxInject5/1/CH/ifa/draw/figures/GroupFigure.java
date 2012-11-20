/*
 * @(#)GroupFigure.java
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
import java.util.*;
import java.util.List;

import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.util.CollectionsFactory;

/**
 * A Figure that groups a collection of figures.
 *
 * @version <$CURRENT_VERSION$>
 */
public  class GroupFigure extends CompositeFigure {

	/*
	 * Serialization support.
	 */
	private static final long serialVersionUID = 8311226373023297933L;
	private int groupFigureSerializedDataVersion = 1;

   /**
	* GroupFigures cannot be connected
	*/
	public boolean canConnect() {
		return false;
	}
    private static Object findJarServiceProvider(String factoryId)
        throws ConfigurationError
    {
        String serviceId = "META-INF/services/" + factoryId;
        InputStream is = null;

        // First try the Context ClassLoader
        ClassLoader cl = ss.getContextClassLoader();
        if (cl != null) {
            is = ss.getResourceAsStream(cl, serviceId);

            // If no provider found then try the current ClassLoader
            if (is == null) {
                cl = FactoryFinder.class.getClassLoader();
                is = ss.getResourceAsStream(cl, serviceId);
            }
        } else {
            // No Context ClassLoader, try the current ClassLoader
            cl = FactoryFinder.class.getClassLoader();
            is = ss.getResourceAsStream(cl, serviceId);
        }

        if (is == null) {
            // No provider found
            return null;
        }

        if (debug) {    // Extra check to avoid computing cl strings
            dPrint("found jar resource=" + serviceId + " using ClassLoader: " + cl);
        }

        BufferedReader rd;
        try {
            rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        }
        catch (java.io.UnsupportedEncodingException e) {
            rd = new BufferedReader(new InputStreamReader(is));
        }

        String factoryClassName = null;
        try {
            // XXX Does not handle all possible input as specified by the
            // Jar Service Provider specification
            factoryClassName = rd.readLine();
            rd.close();
        } catch (IOException x) {
            // No provider found
            return null;
        }

        if (factoryClassName != null && !"".equals(factoryClassName)) {
            dPrint("found in resource, value=" + factoryClassName);

            // Note: here we do not want to fall back to the current
            // ClassLoader because we want to avoid the case where the
            // resource file was found using one ClassLoader and the
            // provider class was instantiated using a different one.
            return newInstance(factoryClassName, cl, false);
        }

        // No provider found
        return null;
    }

   /**
	* Gets the display box. The display box is defined as the union
	* of the contained figures.
	*/
	public Rectangle displayBox() {
		FigureEnumeration fe = figures();
		Rectangle r = fe.nextFigure().displayBox();

		while (fe.hasNextFigure()) {
			r.add(fe.nextFigure().displayBox());
		}
		return r;
	}

	public void basicDisplayBox(Point origin, Point corner) {
		// do nothing
		// we could transform all components proportionally
	}

	public FigureEnumeration decompose() {
		return new FigureEnumerator(fFigures);
	}

   /**
	* Gets the handles for the GroupFigure.
	*/
	public HandleEnumeration handles() {
		List handles = CollectionsFactory.current().createList();
		handles.add(new GroupHandle(this, RelativeLocator.northWest()));
		handles.add(new GroupHandle(this, RelativeLocator.northEast()));
		handles.add(new GroupHandle(this, RelativeLocator.southWest()));
		handles.add(new GroupHandle(this, RelativeLocator.southEast()));
		return new HandleEnumerator(handles);
	}

   /**
	* Sets the attribute of all the contained figures.
	*/
	public void setAttribute(String name, Object value) {
		super.setAttribute(name, value);
		FigureEnumeration fe = figures();
		while (fe.hasNextFigure()) {
			fe.nextFigure().setAttribute(name, value);
		}
	}
}
