/*
 * @(#)FigureTransferCommand.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import CH.ifa.draw.util.*;
import CH.ifa.draw.framework.*;

/**
 * Common base clase for commands that transfer figures
 * between a drawing and the clipboard.
 *
 * @version <$CURRENT_VERSION$>
 */
public abstract class FigureTransferCommand extends AbstractCommand {

	/**
	 * Constructs a drawing command.
	 * @param name the command name
	 * @param newDrawingEditor the DrawingEditor which manages the views
	 */
	protected FigureTransferCommand(String name, DrawingEditor newDrawingEditor) {
		super(name, newDrawingEditor);
	}

   /**
	* Deletes the selection from the drawing.
	*/
	protected void deleteFigures(FigureEnumeration fe) {
	   DeleteFromDrawingVisitor deleteVisitor = new DeleteFromDrawingVisitor(view().drawing());
		while (fe.hasNextFigure()) {
			fe.nextFigure().visit(deleteVisitor);
		}

		view().clearSelection();
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
	* Copies the FigureEnumeration to the clipboard.
	*/
	protected void copyFigures(FigureEnumeration fe, int figureCount) {
		Clipboard.getClipboard().setContents(new StandardFigureSelection(fe, figureCount));
	}

   /**
	* Inserts an enumeration of figures and translates them by the
	* given offset.
	* @todo mrfloppy to investigate making this protected.  Looks like it would
	*       be no problem to me.  It was package scope.  I thought it safer to
	*       make it less restrictive just incase their was a reason for the
	*       package scope I didn't know about. dnoyeb.
	*       Bug - [ 673096 ] FigureTransferCommand has a wrong method
	*/
	public FigureEnumeration insertFigures(FigureEnumeration fe, int dx, int dy) {
		return view().insertFigures(fe, dx, dy, false);
	}
}


