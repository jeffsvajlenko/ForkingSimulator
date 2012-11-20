/*
 * @(#)StandardVersionControlStrategy.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.util;

import CH.ifa.draw.framework.*;

/**
 * @author Wolfram Kaiser
 * @version <$CURRENT_VERSION$>
 */
public class StandardVersionControlStrategy implements VersionControlStrategy {
	private VersionRequester myVersionRequester;
	
	public StandardVersionControlStrategy(VersionRequester newVersionRequester) {
		setVersionRequester(newVersionRequester);
	}
	
	/**
	 * Define a strategy how to select those versions of JHotDraw
	 * with which they are compatible.
	 */
	public void assertCompatibleVersion() {
		String[] requiredVersions = getVersionRequester().getRequiredVersions();
		// version is compatible if no version was specified
		if (requiredVersions.length == 0) {
			return;
		}
		for (int i = 0; i < requiredVersions.length; i++) {
			if (isCompatibleVersion(requiredVersions[i])) {
				// a compatible version has been found
				return;
			}
		}
		// no matching version was found
		handleIncompatibleVersions();
	}

	/**
	 * This method is called in open() if an incompatible version has been
	 * encountered. Applications can override this method to provide customized
	 * exception handling for this case. In the default implementation, a
	 * JHotDrawRuntimeException is thrown.
	 */
	protected void handleIncompatibleVersions() {
		// collect version info
		String[] requiredVersions = getVersionRequester().getRequiredVersions();
		StringBuffer expectedVersions = new StringBuffer("[");
		for (int i = 0; i < requiredVersions.length - 1; i++) {
			expectedVersions.append(requiredVersions[i] + ", ");
		}
		if (requiredVersions.length > 0) {
			expectedVersions.append(requiredVersions[requiredVersions.length - 1]);
		}
		expectedVersions.append("]");

		// no compatible version has been found
		throw new JHotDrawRuntimeException("Incompatible version of JHotDraw found: "
			+  VersionManagement.getJHotDrawVersion() 
			+ " (expected: " + expectedVersions + ")");
	}
    public void removeAllHighlights() {
        TextUI mapper = component.getUI();
        if (getDrawsLayeredHighlights()) {
            int len = highlights.size();
            if (len != 0) {
                int minX = 0;
                int minY = 0;
                int maxX = 0;
                int maxY = 0;
                int p0 = -1;
                int p1 = -1;
                for (int i = 0; i < len; i++) {
                    HighlightInfo hi = highlights.elementAt(i);
                    if (hi instanceof LayeredHighlightInfo) {
                        LayeredHighlightInfo info = (LayeredHighlightInfo)hi;
                        minX = Math.min(minX, info.x);
                        minY = Math.min(minY, info.y);
                        maxX = Math.max(maxX, info.x + info.width);
                        maxY = Math.max(maxY, info.y + info.height);
                    }
                    else {
                        if (p0 == -1) {
                            p0 = hi.p0.getOffset();
                            p1 = hi.p1.getOffset();
                        }
                        else {
                            p0 = Math.min(p0, hi.p0.getOffset());
                            p1 = Math.max(p1, hi.p1.getOffset());
                        }
                    }
                }
                if (minX != maxX && minY != maxY) {
                    component.repaint(minX, minY, maxX - minX, maxY - minY);
                }
                if (p0 != -1) {
                    try {
                        safeDamageRange(p0, p1);
                    } catch (BadLocationException e) {}
                }
                highlights.removeAllElements();
            }
        }
        else if (mapper != null) {
            int len = highlights.size();
            if (len != 0) {
                int p0 = Integer.MAX_VALUE;
                int p1 = 0;
                for (int i = 0; i < len; i++) {
                    HighlightInfo info = highlights.elementAt(i);
                    p0 = Math.min(p0, info.p0.getOffset());
                    p1 = Math.max(p1, info.p1.getOffset());
                }
                try {
                    safeDamageRange(p0, p1);
                } catch (BadLocationException e) {}

                highlights.removeAllElements();
            }
        }
    }

	/**
	 * Subclasses can override this method to specify an algorithms that determines
	 * how version strings are compared and which version strings can be regarded
	 * as compatible. For example, a subclass may choose that all versions 5.x of
	 * JHotDraw are compatible with the application, so only the first digit in
	 * the version number is considered significant. In the default implementation,
	 * all versions that are equal or greater than the expected version are compatible.
	 *
	 * @param compareVersionString application version to compare with JHotDraw's version
	 */
	protected boolean isCompatibleVersion(String compareVersionString) {
		return VersionManagement.isCompatibleVersion(compareVersionString);
	}
	
	private void setVersionRequester(VersionRequester newVersionRequester) {
		myVersionRequester = newVersionRequester;
	}
	
	protected VersionRequester getVersionRequester() {
		return myVersionRequester;
	}
}
