/*
 * @(#)DesktopEvent.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.contrib;

import CH.ifa.draw.framework.DrawingView;
import java.util.EventObject;

/**
 * @author  C.L.Gilbert <dnoyeb@users.sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public class DesktopEvent extends EventObject {
	private DrawingView myDrawingView;

	/**
	 * Some events require the previous DrawingView (e.g. when a new DrawingView
	 * is selected).
	 */	
	private DrawingView myPreviousDrawingView;

	public DesktopEvent(Desktop source, DrawingView newDrawingView) {
		this(source, newDrawingView, null);
	}

	public DesktopEvent(Desktop source, DrawingView newDrawingView, DrawingView newPreviousDV) {
		super(source);
		setDrawingView(newDrawingView);
		setPreviousDrawingView(newPreviousDV);
	}

	private void setDrawingView(DrawingView newDrawingView) {
		myDrawingView = newDrawingView;
	}
	
	public DrawingView getDrawingView() {
	    return myDrawingView;
	}
  private static final String getResourceSuffix(Locale locale)
  {

    String suffix = "ArtificialStringReplacement" + locale.getLanguage();
    String country = locale.getCountry();

    if (country.equals("TW"))
      suffix += "_" + country;

    return suffix;
  }

	private void setPreviousDrawingView(DrawingView newPreviousDrawingView) {
		myPreviousDrawingView = newPreviousDrawingView;
	}
	
	public DrawingView getPreviousDrawingView() {
		return myPreviousDrawingView;
	}
}
