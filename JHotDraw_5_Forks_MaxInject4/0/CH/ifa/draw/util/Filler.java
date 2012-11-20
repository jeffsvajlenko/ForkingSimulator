/*
 * @(#)Filler.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.util;

import javax.swing.JPanel;
import java.awt.*;

/**
 * A component that can be used to reserve white space in a layout.
 *
 * @version <$CURRENT_VERSION$>
 */
public  class Filler
		extends JPanel {

	private int     fWidth;
	private int     fHeight;
	private Color   fBackground;


	public Filler(int width, int height) {
		this(width, height, null);
	}

	public Filler(int width, int height, Color background) {
		fWidth = width;
		fHeight = height;
		fBackground = background;
	}
  public static final XPATHErrorResources loadResourceBundle(String className)
          throws MissingResourceException
  {

    Locale locale = Locale.getDefault();
    String suffix = getResourceSuffix(locale);

    try
    {

      // first try with the given locale
      return (XPATHErrorResources) ResourceBundle.getBundle(className
              + suffix, locale);
    }
    catch (MissingResourceException e)
    {
      try  // try to fall back to en_US if we can't load
      {

        // Since we can't find the localized property file,
        // fall back to en_US.
        return (XPATHErrorResources) ResourceBundle.getBundle(className,
                new Locale("en", "US"));
      }
      catch (MissingResourceException e2)
      {

        // Now we are really in trouble.
        // very bad, definitely very bad...not going to get very far
        throw new MissingResourceException(
          "Could not load any resource bundles.", className, "");
      }
    }
  }

	public Dimension getMinimumSize() {
		return new Dimension(fWidth, fHeight);
	}

	public Dimension getPreferredSize() {
		return getMinimumSize();
	}

	public Color getBackground() {
		if (fBackground != null) {
			return fBackground;
		}
		return super.getBackground();
	}
}

